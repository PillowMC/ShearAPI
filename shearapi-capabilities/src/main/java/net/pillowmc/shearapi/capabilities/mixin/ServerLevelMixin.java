package net.pillowmc.shearapi.capabilities.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.capabilities.CapabilityListenerHolder;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import net.pillowmc.shearapi.capabilities.injection.ServerLevelInjection;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ServerLevelInjection {
    private final CapabilityListenerHolder capListenerHolder = new CapabilityListenerHolder();

    @Override
    public void invalidateCapabilities(BlockPos pos) {
        capListenerHolder.invalidatePos(pos);
    }

    @Override
    public void invalidateCapabilities(ChunkPos pos) {
        capListenerHolder.invalidateChunk(pos);
    }

    @Override
    public void registerCapabilityListener(BlockPos pos, ICapabilityInvalidationListener listener) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerCapabilityListener'");
    }

    @Override
    public void cleanCapabilityListenerReferences() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cleanCapabilityListenerReferences'");
    }
    
}
