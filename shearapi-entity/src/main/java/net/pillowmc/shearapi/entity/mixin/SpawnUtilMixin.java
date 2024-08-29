package net.pillowmc.shearapi.entity.mixin;

import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.pillowmc.shearapi.entity.EntityHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnUtil.class)
public class SpawnUtilMixin {
    @Redirect(method = "trySpawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;)Z"))
    private static boolean redirectCheckSpawnRules(Mob instance, LevelAccessor levelAccessor, MobSpawnType mobSpawnType) {
        return EntityHooks.checkSpawnPosition(instance, (ServerLevelAccessor) levelAccessor, mobSpawnType);
    }

    @Redirect(method = "trySpawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z"))
    private static boolean redirectCheckSpawnObstruction(Mob instance, LevelReader levelReader) {
        return true;
    }
}
