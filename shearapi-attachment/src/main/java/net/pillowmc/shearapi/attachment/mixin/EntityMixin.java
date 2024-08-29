package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.pillowmc.shearapi.attachment.IntoAttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.neoforged.neoforge.attachment.AttachmentHolder.ATTACHMENTS_NBT_KEY;

@Mixin(Entity.class)
public class EntityMixin implements IntoAttachmentHolder {
    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void injectSaveWithoutId(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag attachments = this.getAttachmentHolder().serializeAttachments();
        if (attachments != null) compoundTag.put(ATTACHMENTS_NBT_KEY, attachments);
    }
}
