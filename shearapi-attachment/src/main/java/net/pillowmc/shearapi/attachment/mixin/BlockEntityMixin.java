package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.pillowmc.shearapi.attachment.IntoAttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements IAttachmentHolder, IntoAttachmentHolder {
    @Unique
    @Shadow
    public abstract void setChanged();

    @Override
    @Nullable
    public final <T> T setData(net.neoforged.neoforge.attachment.AttachmentType<T> type, T data) {
        setChanged();
        return getAttachmentHolder().setData(type, data);
    }

            @Override
    @Nullable
    public final <T> T removeData(net.neoforged.neoforge.attachment.AttachmentType<T> type) {
        setChanged();
        return getAttachmentHolder().removeData(type);
    }
}
