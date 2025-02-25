package net.pillowmc.shearapi.conditions.injection;

import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.common.conditions.ICondition;

public interface ReloadableServerResourcesInjection {
    default ICondition.IContext getConditionContext() {
        throw new AssertionError("This should be implemented by mixin!");
    }
//    default RegistryAccess getRegistryAccess() { TODO: Put this elsewhere.
//        throw new AssertionError("This should be implemented by mixin!");
//    }
}
