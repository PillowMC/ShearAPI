package net.pillowmc.shearapi.fmlstuff.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.payload.ConfigFilePayload;
import net.neoforged.neoforge.network.payload.FrozenRegistryPayload;
import net.neoforged.neoforge.network.payload.FrozenRegistrySyncCompletedPayload;
import net.neoforged.neoforge.network.payload.FrozenRegistrySyncStartPayload;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.pillowmc.shearapi.fmlstuff.ShearAPIFMLStuff;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;
import net.pillowmc.shearapi.withpillow.ShearAPIWithPillow;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ShearAPIFMLStuffClient implements ClientModInitializer {
    public static Class<ShearAPIFMLStuffClient> clazz = ShearAPIFMLStuffClient.class;
    private static final Map<ResourceLocation, RegistrySnapshot> synchronizedRegistries = Maps.newConcurrentMap();
    private static final Set<ResourceLocation> toSynchronize = Sets.newConcurrentHashSet();

    @Override
    public void onInitializeClient() {
        ShearAPIFMLStuff.clientHandleRegistrySyncCompleted = ShearAPIFMLStuffClient::handleRegistrySyncCompleted;
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlerEvent(RegisterPayloadHandlerEvent event) {
        if (!(ShearAPIRuntime.getRuntime() instanceof ShearAPIWithPillow)) {
            return;
        }
        final IPayloadRegistrar registrar = event.registrar(ShearAPIRuntime.MOD_ID)
                .versioned(ShearAPIVersion.getSpec())
                .optional();
        registrar
                .configuration(
                        FrozenRegistrySyncStartPayload.ID,
                        FrozenRegistrySyncStartPayload::new,
                        handlers -> handlers.client(ShearAPIFMLStuffClient::handleRegistrySyncStart))
                .configuration(
                        FrozenRegistryPayload.ID,
                        FrozenRegistryPayload::new,
                        handlers -> handlers.client(ShearAPIFMLStuffClient::handleFrozenRegistry))
                .configuration(
                        ConfigFilePayload.ID,
                        ConfigFilePayload::new,
                        handlers -> handlers.client(ShearAPIFMLStuffClient::handleConfigFile));
    }

    private static void handleConfigFile(ConfigFilePayload payload, IPayloadContext context) {
        if (!Minecraft.getInstance().isLocalServer()) {
            Optional.ofNullable(
                    ConfigTracker.INSTANCE.fileMap().get(payload.fileName())
            ).ifPresent(mc -> mc.acceptSyncedConfig(payload.contents()));
        }
    }

    public static void handleFrozenRegistry(FrozenRegistryPayload payload, ConfigurationPayloadContext context) {
        synchronizedRegistries.put(payload.registryName(), payload.snapshot());
        toSynchronize.remove(payload.registryName());
    }

    public static void handleRegistrySyncStart(FrozenRegistrySyncStartPayload payload, ConfigurationPayloadContext context) {
        toSynchronize.addAll(payload.toAccess());
        synchronizedRegistries.clear();
    }

    public static void handleRegistrySyncCompleted(FrozenRegistrySyncCompletedPayload payload, ConfigurationPayloadContext context) {
        if (!toSynchronize.isEmpty()) {
            context.packetHandler().disconnect(Component.translatable("neoforge.network.registries.sync.missing", this.toSynchronize.stream().map(Object::toString).collect(Collectors.joining(", "))));
            return;
        }

        context.workHandler().submitAsync(() -> {
            //This method normally returns missing entries, but we just accept what the server send us and ignore the rest.
            Set<ResourceKey<?>> keysUnknownToClient = RegistryManager.applySnapshot(synchronizedRegistries, false, false);
            if (!keysUnknownToClient.isEmpty()) {
                context.packetHandler().disconnect(Component.translatable("neoforge.network.registries.sync.server-with-unknown-keys", keysUnknownToClient.stream().map(Object::toString).collect(Collectors.joining(", "))));
                return;
            }

            toSynchronize.clear();
            synchronizedRegistries.clear();
        }).exceptionally(e -> {
            context.packetHandler().disconnect(Component.translatable("neoforge.network.registries.sync.failed", e.getMessage()));
            return null;
        }).thenAccept(v -> {
            context.replyHandler().send(new FrozenRegistrySyncCompletedPayload());
        });
    }
}
