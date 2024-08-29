package net.pillowmc.shearapi.player;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.neoforged.bus.api.Event.Result;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.Stat;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent.ProgressType;
import net.neoforged.neoforge.event.StatAwardEvent;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.ArrowNockEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.neoforged.neoforge.event.entity.player.FillBucketEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PermissionsChangedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSleepInBedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.SleepingLocationCheckEvent;
import net.neoforged.neoforge.event.entity.player.SleepingTimeCheckEvent;
import net.pillowmc.shearapi.event.ShearAPIEvent;
import net.pillowmc.shearapi.player.mixin.PlayerDataStorageMixin;

public class EventHooks {

    public static void onAdvancementEarnedEvent(Player player, AdvancementHolder earned) {
        ShearAPIEvent.EVENT_BUS.post(new AdvancementEarnEvent(player, earned));
    }

    public static void onAdvancementProgressedEvent(Player player, AdvancementHolder progressed, AdvancementProgress advancementProgress, String criterion, ProgressType progressType) {
        ShearAPIEvent.EVENT_BUS.post(
            new AdvancementProgressEvent(
                player,
                progressed,
                advancementProgress,
                criterion,
                progressType
            )
        );
    }

    public static boolean doPlayerHarvestCheck(Player player, BlockState state, boolean success) {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.canHarvest();
    }

    public static float getBreakSpeed(Player player, BlockState state, float original, BlockPos pos) {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return (ShearAPIEvent.EVENT_BUS.post(event).isCanceled() ? -1 : event.getNewSpeed());
    }

    public static void onPlayerDestroyItem(Player player, ItemStack stack, @Nullable InteractionHand hand) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack, hand));
    }

    public static void onPlayerBrewedPotion(Player player, ItemStack stack) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerBrewedPotionEvent(player, stack));
    }

    public static boolean fireSleepingLocationCheck(LivingEntity player, BlockPos sleepingLocation) {
        SleepingLocationCheckEvent evt = new SleepingLocationCheckEvent(player, sleepingLocation);
        ShearAPIEvent.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT) {
            return player.getSleepingPos().map(pos -> {
                BlockState state = player.level().getBlockState(pos);
                return state.getBlock() instanceof BedBlock;//.isBed(state, player.level(), pos, player);
                // TODO (ShearAPI): impl isBed?
            }).orElse(false);
        } else
            return canContinueSleep == Result.ALLOW;
    }

    public static boolean fireSleepingTimeCheck(Player player, Optional<BlockPos> sleepingLocation) {
        SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, sleepingLocation);
        ShearAPIEvent.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return !player.level().isDay();
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static InteractionResultHolder<ItemStack> onArrowNock(ItemStack item, Level level, Player player, InteractionHand hand, boolean hasAmmo) {
        ArrowNockEvent event = new ArrowNockEvent(player, item, hand, level, hasAmmo);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled())
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, item);
        return event.getAction();
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, level, charge, hasAmmo);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled())
            return -1;
        return event.getCharge();
    }

    public static int onApplyBonemeal(Player player, Level level, BlockPos pos, BlockState state, ItemStack stack) {
        BonemealEvent event = new BonemealEvent(player, level, pos, state, stack);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled()) return -1;
        if (event.getResult() == Result.ALLOW) {
            if (!level.isClientSide)
                stack.shrink(1);
            return 1;
        }
        return 0;
    }

    @Nullable
    public static InteractionResultHolder<ItemStack> onBucketUse(Player player, Level level, ItemStack stack, @Nullable HitResult target) {
        FillBucketEvent event = new FillBucketEvent(player, stack, level, target);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled()) return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, stack);

        if (event.getResult() == Result.ALLOW) {
            if (player.getAbilities().instabuild)
                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, stack);

            stack.shrink(1);
            if (stack.isEmpty())
                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, event.getFilledBucket());

            if (!player.getInventory().add(event.getFilledBucket()))
                player.drop(event.getFilledBucket(), false);

            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, stack);
        }
        return null;
    }

    public static int onItemPickup(ItemEntity entityItem, Player player) {
        var event = new EntityItemPickupEvent(player, entityItem);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled()) return -1;
        return event.getResult() == Result.ALLOW ? 1 : 0;
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable Player entityPlayer, List<Component> list, TooltipFlag flags) {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, list, flags);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static boolean onPermissionChanged(GameProfile gameProfile, int newLevel, PlayerList playerList) {
        int oldLevel = playerList.getServer().getProfilePermissions(gameProfile);
        ServerPlayer player = playerList.getPlayer(gameProfile.getId());
        if (newLevel != oldLevel && player != null) {
            return ShearAPIEvent.EVENT_BUS.post(new PermissionsChangedEvent(player, newLevel, oldLevel)).isCanceled();
        }
        return false;
    }

    public static void firePlayerChangedDimensionEvent(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, fromDim, toDim));
    }

    public static void firePlayerLoggedIn(Player player) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(player));
    }

    public static void firePlayerLoggedOut(Player player) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.PlayerLoggedOutEvent(player));
    }

    public static void firePlayerRespawnEvent(Player player, boolean endConquered) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.PlayerRespawnEvent(player, endConquered));
    }

    public static void firePlayerItemPickupEvent(Player player, ItemEntity item, ItemStack clone) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.ItemPickupEvent(player, item, clone));
    }

    public static void firePlayerCraftingEvent(Player player, ItemStack crafted, Container craftMatrix) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.ItemCraftedEvent(player, crafted, craftMatrix));
    }

    public static void firePlayerSmeltedEvent(Player player, ItemStack smelted) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.ItemSmeltedEvent(player, smelted));
    }

    public static Player.BedSleepingProblem onPlayerSleepInBed(Player player, Optional<BlockPos> pos) {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getResultStatus();
    }

    public static void onPlayerWakeup(Player player, boolean wakeImmediately, boolean updateLevel) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerWakeUpEvent(player, wakeImmediately, updateLevel));
    }

    public static void onPlayerFall(Player player, float distance, float multiplier) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerFlyableFallEvent(player, distance, multiplier));
    }

    public static boolean onPlayerSpawnSet(Player player, ResourceKey<Level> levelKey, BlockPos pos, boolean forced) {
        return ShearAPIEvent.EVENT_BUS.post(new PlayerSetSpawnEvent(player, levelKey, pos, forced)).isCanceled();
    }

    public static void onPlayerClone(Player player, Player oldPlayer, boolean wasDeath) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.Clone(player, oldPlayer, wasDeath));
    }

    public static PlayerSpawnPhantomsEvent onPhantomSpawn(ServerPlayer player, int phantomsToSpawn) {
        var event = new PlayerSpawnPhantomsEvent(player, phantomsToSpawn);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static StatAwardEvent onStatAward(Player player, Stat<?> stat, int value) {
        StatAwardEvent event = new StatAwardEvent(player, stat, value);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static Component getPlayerDisplayName(Player player, Component username) {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getDisplayname();
    }

    public static Component getPlayerTabListDisplayName(Player player) {
        PlayerEvent.TabListNameFormat event = new PlayerEvent.TabListNameFormat(player);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getDisplayName();
    }

    public static void onStartEntityTracking(Entity entity, Player player) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, Player player) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(Player player, File playerDirectory, String uuidString) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(Player player, File playerDirectory, String uuidString) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerLoadingEvent(Player player, PlayerDataStorage playerFileData, String uuidString) {
        ShearAPIEvent.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, ((PlayerDataStorageMixin)playerFileData).getPlayerDataFolder(), uuidString));
    }
}
