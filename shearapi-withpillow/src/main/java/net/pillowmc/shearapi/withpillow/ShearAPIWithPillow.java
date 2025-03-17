package net.pillowmc.shearapi.withpillow;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.configuration.SyncConfig;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.payload.ConfigFilePayload;
import net.pillowmc.shearapi.runtime.IModBusEvent;
import net.pillowmc.shearapi.withoutpillow.ShearAPIWithoutPillow;

public class ShearAPIWithPillow extends ShearAPIWithoutPillow {
    public static Class<ShearAPIWithPillow> clazz = ShearAPIWithPillow.class;

    @SubscribeEvent
    public static void onGameConfigurationEvent(OnGameConfigurationEvent event) {
        if (event.getListener().isConnected(ConfigFilePayload.ID)) {
            event.register(new SyncConfig(event.getListener()));
        }
    }

    @Override
    public boolean isProduction() {
        return FMLLoader.isProduction();
    }

    @Override
    public boolean isLoadingStateVaild() {
        return ModLoader.isLoadingStateValid();
    }

    private <T extends Event & IModBusEvent, NEOT extends Event & net.pillowmc.shearapi.runtime.IModBusEvent> NEOT castEvent(T event) {
        return (NEOT) event;
    }

    @Override
    public <T extends Event & IModBusEvent> void postModBusEventWrapContainerInModOrder(T event) {
        super.postModBusEventWrapContainerInModOrder(event);
        ModLoader.get().postEventWrapContainerInModOrder(castEvent(event));
    }

    @Override
    public <T extends Event & IModBusEvent> void postModBusEvent(T event) {
        super.postModBusEvent(event);
        ModLoader.get().postEvent(castEvent(event));
    }

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
