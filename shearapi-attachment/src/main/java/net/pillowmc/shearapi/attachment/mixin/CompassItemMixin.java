package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CompassItem.class)
public abstract class CompassItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAbilities()Lnet/minecraft/world/entity/player/Abilities;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectUseOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, BlockPos blockPos, Level level, Player player, ItemStack itemStack, boolean bl, ItemStack itemStack2, CompoundTag compoundTag) {
        AttachmentUtils.copyStackAttachments(itemStack, itemStack2);
    }
}
