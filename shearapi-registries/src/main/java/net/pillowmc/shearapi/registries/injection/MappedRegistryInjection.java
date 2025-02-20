package net.pillowmc.shearapi.registries.injection;

import net.minecraft.resources.ResourceKey;

public interface MappedRegistryInjection<T> {
    default void registerIdMapping(ResourceKey<T> key, int id) {
        throw new AssertionError("This should be implemented by mixin!");
    }
    default void clear(boolean full) {
        throw new AssertionError("This should be implemented by mixin!");
    }
    default void unfreeze() {
        throw new AssertionError("This should be implemented by mixin!");
    }
    default void setSync(boolean sync) {
        throw new AssertionError("This should be implemented by mixin!");
    }
    default void setMaxId(int maxId) {
        throw new AssertionError("This should be implemented by mixin!");
    }
}
