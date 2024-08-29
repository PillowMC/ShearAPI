package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BookCloningRecipe.class)
public abstract class BookCloningRecipeMixin {
    @Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "RETURN", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectAssemble(CraftingContainer craftingContainer, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir, int i, ItemStack itemStack, ItemStack itemStack3, CompoundTag compoundTag) {
        AttachmentUtils.copyStackAttachments(itemStack, itemStack3);
    }
}
