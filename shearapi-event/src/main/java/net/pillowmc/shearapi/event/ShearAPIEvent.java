package net.pillowmc.shearapi.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.pillowmc.shearapi.runtime.IModBusEvent;

import java.util.function.Consumer;

public class ShearAPIEvent implements ModInitializer {
    public static final IEventBus EVENT_BUS = BusBuilder.builder().classChecker(eventType -> {
        if (IModBusEvent.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException("IModBusEvent events are not allowed on the common NeoForge bus! Use a mod bus instead.");
        }
    }).build();

    @Override
    public void onInitialize() {
        FabricLoader.getInstance()
            .getEntrypoints("shearapi-event:game_bus_subscriber", Object.class).forEach(EVENT_BUS::register);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricLoader.getInstance()
                .getEntrypoints("shearapi-event:client_game_bus_subscriber", Object.class)
                .forEach(EVENT_BUS::register);
        }
    }
}
