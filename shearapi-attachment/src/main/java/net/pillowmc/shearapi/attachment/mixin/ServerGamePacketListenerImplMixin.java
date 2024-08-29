package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Inject(method = "signBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addTagElement(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectSignBook(FilteredText filteredText, List<FilteredText> list, int i, CallbackInfo ci, ItemStack itemstack, ItemStack itemstack2) {
        AttachmentUtils.copyStackAttachments(itemstack, itemstack2);
    }
}
