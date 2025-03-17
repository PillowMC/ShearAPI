/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.configuration.SyncRegistries;
import net.neoforged.neoforge.network.configuration.SyncTierSortingRegistry;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.payload.FrozenRegistryPayload;
import net.neoforged.neoforge.network.payload.FrozenRegistrySyncCompletedPayload;
import net.neoforged.neoforge.network.payload.FrozenRegistrySyncStartPayload;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ConfigurationInitialization {
    public static Class<ConfigurationInitialization> clazz = ConfigurationInitialization.class;
    @SubscribeEvent
    private static void configureModdedClient(OnGameConfigurationEvent event) {
        if (event.getListener().isConnected(FrozenRegistrySyncStartPayload.ID) &&
                event.getListener().isConnected(FrozenRegistryPayload.ID) &&
                event.getListener().isConnected(FrozenRegistrySyncCompletedPayload.ID)) {
            event.register(new SyncRegistries());
        }

        //These two can always be registered they detect the listener connection type internally and will skip themselves.
        event.register(new SyncTierSortingRegistry(event.getListener()));
    }
}
