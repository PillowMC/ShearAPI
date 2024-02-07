package net.pillowmc.shearapi.capabilities.injection;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BlockEntityInjection {
    private BlockEntity self() {
        return (BlockEntity) this;
    }
    /**
     * Notify all listeners that the capabilities at the positions of this block entity might have changed.
     * This includes new capabilities becoming available.
     * <p>
     * This is just a convenience method for {@link Level#invalidateCapabilities(BlockPos)}.
     */
    @ApiStatus.NonExtendable
    default void invalidateCapabilities() {
        BlockEntity be = self();
        Level level = be.getLevel();
        if (level != null)
            level.invalidateCapabilities(be.getBlockPos());
    }
}
