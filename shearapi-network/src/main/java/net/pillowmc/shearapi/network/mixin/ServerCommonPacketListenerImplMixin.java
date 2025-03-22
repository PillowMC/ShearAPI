package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.payload.MinecraftRegisterPayload;
import net.neoforged.neoforge.network.payload.MinecraftUnregisterPayload;
import net.neoforged.neoforge.network.payload.ModdedNetworkQueryPayload;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.pillowmc.shearapi.network.injection.IServerCommonPacketListenerExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    // This should be in ServerConfigurationPacketListenerImplMixin...
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayloadHead(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (!((Object)this instanceof ServerConfigurationPacketListenerImpl self)) return;
        if (packet.payload() instanceof MinecraftRegisterPayload payload) {
            self.shearapi$setConnectionType(self.getConnectionType().withMinecraftRegisterPayload());
            NetworkRegistry.getInstance().onMinecraftRegister(self, payload.newChannels());
            return;
        }

        if (packet.payload() instanceof MinecraftUnregisterPayload payload) {
            self.shearapi$setConnectionType(self.getConnectionType().withMinecraftRegisterPayload());
            NetworkRegistry.getInstance().onMinecraftUnregister(self, payload.forgottenChannels());
            return;
        }

        if (packet.payload() instanceof ModdedNetworkQueryPayload payload) {
            self.shearapi$setConnectionType(self.getConnectionType().withNeoForgeQueryPayload());
            NetworkRegistry.getInstance().onModdedConnectionDetectedAtServer(
                    self,
                    payload.configuration(),
                    payload.play()
            );
            return;
        }

        if (self.shearapi$isHandlingModdedConfigurationPhase()) {
            NetworkRegistry.getInstance().onModdedPacketAtServer(self, packet);
            ci.cancel();
        }

    }

    @Inject(method = "handleCustomPayload", at = @At("TAIL"))
    private void handleCustomPayloadTail(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if ((Object)this instanceof ServerConfigurationPacketListenerImpl self) {
            NetworkRegistry.getInstance().onModdedPacketAtServer(self, packet);
        }
    }
}
