package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.pillowmc.shearapi.attachment.IntoAttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ImposterProtoChunk.class)
public abstract class ImposterProtoChunkMixin implements IntoAttachmentHolder {
    @Override
    public AttachmentHolder getAttachmentHolder() {
        return ((ImposterProtoChunk)(Object) this).getWrapped().getAttachmentHolder();
    }
}
