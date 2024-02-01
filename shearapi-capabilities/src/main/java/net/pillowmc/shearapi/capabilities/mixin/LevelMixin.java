package net.pillowmc.shearapi.capabilities.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.pillowmc.shearapi.capabilities.injection.LevelInjection;

@Mixin(Level.class)
public class LevelMixin implements LevelInjection {
    
}
