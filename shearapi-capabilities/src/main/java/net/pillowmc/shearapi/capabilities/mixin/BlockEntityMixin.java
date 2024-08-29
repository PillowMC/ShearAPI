package net.pillowmc.shearapi.capabilities.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.pillowmc.shearapi.capabilities.injection.BlockEntityInjection;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements BlockEntityInjection {
    @Inject(method = "setRemoved", at = @At("TAIL"))
    public void afterSetRemoved(CallbackInfo ci) {
        this.invalidateCapabilities();
    }

    @Inject(method = "clearRemoved", at = @At("TAIL"))
    public void afterClearRemoved(CallbackInfo ci) {
        this.invalidateCapabilities();
    }
}
