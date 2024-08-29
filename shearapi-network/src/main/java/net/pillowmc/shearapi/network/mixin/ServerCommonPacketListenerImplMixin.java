package net.pillowmc.shearapi.network.mixin;

import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.pillowmc.shearapi.network.injection.IServerCommonPacketListenerExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin implements IServerCommonPacketListenerExtension {

}
