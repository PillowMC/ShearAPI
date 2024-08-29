package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FoodOnAStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FoodOnAStickItem.class)
public abstract class FoodOnAStickItemMixin {
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;success(Ljava/lang/Object;)Lnet/minecraft/world/InteractionResultHolder;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectUse(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemStack, Entity entity, ItemSteerable itemSteerable, ItemStack itemStack2) {
        AttachmentUtils.copyStackAttachments(itemStack, itemStack2);
    }
}
