package net.pillowmc.shearapi.registries.datamaps;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.pillowmc.shearapi.utils.Utils;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class DataMapFuelRegistry implements FuelRegistry {
    private final Map<ResourceKey<Item>, FurnaceFuel> map = new IdentityHashMap<>();
    private final Set<ResourceKey<Item>> removal = new HashSet<>();
    protected void onDataMapsLoadedEvent(DataMapsLoadedEvent<Item> event) {
        event.register(Map.of(NeoForgeDataMaps.FURNACE_FUELS, map));
        event.registerRemoval(Map.of(NeoForgeDataMaps.FURNACE_FUELS, removal));
    }

    @Override
    public Integer get(ItemLike item) {
        return Utils.getItemHolder(item.asItem()).map(itemHolder -> itemHolder.getData(NeoForgeDataMaps.FURNACE_FUELS).burnTime()).orElse(null);
    }

    @Override
    public void add(ItemLike item, Integer value) {
        var key = BuiltInRegistries.ITEM.getResourceKey(item.asItem());
        if (key.isEmpty()) {
            return;
        }
        map.put(key.get(), new FurnaceFuel(value));
    }

    @Override
    public void add(TagKey<Item> tag, Integer value) {
        for (var item: BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            map.put(item.unwrapKey().orElseThrow(), new FurnaceFuel(value));
        }
    }

    @Override
    public void remove(ItemLike item) {
        add(item, 0);
    }

    @Override
    public void remove(TagKey<Item> tag) {
        add(tag, 0);
    }

    @Override
    public void clear(ItemLike item) {
        BuiltInRegistries.ITEM.getResourceKey(item.asItem()).ifPresent(key -> {
            if(map.containsKey(key)) {
                map.remove(key);
                return;
            }
            removal.add(key);
        });
    }

    @Override
    public void clear(TagKey<Item> tag) {
        for (var item: BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            var key = item.unwrapKey().orElseThrow();
            if(map.containsKey(key)) {
                map.remove(key);
                continue;
            }
            removal.add(key);
        }
    }
}
