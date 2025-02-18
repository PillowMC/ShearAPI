package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "areMergable", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    private static void injectAreMergable(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<Boolean> cir) {
        if (!itemStack.areAttachmentsCompatible(itemStack2)) {
            cir.setReturnValue(false);
        }
    }
}
