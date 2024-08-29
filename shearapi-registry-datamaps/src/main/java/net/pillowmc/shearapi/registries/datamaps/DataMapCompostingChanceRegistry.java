package net.pillowmc.shearapi.registries.datamaps;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.pillowmc.shearapi.utils.Utils;

import java.util.*;

public class DataMapCompostingChanceRegistry implements CompostingChanceRegistry {
    private final Map<ResourceKey<Item>, Compostable> map = new IdentityHashMap<>();
    private final Set<ResourceKey<Item>> removal = new HashSet<>();
    protected void onDataMapsLoadedEvent(DataMapsLoadedEvent<Item> event) {
        event.register(Map.of(NeoForgeDataMaps.COMPOSTABLES, map));
        event.registerRemoval(Map.of(NeoForgeDataMaps.COMPOSTABLES, removal));
    }

    @Override
    public Float get(ItemLike item) {
        return Utils.getItemHolder(item.asItem()).map(itemHolder -> itemHolder.getData(NeoForgeDataMaps.COMPOSTABLES).chance()).orElse(0.0F);
    }

    @Override
    public void add(ItemLike item, Float value) {
        var key = BuiltInRegistries.ITEM.getResourceKey(item.asItem());
        if (key.isEmpty()) {
            return;
        }
        map.put(key.get(), new Compostable(value));
    }

    @Override
    public void add(TagKey<Item> tag, Float value) {
        for (var item: BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            map.put(item.unwrapKey().orElseThrow(), new Compostable(value));
        }
    }

    @Override
    public void remove(ItemLike item) {
        BuiltInRegistries.ITEM.getResourceKey(item.asItem()).ifPresent(key -> {
            if(map.containsKey(key)) {
                map.remove(key);
                return;
            }
            removal.add(key);
        });
    }

    @Override
    public void remove(TagKey<Item> tag) {
        for (var item: BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            var key = item.unwrapKey().orElseThrow();
            if(map.containsKey(key)) {
                map.remove(key);
                continue;
            }
            removal.add(key);
        }
    }

    @Override
    public void clear(ItemLike item) {
        throw new UnsupportedOperationException("DataMapCompostingChanceRegistry operates on the NeoForge Data Map - clearing not supported!");
    }

    @Override
    public void clear(TagKey<Item> tag) {
        throw new UnsupportedOperationException("DataMapCompostingChanceRegistry operates on the NeoForge Data Map - clearing not supported!");
    }
}
