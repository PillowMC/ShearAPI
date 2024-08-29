package net.pillowmc.shearapi.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.pillowmc.shearapi.entity.injection.EntityInjection;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInjection {
    private boolean isAddedToWorld;

    @Shadow
    protected abstract float getEyeHeight(Pose pose, EntityDimensions size);

    @Override
    public float getEyeHeightAccess(Pose pose, EntityDimensions size) {
        return this.getEyeHeight(pose, size);
    }

    @Override
    public final boolean isAddedToWorld() { return this.isAddedToWorld; }

    @Override
    public void onAddedToWorld() { this.isAddedToWorld = true; }

    @Override
    public void onRemovedFromWorld() { this.isAddedToWorld = false; }
}
