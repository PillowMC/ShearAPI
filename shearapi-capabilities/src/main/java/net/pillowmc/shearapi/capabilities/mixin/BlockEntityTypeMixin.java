package net.pillowmc.shearapi.capabilities.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Shadow
    private Set<Block> validBlocks;

    public Set<Block> getValidBlocks() {
        return java.util.Collections.unmodifiableSet(this.validBlocks);
    }
}
