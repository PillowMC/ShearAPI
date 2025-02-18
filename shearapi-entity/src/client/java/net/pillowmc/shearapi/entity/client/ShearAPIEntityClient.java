package net.pillowmc.shearapi.entity.client;

import net.fabricmc.api.ClientModInitializer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.payload.AdvancedAddEntityPayload;
import net.pillowmc.shearapi.entity.client.network.ClientPayloadHandler;
import net.pillowmc.shearapi.event.ShearAPIEvent;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;

public class ShearAPIEntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ShearAPIEvent.EVENT_BUS.addListener((RegisterPayloadHandlerEvent event) -> {
            event.registrar(ShearAPIRuntime.MOD_ID)
                    .versioned(ShearAPIVersion.getSpec())
                    .optional()
                    .play(
                        AdvancedAddEntityPayload.ID,
                        AdvancedAddEntityPayload::new,
                        handlers -> handlers.client(ClientPayloadHandler::handle)
                    );
        });
    }
}
