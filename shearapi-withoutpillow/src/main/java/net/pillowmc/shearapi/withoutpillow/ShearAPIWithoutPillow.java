package net.pillowmc.shearapi.withoutpillow;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.Event;
import net.pillowmc.shearapi.runtime.IModBusEvent;
import net.pillowmc.shearapi.runtime.IRuntime;

public class ShearAPIWithoutPillow implements IRuntime {
    private final IEventBus MODBUS = BusBuilder.builder().markerType(IModBusEvent.class).build();

    @Override
    public <T extends Event & IModBusEvent> void postModBusEventWrapContainerInModOrder(T event) {
        MODBUS.post(event);
    }

    @Override
    public <T extends Event & IModBusEvent> void postModBusEvent(T event) {
        MODBUS.post(event);
    }

    @Override
    public void postForgeBusEvent(Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'postForgeBusEvent'");
    }
    
}
