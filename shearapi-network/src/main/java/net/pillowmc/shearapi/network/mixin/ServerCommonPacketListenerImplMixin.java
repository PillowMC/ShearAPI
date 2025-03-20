package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.pillowmc.shearapi.network.injection.IServerCommonPacketListenerExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin implements IServerCommonPacketListenerExtension {

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Shadow
    @Final
    protected Connection connection;

    @Shadow
    @Final
    protected MinecraftServer server;

    @Shadow
    public abstract void send(Packet<?> packet);

    @Shadow
    public abstract void send(Packet<?> packet, @Nullable PacketSendListener packetSendListener);

    @Override
    public ReentrantBlockableEventLoop<?> getMainThreadEventLoop() {
        return server;
    }

    @Override
    public abstract ConnectionType getConnectionType();

    @Override
    public void send(CustomPacketPayload packetPayload) {
        this.send(new ClientboundCustomPayloadPacket(packetPayload));
    }

    @Override
    public void send(CustomPacketPayload packetPayload, @Nullable PacketSendListener listener) {
        this.send(new ClientboundCustomPayloadPacket(packetPayload), listener);
    }

    @Override
    public boolean isVanillaConnection() {
        return getConnectionType().isVanilla();
    }

    @Override
    public boolean isConnected(ResourceLocation payloadId) {
        return NetworkRegistry.getInstance().isConnected((ServerCommonPacketListener) (Object)this, payloadId);
    }

    @Override
    public boolean isConnected(CustomPacketPayload payload) {
        return this.isConnected(payload.id());
    }
}
