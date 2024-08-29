package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.pillowmc.shearapi.network.injection.IServerCommonPacketListenerExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerCommonPacketListener.class)
public interface ServerCommonPacketListenerMixin extends IServerCommonPacketListenerExtension {

}
