/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.pillowmc.shearapi.network.injection;

import javax.annotation.Nullable;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/**
 * Extension class for {@link net.minecraft.network.protocol.common.ServerCommonPacketListener}
 * <p>
 * This interface and its default methods is used to make sending custom payloads easier.
 * </p>
 */
public interface IServerCommonPacketListenerExtension {

    /**
     * Sends a custom payload to the client which this listener is attached to.
     *
     * @param packetPayload The payload to send
     */
    default void send(CustomPacketPayload packetPayload) {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * Sends a custom payload to the client which this listener is attached to.
     *
     * @param packetPayload The payload to send
     * @param listener      The listener to call when the packet is sent
     */
    default void send(CustomPacketPayload packetPayload, @Nullable PacketSendListener listener) {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * {@return the connection this listener is attached to}
     */
    Connection getConnection();

    /**
     * {@return the main thread event loop}
     */
    ReentrantBlockableEventLoop<?> getMainThreadEventLoop();

    /**
     * {@return true if the connection is to a vanilla client}
     *
     * @deprecated Use {@link #getConnectionType()} instead
     */
    @Deprecated(forRemoval = true)
    default boolean isVanillaConnection() {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * {@return true if the custom payload type with the given id is usable by this connection}
     *
     * @param payloadId The payload id to check
     */
    default boolean isConnected(final ResourceLocation payloadId) {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * {@return true if the custom payload is usable by this connection}
     *
     * @param payload The payload to check
     */
    default boolean isConnected(final CustomPacketPayload payload) {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * {@return the connection type of the connection}
     */
    ConnectionType getConnectionType();
}
