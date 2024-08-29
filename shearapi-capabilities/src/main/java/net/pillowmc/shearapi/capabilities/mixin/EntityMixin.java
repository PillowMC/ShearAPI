package net.pillowmc.shearapi.capabilities.mixin;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.capabilities.EntityCapability;

@Mixin(Entity.class)
public class EntityMixin {

    @Nullable
    public final <T, C> T getCapability(EntityCapability<T, C> capability, @UnknownNullability C context) {
        return capability.getCapability((Entity)(Object)this, context);
    }

    @Nullable
    public final <T> T getCapability(EntityCapability<T, Void> capability) {
        return capability.getCapability((Entity)(Object)this, null);
    }
    
}
