package net.pillowmc.shearapi.attachment.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", ordinal = 9), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectWrite(ServerLevel serverLevel, ChunkAccess chunkAccess, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkPos, CompoundTag compoundTag, BlendingData blendingData, BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, LevelChunkSection[] levelChunkSections, ListTag listTag, LevelLightEngine levelLightEngine, Registry registry, Codec codec, boolean bl, ListTag listTag2, CompoundTag compoundTag4) {
        try {
            final CompoundTag capTag = chunkAccess.getAttachmentHolder().serializeAttachments();
            if (capTag != null) compoundTag.put(net.neoforged.neoforge.attachment.AttachmentHolder.ATTACHMENTS_NBT_KEY, capTag);
        } catch (Exception exception) {
            LOGGER.error("Failed to write chunk attachments. An attachment has likely thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
        }
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkAccess;setLightCorrect(Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectRead(ServerLevel serverLevel, PoiManager poiManager, ChunkPos chunkPos, CompoundTag compoundTag, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkPos2, UpgradeData upgradeData, boolean bl, ListTag listTag, int i, LevelChunkSection[] levelChunkSections, boolean bl2, ChunkSource chunkSource, LevelLightEngine levelLightEngine, Registry registry, Codec codec, boolean bl3, long m, ChunkStatus.ChunkType chunkType, BlendingData blendingData, ChunkAccess chunkAccess) {
        if (compoundTag.contains(AttachmentHolder.ATTACHMENTS_NBT_KEY, Tag.TAG_COMPOUND))
            ((AttachmentHolder.AsField)chunkAccess.getAttachmentHolder()).deserializeInternal(compoundTag.getCompound(AttachmentHolder.ATTACHMENTS_NBT_KEY));
    }
}
