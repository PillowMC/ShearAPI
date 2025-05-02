package net.pillowmc.shearapi.runtime;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;

import java.util.Optional;

public interface IRuntime {
    boolean isLoadingStateVaild();
    boolean isProduction();
    <T extends Event & IModBusEvent> void postModBusEventWrapContainerInModOrder(T event);
    <T extends Event & IModBusEvent> void postModBusEvent(T event);
    IEventBus getModBus();
    boolean isModLoaded(String modid);
    Optional<String> getModDisplayName(String modid);
}
