package net.pillowmc.shearapi.fmlstuff;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.configuration.SyncConfig;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.payload.ConfigFilePayload;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.withpillow.ShearAPIWithPillow;

public class ShearAPIFMLStuff {
    public static Class<ShearAPIFMLStuff> clazz = ShearAPIFMLStuff.class;

    @SubscribeEvent
    public static void onGameConfigurationEvent(OnGameConfigurationEvent event) {
        if (event.getListener().isConnected(ConfigFilePayload.ID) && ShearAPIRuntime.getRuntime() instanceof ShearAPIWithPillow) {
            event.register(new SyncConfig(event.getListener()));
        }
    }
}
