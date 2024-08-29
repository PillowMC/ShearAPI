package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
    @Inject(method = "method_17410", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectUse(ItemStack itemStack, int i, Player player, int j, ItemStack itemStack2, Level level, BlockPos blockPos, CallbackInfo ci, ItemStack itemStack3, List list, boolean bl) {
        AttachmentUtils.copyStackAttachments(itemStack, itemStack3);
    }
}
