package net.pillowmc.shearapi.entity.client;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.payload.AdvancedAddEntityPayload;
import net.pillowmc.shearapi.entity.client.network.ClientPayloadHandler;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;


public class ShearAPIEntityClient {
    public void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
        event.registrar(ShearAPIRuntime.MOD_ID)
            .versioned(ShearAPIVersion.getSpec())
            .optional()
            .play(
                AdvancedAddEntityPayload.ID,
                AdvancedAddEntityPayload::new,
                handlers -> handlers.client(ClientPayloadHandler::handle)
            );
    }
}
