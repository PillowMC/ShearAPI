package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.payload.MinecraftRegisterPayload;
import net.neoforged.neoforge.network.payload.MinecraftUnregisterPayload;
import net.neoforged.neoforge.network.payload.ModdedNetworkQueryPayload;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.pillowmc.shearapi.network.injection.IServerConfigurationPacketListenerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerImplMixin implements IServerConfigurationPacketListenerExtension {
    private ConnectionType connectionType = ConnectionType.VANILLA;
    private boolean isHandlingModdedConfigurationPhase = false;

    @Override
    @Shadow
    public abstract void finishCurrentTask(ConfigurationTask.Type task);

    @Override
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public void shearapi$setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public boolean shearapi$isHandlingModdedConfigurationPhase() {
        return isHandlingModdedConfigurationPhase;
    }

    @Override
    public void shearapi$setHandlingModdedConfigurationPhase() {
        isHandlingModdedConfigurationPhase = true;
    }

    @Inject(method = "handleConfigurationFinished", at = @At("HEAD"))
    private void handleConfigurationFinished(ServerboundFinishConfigurationPacket serverboundFinishConfigurationPacket, CallbackInfo ci) {
        if (this.connectionType == ConnectionType.OTHER) {
            NetworkRegistry.getInstance().onModdedConnectionDetectedAtServer(
                    (ServerConfigurationPacketListener) this,
                    Set.of(),
                    Set.of()
            );
        }
        NetworkRegistry.getInstance().onConfigurationFinished((ServerConfigurationPacketListener) this);
    }
}
