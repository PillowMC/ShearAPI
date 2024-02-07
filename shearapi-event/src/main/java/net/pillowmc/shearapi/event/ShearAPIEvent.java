package net.pillowmc.shearapi.event;

import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.IEventBus;
import net.pillowmc.shearapi.runtime.IModBusEvent;

public class ShearAPIEvent {
    public static final IEventBus EVENT_BUS = BusBuilder.builder().startShutdown().classChecker(eventType -> {
        if (IModBusEvent.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException("IModBusEvent events are not allowed on the common NeoForge bus! Use a mod bus instead.");
        }
    }).build();
}
