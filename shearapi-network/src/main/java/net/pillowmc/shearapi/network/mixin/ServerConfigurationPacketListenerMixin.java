package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.pillowmc.shearapi.network.injection.IServerConfigurationPacketListenerExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerConfigurationPacketListener.class)
public interface ServerConfigurationPacketListenerMixin extends IServerConfigurationPacketListenerExtension {

}
