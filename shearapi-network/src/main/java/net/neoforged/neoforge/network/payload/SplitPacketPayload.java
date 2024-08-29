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
 * A payload that is used to split a packet into multiple payloads.
 * <p>
 * This single payload will contain a slice of the original packet.
 * </p>
 * 
 * @param payload The slice of the original packet.
 */
@ApiStatus.Internal
public record SplitPacketPayload(byte[] payload) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(ShearAPIRuntime.MOD_ID, "split");

    public SplitPacketPayload(FriendlyByteBuf buf) {
        this(buf.readByteArray());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeByteArray(payload);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
