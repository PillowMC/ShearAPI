package net.pillowmc.shearapi.runtime;

import net.neoforged.bus.api.Event;

public interface IRuntime {
    <T extends Event & IModBusEvent> void postModBusEventWrapContainerInModOrder(T event);
    <T extends Event & IModBusEvent> void postModBusEvent(T event);
    void postForgeBusEvent(Event event);
}
