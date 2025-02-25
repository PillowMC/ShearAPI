package net.pillowmc.shearapi.conditions.mixin;

import net.minecraft.server.ReloadableServerResources;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.pillowmc.shearapi.conditions.injection.AddReloadListenerEventInjection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AddReloadListenerEvent.class)
public class AddReloadListenerEventMixin implements AddReloadListenerEventInjection {
    @Shadow
    @Final
    private ReloadableServerResources serverResources;
    @Override
    public ICondition.IContext getConditionContext() {
        return serverResources.getConditionContext();
    }
}
