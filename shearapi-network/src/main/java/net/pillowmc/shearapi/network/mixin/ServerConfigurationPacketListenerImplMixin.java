package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.pillowmc.shearapi.network.injection.IServerConfigurationPacketListenerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerImplMixin implements IServerConfigurationPacketListenerExtension {

    @Override
    @Shadow
    public abstract void finishCurrentTask(ConfigurationTask.Type task);

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public ReentrantBlockableEventLoop<?> getMainThreadEventLoop() {
        return null;
    }

    @Override
    public ConnectionType getConnectionType() {
        return null;
    }
}
