package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {
    @ModifyVariable(method = "writeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeNbt(Lnet/minecraft/nbt/Tag;)Lnet/minecraft/network/FriendlyByteBuf;"), index = 3)
    private CompoundTag modifyWriteItem(CompoundTag value, ItemStack itemStack) {
        return AttachmentInternals.addAttachmentsToTag(value, itemStack, false);
    }
    @Inject(method = "readItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;I)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectReadItem(CallbackInfoReturnable<ItemStack> cir, Item item, int i) {
        cir.setReturnValue(AttachmentInternals.reconstructItemStack(item, i, ((FriendlyByteBuf)(Object)this).readNbt()));
    }
}
