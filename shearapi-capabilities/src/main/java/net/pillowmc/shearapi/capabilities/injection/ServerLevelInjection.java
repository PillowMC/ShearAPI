package net.pillowmc.shearapi.capabilities.injection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;

public interface ServerLevelInjection {

    default public void invalidateCapabilities(BlockPos pos) {
        throw new AssertionError("This should be implemented by mixin!");
    }

    default public void invalidateCapabilities(ChunkPos pos) {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * Register a listener for capability invalidation.
     * @see ICapabilityInvalidationListener
     */
    default public void registerCapabilityListener(BlockPos pos, ICapabilityInvalidationListener listener) {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * Internal method, used to clean capability listeners that are not referenced.
     * Do not call.
     */
    @org.jetbrains.annotations.ApiStatus.Internal
    default public void cleanCapabilityListenerReferences() {
        throw new AssertionError("This should be implemented by mixin!");
    }
}
