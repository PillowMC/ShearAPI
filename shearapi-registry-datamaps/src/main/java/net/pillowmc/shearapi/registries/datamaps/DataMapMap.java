package net.pillowmc.shearapi.registries.datamaps;

import com.google.common.collect.Streams;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.IRegistryExtensionInjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataMapMap<K, V, D> implements Map<K, V> {
    private final MappedRegistry<K> registry;
    private final Class<K> type;
    private final DataMapType<K, D> dataMapType;
    private Map<ResourceKey<K>, D> dataMap;
    private Map<ResourceKey<K>, D> added;
    private Set<ResourceKey<K>> removed;
    private final Function<D, V> mapper;
    private final Function<V, D> unmapper;

    public DataMapMap(MappedRegistry<K> registry, DataMapType<K, D> dataMapType, Class<K> type, Function<D, V> mapper, Function<V, D> unmapper) {
        this.registry = registry;
        this.dataMapType = dataMapType;
        this.type = type;
        this.added = Map.of();
        this.removed = Set.of();
        this.mapper = mapper;
        this.unmapper = unmapper;
    }

    public void onDataMapsLoadedEvent(DataMapsLoadedEvent<K> event) {
        event.register(Map.of(dataMapType, added));
        event.registerRemoval(Map.of(dataMapType, removed));
        dataMap = ((IRegistryExtensionInjection<K>)registry).getDataMap(dataMapType);
    }

    private ResourceKey<K> getKey(K key) {
        return registry.getResourceKey(key).orElseThrow();
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (type.isAssignableFrom(key.getClass())) {
            var k = getKey((K)key);
            return dataMap.containsKey(k)||added.containsKey(k)&&!removed.contains(k);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        if(type.isAssignableFrom(key.getClass()) && removed.contains(getKey((K)key))) {
            return null;
        }
        var k = getKey((K)key);
        return mapper.apply(added.getOrDefault(k, dataMap.get(k)));
    }

    @Override
    public @Nullable V put(K key, V value) {
        var k = getKey(key);
        removed.remove(k);
        return mapper.apply(added.put(k, unmapper.apply(value)));
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (var entry: m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        if (!type.isAssignableFrom(key.getClass())) return null;
        var k = getKey((K)key);
        var a = added.remove(k);
        if (a != null) return mapper.apply(a);
        removed.remove(k);
        return null;
    }

    @Override
    public void clear() {
        throw new RuntimeException("Can't clear data map!");
    }

    @Override
    public @NotNull Set<K> keySet() {
        return Streams.concat(dataMap.keySet().stream().filter(v->!removed.contains(v)), added.keySet().stream()).map(registry::get).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Collection<V> values() {
        return Streams.concat(dataMap.entrySet().stream().filter(v->!removed.contains(v.getKey())), added.entrySet().stream()).map(Entry::getValue).map(mapper).collect(Collectors.toList());
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return Streams.concat(dataMap.entrySet().stream().filter(v->!removed.contains(v.getKey())), added.entrySet().stream()).map(WrappedEntry::new).collect(Collectors.toSet());
    }

    class WrappedEntry implements Entry<K, V> {
        private final K key;

        WrappedEntry(Entry<ResourceKey<K>, D> inner) {
            this.key = registry.get(inner.getKey());
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return DataMapMap.this.get(key);
        }

        @Override
        public V setValue(V value) {
            return DataMapMap.this.put(key, value);
        }
    }
}
