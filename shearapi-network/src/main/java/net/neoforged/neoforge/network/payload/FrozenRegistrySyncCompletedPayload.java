/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import org.jetbrains.annotations.ApiStatus;

/**
 * This payload is sent to the client when the server has finished sending all the frozen registries.
 */
@ApiStatus.Internal
public record FrozenRegistrySyncCompletedPayload() implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(ShearAPIRuntime.MOD_ID, "frozen_registry_sync_completed");

    public FrozenRegistrySyncCompletedPayload(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buf) {}

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
