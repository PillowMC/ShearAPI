package net.pillowmc.shearapi.capabilities.injection;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

public interface LevelInjection {
    private Level self() {
        return (Level) this;
    }

    /**
     * Retrieve a block capability.
     *
     * <p>If the block state and/or the block entity is known,
     * pass them via {@link #getCapability(BlockCapability, BlockPos, BlockState, BlockEntity, Object)} instead.
     */
    @Nullable
    default <T, C> T getCapability(BlockCapability<T, C> cap, BlockPos pos, C context) {
        return cap.getCapability(self(), pos, null, null, context);
    }

    /**
     * Retrieve a block capability.
     *
     * <p>Use this override if the block state and/or the block entity is known,
     * otherwise prefer the shorter {@link #getCapability(BlockCapability, BlockPos, Object)}.
     *
     * <p>If either the block state or the block entity is unknown, simply pass {@code null}.
     * This function will fetch {@code null} parameters from the level,
     * with some extra checks to attempt to skip unnecessary fetches.
     *
     * @param state       the block state, if known, or {@code null} if unknown
     * @param blockEntity the block entity, if known, or {@code null} if unknown
     */
    @Nullable
    default <T, C> T getCapability(BlockCapability<T, C> cap, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context) {
        return cap.getCapability(self(), pos, state, blockEntity, context);
    }

    /**
     * Notify all listeners that the capabilities at a specific position might have changed.
     * This includes new capabilities becoming available.
     *
     * <p>This method will only do something on {@link ServerLevel}s,
     * but it is safe to call on any {@link Level}, without the need for an {@code instanceof} check.
     *
     * <p>If you already have a block entity at that position, you can call {@link BlockEntity#invalidateCapabilities()} instead.
     */
    default void invalidateCapabilities(BlockPos pos) {}

    /**
     * Notify all listeners that the capabilities at all the positions in a chunk might have changed.
     * This includes new capabilities becoming available.
     *
     * <p>This method will only do something on {@link ServerLevel}s,
     * but it is safe to call on any {@link Level}, without the need for an {@code instanceof} check.
     */
    default void invalidateCapabilities(ChunkPos pos) {}
}
