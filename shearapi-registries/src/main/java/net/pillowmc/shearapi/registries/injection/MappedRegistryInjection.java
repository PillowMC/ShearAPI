package net.pillowmc.shearapi.registries.injection;

import net.minecraft.resources.ResourceKey;

public interface MappedRegistryInjection<T> {
    void registerIdMapping(ResourceKey<T> key, int id);
    void clear(boolean full);
    void unfreeze();
}
