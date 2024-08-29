package net.pillowmc.shearapi.entity.injection;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public interface EntityInjection {
    default float getEyeHeightAccess(Pose pose, EntityDimensions size) {
        throw new AssertionError("This should be implemented by mixin!");
    }
}
