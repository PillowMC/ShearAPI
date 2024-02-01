package net.pillowmc.shearapi.common.extensions.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.ILevelExtension;

@Mixin(Level.class)
public class LevelMixin implements ILevelExtension {
    
}
