package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.pillowmc.shearapi.network.injection.IFriendlyByteBufExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FriendlyByteBuf.class)
public class FriendlyByteBufMixin implements IFriendlyByteBufExtension {
}
