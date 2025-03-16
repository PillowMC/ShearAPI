package net.pillowmc.shearapi.withpillow;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.pillowmc.shearapi.runtime.IModBusEvent;
import net.pillowmc.shearapi.withoutpillow.ShearAPIWithoutPillow;

public class ShearAPIWithPillow extends ShearAPIWithoutPillow {

    public static void onInitialize() {

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
