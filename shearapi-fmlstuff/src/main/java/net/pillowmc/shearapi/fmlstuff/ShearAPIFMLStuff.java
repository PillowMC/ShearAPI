package net.pillowmc.shearapi.fmlstuff;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.configuration.SyncConfig;
import net.neoforged.neoforge.network.configuration.SyncRegistries;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.IConfigurationPayloadHandler;
import net.neoforged.neoforge.network.payload.ConfigFilePayload;
import net.neoforged.neoforge.network.payload.FrozenRegistryPayload;
import net.neoforged.neoforge.network.payload.FrozenRegistrySyncCompletedPayload;
import net.neoforged.neoforge.network.payload.FrozenRegistrySyncStartPayload;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;
import net.pillowmc.shearapi.withpillow.ShearAPIWithPillow;

public class ShearAPIFMLStuff {
    public static Class<ShearAPIFMLStuff> clazz = ShearAPIFMLStuff.class;
    public static IConfigurationPayloadHandler<FrozenRegistrySyncCompletedPayload> clientHandleRegistrySyncCompleted;

    @SubscribeEvent
    public static void onGameConfigurationEvent(OnGameConfigurationEvent event) {
        if (event.getListener().isConnected(FrozenRegistrySyncStartPayload.ID) &&
                event.getListener().isConnected(FrozenRegistryPayload.ID) &&
                event.getListener().isConnected(FrozenRegistrySyncCompletedPayload.ID)) {
            event.register(new SyncRegistries());
        }
        if (event.getListener().isConnected(ConfigFilePayload.ID) && ShearAPIRuntime.getRuntime() instanceof ShearAPIWithPillow) {
            event.register(new SyncConfig(event.getListener()));
        }
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
                        FrozenRegistrySyncCompletedPayload.ID,
                        FrozenRegistrySyncCompletedPayload::new,
                        handlers -> handlers.client(clientHandleRegistrySyncCompleted)
                                .server(ShearAPIFMLStuff::handleRegistrySyncCompleted));
    }

    public static void handleRegistrySyncCompleted(FrozenRegistrySyncCompletedPayload payload, ConfigurationPayloadContext context) {
        context.taskCompletedHandler().onTaskCompleted(SyncRegistries.TYPE);
    }
}
