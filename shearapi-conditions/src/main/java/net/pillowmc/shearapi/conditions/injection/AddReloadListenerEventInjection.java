package net.pillowmc.shearapi.conditions.injection;

import net.neoforged.neoforge.common.conditions.ICondition;

public interface AddReloadListenerEventInjection {
    /**
     * This context object holds data relevant to the current reload, such as staged tags.
     *
     * @return The condition context for the currently active reload.
     */
    default ICondition.IContext getConditionContext() {
        throw new AssertionError("This should be implemented by mixin!");
    }
}
