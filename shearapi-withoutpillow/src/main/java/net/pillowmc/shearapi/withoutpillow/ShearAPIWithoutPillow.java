package net.pillowmc.shearapi.withoutpillow;

import net.neoforged.bus.api.IEventBus;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.Event;
import net.pillowmc.shearapi.runtime.IModBusEvent;
import net.pillowmc.shearapi.runtime.IRuntime;

import java.util.Optional;

public class ShearAPIWithoutPillow implements IRuntime {
    private final IEventBus MODBUS = BusBuilder.builder().markerType(IModBusEvent.class).build();

    @Override
    public boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isLoadingStateVaild() {
        return true;
    }

    @Override
    public IEventBus getModBus() {
        return MODBUS;
    }

    @Override
    public <T extends Event & IModBusEvent> void postModBusEventWrapContainerInModOrder(T event) {
        MODBUS.post(event);
    }

    @Override
    public <T extends Event & IModBusEvent> void postModBusEvent(T event) {
        MODBUS.post(event);
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public Optional<String> getModDisplayName(String modid) {
        return FabricLoader.getInstance().getModContainer(modid).map(m -> m.getMetadata().getName());
    }
}
