/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.pillowmc.shearapi.registries.datamaps.client;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.datamaps.network.KnownRegistryDataMapsPayload;
import net.neoforged.neoforge.registries.datamaps.network.KnownRegistryDataMapsReplyPayload;
import net.neoforged.neoforge.registries.datamaps.network.RegistryDataMapSyncPayload;
import net.pillowmc.shearapi.registries.datamaps.DataMapManager;
import net.pillowmc.shearapi.registries.datamaps.injections.IBaseMappedRegistryInjection;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public class ClientDataMapManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    @ApiStatus.Internal
    public void shearapi$fabricClientInitialize(){
        ShearAPIRuntime.getRuntime().getModBus().addListener((RegisterPayloadHandlerEvent event) -> {
            final IPayloadRegistrar registrar = event.registrar(ShearAPIRuntime.MOD_ID)
                    .versioned(ShearAPIVersion.getSpec())
                    .optional();
            registrar
                    .configuration(
                            KnownRegistryDataMapsPayload.ID,
                            KnownRegistryDataMapsPayload::new,
                            handlers -> handlers.client(ClientDataMapManager::handleKnownDataMaps))
                    .play(
                            RegistryDataMapSyncPayload.ID,
                            RegistryDataMapSyncPayload::decode,
                            handlers -> handlers.client(ClientDataMapManager::handleDataMapSync));
        });
    };

    public static <R> void handleDataMapSync(final RegistryDataMapSyncPayload<R> payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            final IBaseMappedRegistryInjection<R> registry = (IBaseMappedRegistryInjection<R>) Minecraft.getInstance().level.registryAccess()
                    .registryOrThrow(payload.registryKey());
            registry.shearAPI$getDataMaps().clear();
            payload.dataMaps().forEach((attachKey, maps) -> registry.shearAPI$getDataMaps().put(DataMapManager.getDataMap(payload.registryKey(), attachKey), Collections.unmodifiableMap(maps)));
        }).exceptionally(ex -> {
            context.packetHandler().disconnect(Component.translatable("neoforge.network.data_maps.failed", payload.registryKey().location(), ex.getMessage()));
            LOGGER.error("Failed to handle registry data map sync: ", ex);
            return null;
        });
    }

    public static void handleKnownDataMaps(final KnownRegistryDataMapsPayload payload, final ConfigurationPayloadContext context) {
        record MandatoryEntry(ResourceKey<Registry<?>> registry, ResourceLocation id) {}
        final Set<MandatoryEntry> ourMandatory = new HashSet<>();
        DataMapManager.getDataMaps().forEach((reg, values) -> values.values().forEach(attach -> {
            if (attach.mandatorySync()) {
                ourMandatory.add(new MandatoryEntry(reg, attach.id()));
            }
        }));

        final Set<MandatoryEntry> theirMandatory = new HashSet<>();
        payload.dataMaps().forEach((reg, values) -> values.forEach(attach -> {
            if (attach.mandatory()) {
                theirMandatory.add(new MandatoryEntry(reg, attach.id()));
            }
        }));

        final List<Component> messages = new ArrayList<>();
        final var missingOur = Sets.difference(ourMandatory, theirMandatory);
        if (!missingOur.isEmpty()) {
            messages.add(Component.translatable("neoforge.network.data_maps.missing_our", Component.literal(missingOur.stream()
                    .map(e -> e.id() + " (" + e.registry().location() + ")")
                    .collect(Collectors.joining(", "))).withStyle(ChatFormatting.GOLD)));
        }

        final var missingTheir = Sets.difference(theirMandatory, ourMandatory);
        if (!missingTheir.isEmpty()) {
            messages.add(Component.translatable("neoforge.network.data_maps.missing_their", Component.literal(missingTheir.stream()
                    .map(e -> e.id() + " (" + e.registry().location() + ")")
                    .collect(Collectors.joining(", "))).withStyle(ChatFormatting.GOLD)));
        }

        if (!messages.isEmpty()) {
            MutableComponent message = Component.empty();
            final var itr = messages.iterator();
            while (itr.hasNext()) {
                message = message.append(itr.next());
                if (itr.hasNext()) {
                    message = message.append("\n");
                }
            }

            context.packetHandler().disconnect(message);
            return;
        }

        final var known = new HashMap<ResourceKey<Registry<?>>, Collection<ResourceLocation>>();
        DataMapManager.getDataMaps().forEach((key, vals) -> known.put(key, vals.keySet()));
        context.replyHandler().send(new KnownRegistryDataMapsReplyPayload(known));
    }
}
