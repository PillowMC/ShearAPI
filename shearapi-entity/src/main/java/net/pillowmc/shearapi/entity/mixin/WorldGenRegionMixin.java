package net.pillowmc.shearapi.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

@Mixin(WorldGenRegion.class)
public class WorldGenRegionMixin {
    @Inject(at = @At("HEAD"), method = "addFreshEntity", cancellable = true)
    private void addFreshEntity(Entity p_9580_, CallbackInfoReturnable<Boolean> cir) {
        if (p_9580_ instanceof Mob mob && ((MobMixin)(Object) mob).isSpawnCancelled()) cir.setReturnValue(false);;
    }
}
