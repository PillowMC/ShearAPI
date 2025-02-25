package net.pillowmc.shearapi.conditions.mixin;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.TagManager;
import net.neoforged.neoforge.common.conditions.ConditionContext;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.pillowmc.shearapi.conditions.injection.ReloadableServerResourcesInjection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin implements ReloadableServerResourcesInjection {
    @Shadow @Final private TagManager tagManager;
    @Mutable @Final @Unique private ICondition.IContext context;
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setContext(CallbackInfo ci) {
        this.context = new ConditionContext(this.tagManager);
    }
    @Override
    public ICondition.IContext getConditionContext() {
        return context;
    }
}
