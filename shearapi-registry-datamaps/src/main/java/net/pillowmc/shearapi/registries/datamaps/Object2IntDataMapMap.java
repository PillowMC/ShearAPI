package net.pillowmc.shearapi.registries.datamaps;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.IRegistryExtensionInjection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class Object2IntDataMapMap<K, D> implements Object2IntMap<K> {
    private final MappedRegistry<K> registry;
    private final Class<K> type;
    private final DataMapType<K, D> dataMapType;
    private Map<ResourceKey<K>, D> dataMap;
    private Map<ResourceKey<K>, D> added;
    private Set<ResourceKey<K>> removed;
    private final ToIntFunction<D> mapper;
    private final Int2ObjectFunction<D> unmapper;
    private int defaultValue;

    public Object2IntDataMapMap(MappedRegistry<K> registry, DataMapType<K, D> dataMapType, Class<K> type, ToIntFunction<D> mapper, Int2ObjectFunction<D> unmapper) {
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
    public boolean containsValue(int value) {
        return false;
    }

    @Override
    public Integer get(Object key) {
        if(type.isAssignableFrom(key.getClass()) && removed.contains(getKey((K)key))) {
            return null;
        }
        var k = getKey((K)key);
        return mapper.applyAsInt(added.getOrDefault(k, dataMap.get(k)));
    }

    @Override
    public int put(K key, int value) {
        var k = getKey(key);
        removed.remove(k);
        return mapper.applyAsInt(added.put(k, unmapper.apply(value)));
    }

    @Override
    public int getInt(Object o) {
        return 0;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends Integer> m) {
        for (var entry: m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Integer remove(Object key) {
        if (!type.isAssignableFrom(key.getClass())) return null;
        var k = getKey((K)key);
        var a = added.remove(k);
        if (a != null) return mapper.applyAsInt(a);
        removed.remove(k);
        return null;
    }

    @Override
    public void clear() {
        throw new RuntimeException("Can't clear data map!");
    }

    @Override
    public void defaultReturnValue(int i) {
        this.defaultValue = i;
    }

    @Override
    public int defaultReturnValue() {
        return this.defaultValue;
    }

    @Override
    public @NotNull ObjectSet<K> keySet() {
        return new ObjectArraySet<>(Streams.concat(dataMap.keySet().stream().filter(v->!removed.contains(v)), added.keySet().stream()).map(registry::get).collect(Collectors.toList()));
    }

    @Override
    public @NotNull IntCollection values() {
        return new IntArrayList(Streams.concat(dataMap.entrySet().stream().filter(v->!removed.contains(v.getKey())), added.entrySet().stream()).map(Map.Entry::getValue).map(mapper::applyAsInt).collect(Collectors.toList()));
    }

    @Override
    public @NotNull ObjectSet<Entry<K>> object2IntEntrySet() {
        return new ObjectArraySet<>(Streams.concat(dataMap.entrySet().stream().filter(v->!removed.contains(v.getKey())), added.entrySet().stream()).map(WrappedEntry::new).collect(Collectors.toSet()));
    }

    class WrappedEntry implements Entry<K> {
        private final K key;

        WrappedEntry(Map.Entry<ResourceKey<K>, D> inner) {
            this.key = registry.get(inner.getKey());
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public int getIntValue() {
            return Object2IntDataMapMap.this.get(key);
        }

        @Override
        public int setValue(int value) {
            return Object2IntDataMapMap.this.put(key, value);
        }
    }
}
