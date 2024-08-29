package net.pillowmc.shearapi.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.PositionCheck;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.AllowDespawn;
import net.neoforged.neoforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.pillowmc.shearapi.event.ShearAPIEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class EntityHooks {    /**
 * Internal, should only be called via {@link SpawnPlacements#checkSpawnRules}.
 *
 * @see SpawnPlacementCheck
 */
@ApiStatus.Internal
public static boolean checkSpawnPlacements(EntityType<?> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random, boolean defaultResult) {
    var event = new SpawnPlacementCheck(entityType, level, spawnType, pos, random, defaultResult);
    ShearAPIEvent.EVENT_BUS.post(event);
    return event.getResult() == Event.Result.DEFAULT ? defaultResult : event.getResult() == Event.Result.ALLOW;
}

    /**
     * Checks if the current position of the passed mob is valid for spawning, by firing {@link PositionCheck}.<br>
     * The default check is to perform the logical and of {@link Mob#checkSpawnRules} and {@link Mob#checkSpawnObstruction}.<br>
     *
     * @param mob       The mob being spawned.
     * @param level     The level the mob will be added to, if successful.
     * @param spawnType The spawn type of the spawn.
     * @return True, if the position is valid, as determined by the contract of {@link PositionCheck}.
     * @see PositionCheck
     */
    public static boolean checkSpawnPosition(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType) {
        var event = new PositionCheck(mob, level, spawnType, null);
        ShearAPIEvent.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DEFAULT) {
            return mob.checkSpawnRules(level, spawnType) && mob.checkSpawnObstruction(level);
        }
        return event.getResult() == Event.Result.ALLOW;
    }

    /**
     * Specialized variant of {@link #checkSpawnPosition} for spawners, as they have slightly different checks.
     *
     * @see #checkSpawnPosition
     * @implNote See in-line comments about custom spawn rules.
     */
    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, SpawnData spawnData, BaseSpawner spawner) {
        var event = new PositionCheck(mob, level, spawnType, null);
        ShearAPIEvent.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DEFAULT) {
            // Spawners do not evaluate Mob#checkSpawnRules if any custom rules are present. This is despite the fact that these two methods do not check the same things.
            return (spawnData.getCustomSpawnRules().isPresent() || mob.checkSpawnRules(level, spawnType)) && mob.checkSpawnObstruction(level);
        }
        return event.getResult() == Event.Result.ALLOW;
    }

    /**
     * Vanilla calls to {@link Mob#finalizeSpawn} are replaced with calls to this method via coremod.<br>
     * Mods should call this method in place of calling {@link Mob#finalizeSpawn}. Super calls (from within overrides) should not be wrapped.
     * <p>
     * When interfacing with this event, write all code as normal, and replace the call to {@link Mob#finalizeSpawn} with a call to this method.<p>
     * As an example, the following code block:
     * <code>
     *
     * <pre>
     * var zombie = new Zombie(level);
     * zombie.finalizeSpawn(level, difficulty, spawnType, spawnData, spawnTag);
     * level.tryAddFreshEntityWithPassengers(zombie);
     * if (zombie.isAddedToWorld()) {
     *     // Do stuff with your new zombie
     * }
     * </pre>
     *
     * </code>
     * Would become:
     * <code>
     *
     * <pre>
     * var zombie = new Zombie(level);
     * EventHook.onFinalizeSpawn(zombie, level, difficulty, spawnType, spawnData, spawnTag);
     * level.tryAddFreshEntityWithPassengers(zombie);
     * if (zombie.isAddedToWorld()) {
     *     // Do stuff with your new zombie
     * }
     * </pre>
     *
     * </code>
     * The only code that changes is the {@link Mob#finalizeSpawn} call.
     *
     * @return The SpawnGroupData from this event, or null if it was canceled. The return value of this method has no bearing on if the entity will be spawned.
     * @see MobSpawnEvent.FinalizeSpawn
     * @see Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)
     * @apiNote Callers do not need to check if the entity's spawn was cancelled, as the spawn will be blocked by Forge.
     * @implNote Changes to the signature of this method must be reflected in the method redirector coremod.
     */
    @Nullable
    @SuppressWarnings("deprecation") // Call to deprecated Mob#finalizeSpawn is expected.
    public static SpawnGroupData onFinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag) {
        var event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, spawnType, spawnData, spawnTag, null);
        boolean cancel = ShearAPIEvent.EVENT_BUS.post(event).isCanceled();

        if (!cancel) {
            mob.finalizeSpawn(level, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
        }

        return cancel ? null : event.getSpawnData();
    }

    /**
     * Returns the FinalizeSpawn event instance, or null if it was canceled.<br>
     * This is separate since mob spawners perform special finalizeSpawn handling when NBT data is present, but we still want to fire the event.<br>
     * This overload is also the only way to pass through a {@link BaseSpawner} instance.
     *
     * @see #onFinalizeSpawn
     */
    @Nullable
    public static MobSpawnEvent.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
        var event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, MobSpawnType.SPAWNER, spawnData, spawnTag, spawner);
        boolean cancel = ShearAPIEvent.EVENT_BUS.post(event).isCanceled();
        return cancel ? null : event;
    }

    public static Event.Result canEntityDespawn(Mob entity, ServerLevelAccessor level) {
        AllowDespawn event = new AllowDespawn(entity, level);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static SummonAidEvent fireZombieSummonAid(Zombie zombie, Level level, int x, int y, int z, LivingEntity attacker, double summonChance) {
        SummonAidEvent summonEvent = new SummonAidEvent(zombie, level, x, y, z, attacker, summonChance);
        ShearAPIEvent.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }

    public static boolean onEntityStruckByLightning(Entity entity, LightningBolt bolt) {
        return ShearAPIEvent.EVENT_BUS.post(new EntityStruckByLightningEvent(entity, bolt)).isCanceled();
    }

    public static int onItemUseStart(LivingEntity entity, ItemStack item, int duration) {
        var event = new LivingEntityUseItemEvent.Start(entity, item, duration);
        return ShearAPIEvent.EVENT_BUS.post(event).isCanceled() ? -1 : event.getDuration();
    }

    public static int onItemUseTick(LivingEntity entity, ItemStack item, int duration) {
        var event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
        return ShearAPIEvent.EVENT_BUS.post(event).isCanceled() ? -1 : event.getDuration();
    }

    public static boolean onUseItemStop(LivingEntity entity, ItemStack item, int duration) {
        return ShearAPIEvent.EVENT_BUS.post(new LivingEntityUseItemEvent.Stop(entity, item, duration)).isCanceled();
    }

    public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack item, int duration, ItemStack result) {
        LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(entity, item, duration, result);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getResultStack();
    }

    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting) {
        boolean isCanceled = ShearAPIEvent.EVENT_BUS.post(new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.level(), isMounting)).isCanceled();

        if (isCanceled) {
            entityMounting.absMoveTo(entityMounting.getX(), entityMounting.getY(), entityMounting.getZ(), entityMounting.yRotO, entityMounting.xRotO);
            return false;
        } else
            return true;
    }

    public static boolean onAnimalTame(Animal animal, Player tamer) {
        return ShearAPIEvent.EVENT_BUS.post(new AnimalTameEvent(animal, tamer)).isCanceled();
    }

    public static float onLivingHeal(LivingEntity entity, float amount) {
        LivingHealEvent event = new LivingHealEvent(entity, amount);
        return (ShearAPIEvent.EVENT_BUS.post(event).isCanceled() ? 0 : event.getAmount());
    }

    public static boolean onProjectileImpact(Projectile projectile, HitResult ray) {
        return ShearAPIEvent.EVENT_BUS.post(new ProjectileImpactEvent(projectile, ray)).isCanceled();
    }

    public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state) {
        return !ShearAPIEvent.EVENT_BUS.post(new LivingDestroyBlockEvent(entity, pos, state)).isCanceled();
    }

    public static boolean getMobGriefingEvent(Level level, @Nullable Entity entity) {
        if (entity == null)
            return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

        EntityMobGriefingEvent event = new EntityMobGriefingEvent(entity);
        ShearAPIEvent.EVENT_BUS.post(event);

        Event.Result result = event.getResult();
        return result == Event.Result.DEFAULT ? level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) : result == Event.Result.ALLOW;
    }

    public static EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions size, float eyeHeight) {
        EntityEvent.Size evt = new EntityEvent.Size(entity, pose, size, eyeHeight);
        ShearAPIEvent.EVENT_BUS.post(evt);
        return evt;
    }

    public static EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float newEyeHeight) {
        EntityEvent.Size evt = new EntityEvent.Size(entity, pose, oldSize, newSize, entity.getEyeHeight(), newEyeHeight);
        ShearAPIEvent.EVENT_BUS.post(evt);
        return evt;
    }

    public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> outcome, Consumer<Integer> timer) {
        return !ShearAPIEvent.EVENT_BUS.post(new LivingConversionEvent.Pre(entity, outcome, timer)).isCanceled();
    }

    public static void onLivingConvert(LivingEntity entity, LivingEntity outcome) {
        ShearAPIEvent.EVENT_BUS.post(new LivingConversionEvent.Post(entity, outcome));
    }

    public static EntityTeleportEvent.TeleportCommand onEntityTeleportCommand(Entity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.TeleportCommand event = new EntityTeleportEvent.TeleportCommand(entity, targetX, targetY, targetZ);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static EntityTeleportEvent.SpreadPlayersCommand onEntityTeleportSpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.SpreadPlayersCommand event = new EntityTeleportEvent.SpreadPlayersCommand(entity, targetX, targetY, targetZ);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static EntityTeleportEvent.EnderEntity onEnderTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, targetX, targetY, targetZ);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    @ApiStatus.Internal
    public static EntityTeleportEvent.EnderPearl onEnderPearlLand(ServerPlayer entity, double targetX, double targetY, double targetZ, ThrownEnderpearl pearlEntity, float attackDamage, HitResult hitResult) {
        EntityTeleportEvent.EnderPearl event = new EntityTeleportEvent.EnderPearl(entity, targetX, targetY, targetZ, pearlEntity, attackDamage, hitResult);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static EntityTeleportEvent.ChorusFruit onChorusFruitTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.ChorusFruit event = new EntityTeleportEvent.ChorusFruit(entity, targetX, targetY, targetZ);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static boolean onEffectRemoved(LivingEntity entity, MobEffect effect, @Nullable EffectCure cure) {
        return ShearAPIEvent.EVENT_BUS.post(new MobEffectEvent.Remove(entity, effect, cure)).isCanceled();
    }

    public static boolean onEffectRemoved(LivingEntity entity, MobEffectInstance effectInstance, @Nullable EffectCure cure) {
        return ShearAPIEvent.EVENT_BUS.post(new MobEffectEvent.Remove(entity, effectInstance, cure)).isCanceled();
    }

    /**
     * Fires the mob split event. Returns the event for cancellation checking.
     *
     * @param parent   The parent mob, which is in the process of being removed.
     * @param children All child mobs that would have normally spawned.
     * @return The event object.
     */
    public static MobSplitEvent onMobSplit(Mob parent, List<Mob> children) {
        var event = new MobSplitEvent(parent, children);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static int onItemExpire(ItemEntity entity, ItemStack item) {
        if (item.isEmpty()) return -1;
        ItemExpireEvent event = new ItemExpireEvent(entity, (item.isEmpty() ? 6000 : item.getItem().getEntityLifespan(item, entity.level())));
        if (!ShearAPIEvent.EVENT_BUS.post(event).isCanceled()) return -1;
        return event.getExtraLife();
    }

    public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
        LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled()) {
            return 0;
        }
        return event.getDroppedExperience();
    }

    public static int getMaxSpawnPackSize(Mob entity) {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        ShearAPIEvent.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Event.Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.getMaxSpawnClusterSize();
    }
}
