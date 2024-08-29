package net.pillowmc.shearapi.registries.datamaps;

import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.IBaseMappedRegistryInjection;
import net.pillowmc.shearapi.runtime.IModBusEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class DataMapsLoadedEvent<R> extends Event implements IModBusEvent {
    private final IBaseMappedRegistryInjection<R> registry;
    public DataMapsLoadedEvent(IBaseMappedRegistryInjection<R> registry) {
        this.registry = registry;
    }

    public <T> void register(Map<DataMapType<R, T>, Map<ResourceKey<R>, T>> map) {
        var dataMaps = registry.shearAPI$getDataMaps();
        for(var v: map.entrySet()) {
            dataMaps.computeIfAbsent(v.getKey(), key -> new IdentityHashMap<>()).putAll((Map)v.getValue());
        }
    }

    public <T> void registerRemoval(Map<DataMapType<R, T>, Set<ResourceKey<R>>> map) {
        var dataMaps = registry.shearAPI$getDataMaps();
        for(var v: map.entrySet()) {
            for(var key: v.getValue()) {
                dataMaps.remove(key);
            }
        }
    }
}
