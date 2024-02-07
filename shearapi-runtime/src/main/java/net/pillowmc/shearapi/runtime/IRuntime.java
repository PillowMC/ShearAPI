package net.pillowmc.shearapi.runtime;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;

public interface IRuntime {
    <T extends Event & IModBusEvent> void postModBusEventWrapContainerInModOrder(T event);
    <T extends Event & IModBusEvent> void postModBusEvent(T event);
    IEventBus getModBus();
}
