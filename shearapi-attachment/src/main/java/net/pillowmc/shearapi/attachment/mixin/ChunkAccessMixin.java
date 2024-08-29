package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.pillowmc.shearapi.attachment.IntoAttachmentHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements IAttachmentHolder, IntoAttachmentHolder {
    @Unique
    private final AttachmentHolder.AsField attachmentHolder = new AttachmentHolder.AsField(this);

    @Override
    public boolean hasAttachments() {
        return attachmentHolder.hasAttachments();
    }

    @Override
    public boolean hasData(AttachmentType<?> type) {
        return attachmentHolder.hasData(type);
    }

    @Override
    public <T> T getData(AttachmentType<T> type) {
        return attachmentHolder.getData(type);
    }

    @Override
    public <T> Optional<T> getExistingData(AttachmentType<T> type) {
        return attachmentHolder.getExistingData(type);
    }

    @Override
    public <T> @Nullable T setData(AttachmentType<T> type, T data) {
        ((ChunkAccess)(Object) this).setUnsaved(true);
        return attachmentHolder.setData(type, data);
    }

    @Override
    public <T> @Nullable T removeData(AttachmentType<T> type) {
        ((ChunkAccess)(Object) this).setUnsaved(true);
        return attachmentHolder.removeData(type);
    }

    @Override
    public AttachmentHolder.AsField getAttachmentHolder() {
        return attachmentHolder;
    }
}
