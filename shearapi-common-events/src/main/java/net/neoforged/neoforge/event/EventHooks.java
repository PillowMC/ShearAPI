/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.event;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event.Result;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.common.util.MutableHashedLinkedMap;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.AlterGroundEvent;
import net.neoforged.neoforge.event.level.AlterGroundEvent.StateProvider;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BlockToolModificationEvent;
import net.neoforged.neoforge.event.level.BlockEvent.CreateFluidSourceEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityMultiPlaceEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
import net.neoforged.neoforge.event.level.BlockEvent.NeighborNotifyEvent;
import net.pillowmc.shearapi.event.ShearAPIEvent;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.neoforged.neoforge.event.level.ChunkTicketLevelUpdatedEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.level.SaplingGrowTreeEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class EventHooks {
    public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction) {
        BlockSnapshot snap = blockSnapshots.get(0);
        BlockState placedAgainst = snap.getLevel().getBlockState(snap.getPos().relative(direction.getOpposite()));
        EntityMultiPlaceEvent event = new EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
        return ShearAPIEvent.EVENT_BUS.post(event).isCanceled();
    }

    public static boolean onBlockPlace(@Nullable Entity entity, BlockSnapshot blockSnapshot, Direction direction) {
        BlockState placedAgainst = blockSnapshot.getLevel().getBlockState(blockSnapshot.getPos().relative(direction.getOpposite()));
        EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
        return ShearAPIEvent.EVENT_BUS.post(event).isCanceled();
    }

    public static NeighborNotifyEvent onNeighborNotify(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
        NeighborNotifyEvent event = new NeighborNotifyEvent(level, pos, state, notifiedSides, forceRedstoneUpdate);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static int getItemBurnTime(ItemStack itemStack, int burnTime, @Nullable RecipeType<?> recipeType) {
        FurnaceFuelBurnTimeEvent event = new FurnaceFuelBurnTimeEvent(itemStack, burnTime, recipeType);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getBurnTime();
    }

    public static BlockState fireFluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
        BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(level, pos, liquidPos, state);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getNewState();
    }

    @Nullable
    public static BlockState onToolUse(BlockState originalState, UseOnContext context, ToolAction toolAction, boolean simulate) {
        BlockToolModificationEvent event = new BlockToolModificationEvent(originalState, context, toolAction, simulate);
        return ShearAPIEvent.EVENT_BUS.post(event).isCanceled() ? null : event.getFinalState();
    }

    public static PlayLevelSoundEvent.AtEntity onPlaySoundAtEntity(Entity entity, Holder<SoundEvent> name, SoundSource category, float volume, float pitch) {
        PlayLevelSoundEvent.AtEntity event = new PlayLevelSoundEvent.AtEntity(entity, name, category, volume, pitch);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static PlayLevelSoundEvent.AtPosition onPlaySoundAtPosition(Level level, double x, double y, double z, Holder<SoundEvent> name, SoundSource category, float volume, float pitch) {
        PlayLevelSoundEvent.AtPosition event = new PlayLevelSoundEvent.AtPosition(level, new Vec3(x, y, z), name, category, volume, pitch);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    public static boolean onExplosionStart(Level level, Explosion explosion) {
        return ShearAPIEvent.EVENT_BUS.post(new ExplosionEvent.Start(level, explosion)).isCanceled();
    }

    public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {
        //Filter entities to only those who are effected, to prevent modders from seeing more then will be hurt.
        /* Enable this if we get issues with modders looping to much.
        Iterator<Entity> itr = list.iterator();
        Vec3 p = explosion.getPosition();
        while (itr.hasNext())
        {
            Entity e = itr.next();
            double dist = e.getDistance(p.xCoord, p.yCoord, p.zCoord) / diameter;
            if (e.isImmuneToExplosions() || dist > 1.0F) itr.remove();
        }
        */
        ShearAPIEvent.EVENT_BUS.post(new ExplosionEvent.Detonate(level, explosion, list));
    }

    public static boolean onCreateWorldSpawn(Level level, ServerLevelData settings) {
        return ShearAPIEvent.EVENT_BUS.post(new LevelEvent.CreateSpawnPosition(level, settings)).isCanceled();
    }

    public static boolean onPotionAttemptBrew(NonNullList<ItemStack> stacks) {
        NonNullList<ItemStack> tmp = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int x = 0; x < tmp.size(); x++)
            tmp.set(x, stacks.get(x).copy());

        PotionBrewEvent.Pre event = new PotionBrewEvent.Pre(tmp);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled()) {
            boolean changed = false;
            for (int x = 0; x < stacks.size(); x++) {
                changed |= ItemStack.matches(tmp.get(x), stacks.get(x));
                stacks.set(x, event.getItem(x));
            }
            if (changed)
                onPotionBrewed(stacks);
            return true;
        }
        return false;
    }

    public static void onPotionBrewed(NonNullList<ItemStack> brewingItemStacks) {
        ShearAPIEvent.EVENT_BUS.post(new PotionBrewEvent.Post(brewingItemStacks));
    }

    public static LootTable loadLootTable(ResourceLocation name, LootTable table) {
        if (table == LootTable.EMPTY) // Empty table has a null name, and shouldn't be modified anyway.
            return table;
        LootTableLoadEvent event = new LootTableLoadEvent(name, table);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled())
            return LootTable.EMPTY;
        return event.getTable();
    }

    public static boolean canCreateFluidSource(Level level, BlockPos pos, BlockState state, boolean def) {
        CreateFluidSourceEvent evt = new CreateFluidSourceEvent(level, pos, state);
        ShearAPIEvent.EVENT_BUS.post(evt);

        Result result = evt.getResult();
        return result == Result.DEFAULT ? def : result == Result.ALLOW;
    }

    public static Optional<PortalShape> onTrySpawnPortal(LevelAccessor level, BlockPos pos, Optional<PortalShape> size) {
        if (!size.isPresent()) return size;
        return !ShearAPIEvent.EVENT_BUS.post(new BlockEvent.PortalSpawnEvent(level, pos, level.getBlockState(pos), size.get())).isCanceled() ? size : Optional.empty();
    }

    public static int onEnchantmentLevelSet(Level level, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int enchantmentLevel) {
        EnchantmentLevelSetEvent e = new EnchantmentLevelSetEvent(level, pos, enchantRow, power, itemStack, enchantmentLevel);
        ShearAPIEvent.EVENT_BUS.post(e);
        return e.getEnchantLevel();
    }

    public static SaplingGrowTreeEvent blockGrowFeature(LevelAccessor level, RandomSource randomSource, BlockPos pos, @Nullable Holder<ConfiguredFeature<?, ?>> holder) {
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(level, randomSource, pos, holder);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event;
    }

    /**
     * Fires the {@link AlterGroundEvent} and retrieves the resulting {@link StateProvider}.
     * 
     * @param ctx       The tree decoration context for the current alteration.
     * @param positions The list of positions that are considered roots.
     * @param provider  The original {@link BlockStateProvider} from the {@link AlterGroundDecorator}.
     * @return The (possibly event-modified) {@link StateProvider} to be used for ground alteration.
     * @apiNote This method is called off-thread during world generation.
     */
    public static StateProvider alterGround(TreeDecorator.Context ctx, List<BlockPos> positions, StateProvider provider) {
        if (positions.isEmpty()) return provider; // I don't think this list is ever empty, but if it is, firing the event is pointless anyway.
        AlterGroundEvent event = new AlterGroundEvent(ctx, positions, provider);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getStateProvider();
    }

    public static void fireChunkTicketLevelUpdated(ServerLevel level, long chunkPos, int oldTicketLevel, int newTicketLevel, @Nullable ChunkHolder chunkHolder) {
        if (oldTicketLevel != newTicketLevel)
            ShearAPIEvent.EVENT_BUS.post(new ChunkTicketLevelUpdatedEvent(level, chunkPos, oldTicketLevel, newTicketLevel, chunkHolder));
    }

    public static void fireChunkWatch(ServerPlayer entity, LevelChunk chunk, ServerLevel level) {
        ShearAPIEvent.EVENT_BUS.post(new ChunkWatchEvent.Watch(entity, chunk, level));
    }

    public static void fireChunkSent(ServerPlayer entity, LevelChunk chunk, ServerLevel level) {
        ShearAPIEvent.EVENT_BUS.post(new ChunkWatchEvent.Sent(entity, chunk, level));
    }

    public static void fireChunkUnWatch(ServerPlayer entity, ChunkPos chunkpos, ServerLevel level) {
        ShearAPIEvent.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(entity, chunkpos, level));
    }

    public static boolean onPistonMovePre(Level level, BlockPos pos, Direction direction, boolean extending) {
        return ShearAPIEvent.EVENT_BUS.post(new PistonEvent.Pre(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT)).isCanceled();
    }

    public static void onPistonMovePost(Level level, BlockPos pos, Direction direction, boolean extending) {
        ShearAPIEvent.EVENT_BUS.post(new PistonEvent.Post(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
    }

    public static long onSleepFinished(ServerLevel level, long newTime, long minTime) {
        SleepFinishedTimeEvent event = new SleepFinishedTimeEvent(level, newTime, minTime);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getNewTime();
    }

    public static List<PreparableReloadListener> onResourceReload(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        AddReloadListenerEvent event = new AddReloadListenerEvent(serverResources, registryAccess);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getListeners();
    }

    public static void onCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment, CommandBuildContext context) {
        RegisterCommandsEvent event = new RegisterCommandsEvent(dispatcher, environment, context);
        ShearAPIEvent.EVENT_BUS.post(event);
    }

    public static void onRenderTickStart(float timer) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.START, timer));
    }

    public static void onRenderTickEnd(float timer) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.END, timer));
    }

    public static void onPlayerPreTick(Player player) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, player));
    }

    public static void onPlayerPostTick(Player player) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
    }

    public static void onPreLevelTick(Level level, BooleanSupplier haveTime) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.LevelTickEvent(level.isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER, TickEvent.Phase.START, level, haveTime));
    }

    public static void onPostLevelTick(Level level, BooleanSupplier haveTime) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.LevelTickEvent(level.isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER, TickEvent.Phase.END, level, haveTime));
    }

    public static void onPreClientTick() {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.ClientTickEvent(TickEvent.Phase.START));
    }

    public static void onPostClientTick() {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.ClientTickEvent(TickEvent.Phase.END));
    }

    public static void onPreServerTick(BooleanSupplier haveTime, MinecraftServer server) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.ServerTickEvent(TickEvent.Phase.START, haveTime, server));
    }

    public static void onPostServerTick(BooleanSupplier haveTime, MinecraftServer server) {
        ShearAPIEvent.EVENT_BUS.post(new TickEvent.ServerTickEvent(TickEvent.Phase.END, haveTime, server));
    }

    public static WeightedRandomList<MobSpawnSettings.SpawnerData> getPotentialSpawns(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList) {
        LevelEvent.PotentialSpawns event = new LevelEvent.PotentialSpawns(level, category, pos, oldList);
        if (ShearAPIEvent.EVENT_BUS.post(event).isCanceled())
            return WeightedRandomList.create();
        return WeightedRandomList.create(event.getSpawnerDataList());
    }

    /**
     * Fires {@link GetEnchantmentLevelEvent} and for a single enchantment, returning the (possibly event-modified) level.
     * 
     * @param level The original level of the enchantment as provided by the Item.
     * @param stack The stack being queried against.
     * @param ench  The enchantment being queried for.
     * @return The new level of the enchantment.
     */
    public static int getEnchantmentLevelSpecific(int level, ItemStack stack, Enchantment ench) {
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(ench, level);
        var event = new GetEnchantmentLevelEvent(stack, map, ench);
        ShearAPIEvent.EVENT_BUS.post(event);
        return event.getEnchantments().getOrDefault(ench, 0);
    }

    /**
     * Fires {@link GetEnchantmentLevelEvent} and for all enchantments, returning the (possibly event-modified) enchantment map.
     * 
     * @param enchantments The original enchantment map as provided by the Item.
     * @param stack        The stack being queried against.
     * @return The new enchantment map.
     */
    public static Map<Enchantment, Integer> getEnchantmentLevel(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        enchantments = new HashMap<>(enchantments);
        var event = new GetEnchantmentLevelEvent(stack, enchantments, null);
        ShearAPIEvent.EVENT_BUS.post(event);
        return enchantments;
    }

    /**
     * Fires the {@link BuildCreativeModeTabContentsEvent}.
     *
     * @param tab               The tab that contents are being collected for.
     * @param tabKey            The resource key of the tab.
     * @param originalGenerator The display items generator that populates vanilla entries.
     * @param params            Display parameters, controlling if certain items are hidden.
     * @param output            The output acceptor.
     * @apiNote Call via {@link CreativeModeTab#buildContents(CreativeModeTab.ItemDisplayParameters)}
     */
    @ApiStatus.Internal
    public static void onCreativeModeTabBuildContents(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.DisplayItemsGenerator originalGenerator, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        final var entries = new MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility>(ItemStackLinkedSet.TYPE_AND_TAG,
                (key, left, right) -> {
                    //throw new IllegalStateException("Accidentally adding the same item stack twice " + key.getDisplayName().getString() + " to a Creative Mode Tab: " + tab.getDisplayName().getString());
                    // Vanilla adds enchanting books twice in both visibilities.
                    // This is just code cleanliness for them. For us lets just increase the visibility and merge the entries.
                    return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
                });

        originalGenerator.accept(params, (stack, vis) -> {
            if (stack.getCount() != 1)
                throw new IllegalArgumentException("The stack count must be 1");
            entries.put(stack, vis);
        });

        ShearAPIRuntime.getRuntime().postModBusEvent(new BuildCreativeModeTabContentsEvent(tab, tabKey, params, entries));

        for (var entry : entries)
            output.accept(entry.getKey(), entry.getValue());
    }
}
