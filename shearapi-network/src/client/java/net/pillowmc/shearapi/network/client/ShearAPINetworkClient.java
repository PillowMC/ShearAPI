package net.pillowmc.shearapi.network.client;

import net.fabricmc.api.ClientModInitializer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handlers.ServerPayloadHandler;
import net.neoforged.neoforge.network.payload.*;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;

public class ShearAPINetworkClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ShearAPIRuntime.getRuntime().getModBus().addListener((final RegisterPayloadHandlerEvent event) -> {
            final IPayloadRegistrar registrar = event.registrar(ShearAPIRuntime.MOD_ID)
                    .versioned(ShearAPIVersion.getSpec())
                    .optional();
            registrar
                    .common(
                            TierSortingRegistryPayload.ID,
                            TierSortingRegistryPayload::new,
                            handlers -> handlers.client(ClientPayloadHandler.getInstance()::handle))
                    .configuration(
                            FrozenRegistrySyncStartPayload.ID,
                            FrozenRegistrySyncStartPayload::new,
                            handlers -> handlers.client(ClientPayloadHandler.getInstance()::handle))
                    .configuration(
                            FrozenRegistryPayload.ID,
                            FrozenRegistryPayload::new,
                            handlers -> handlers.client(ClientPayloadHandler.getInstance()::handle))
                    .configuration(
                            FrozenRegistrySyncCompletedPayload.ID,
                            FrozenRegistrySyncCompletedPayload::new,
                            handlers -> handlers.client(ClientPayloadHandler.getInstance()::handle)
                                    .server(ServerPayloadHandler.getInstance()::handle))
                    .configuration(
                            TierSortingRegistrySyncCompletePayload.ID,
                            TierSortingRegistrySyncCompletePayload::new,
                            handlers -> handlers.server(ServerPayloadHandler.getInstance()::handle))
                    .play(
                            AdvancedOpenScreenPayload.ID,
                            AdvancedOpenScreenPayload::new,
                            handlers -> handlers.client(ClientPayloadHandler.getInstance()::handle))
                    .play(
                            AuxiliaryLightDataPayload.ID,
                            AuxiliaryLightDataPayload::new,
                            handlers -> handlers.client(ClientPayloadHandler.getInstance()::handle))
                    .play(AdvancedContainerSetDataPayload.ID,
                            AdvancedContainerSetDataPayload::new,
                            handlers -> handlers.client(ClientPayloadHandler.getInstance()::handle));
        });
    }
}
