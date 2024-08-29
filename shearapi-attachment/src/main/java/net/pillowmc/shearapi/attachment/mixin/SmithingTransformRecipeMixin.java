package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SmithingTransformRecipe.class)
public abstract class SmithingTransformRecipeMixin {
    @Inject(method = "assemble", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectAssemble(Container container, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        AttachmentUtils.copyStackAttachments(container.getItem(1), itemStack);
    }
}
