package net.pillowmc.shearapi.fmlstuff.client;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.payload.ConfigFilePayload;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;
import net.pillowmc.shearapi.withpillow.ShearAPIWithPillow;

import java.util.Optional;

public class ShearAPIFMLStuffClient {
    public static Class<ShearAPIFMLStuffClient> clazz = ShearAPIFMLStuffClient.class;
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
}
