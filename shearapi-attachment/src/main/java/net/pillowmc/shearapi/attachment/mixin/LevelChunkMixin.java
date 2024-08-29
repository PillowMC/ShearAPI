package net.pillowmc.shearapi.attachment.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements IAttachmentHolder {
    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ProtoChunk;getHeightmaps()Ljava/util/Collection;"))
    private void injectInit(ServerLevel serverLevel, ProtoChunk protoChunk, LevelChunk.PostLoadProcessor postLoadProcessor, CallbackInfo ci) {
        AttachmentUtils.copyChunkAttachmentsOnPromotion((AttachmentHolder.AsField) protoChunk.getAttachmentHolder(), (AttachmentHolder.AsField) ((ChunkAccess)(Object)this).getAttachmentHolder());
    }
}
