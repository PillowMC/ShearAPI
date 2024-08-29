package net.pillowmc.shearapi.registries.datamaps.injections;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Map;

public interface IBaseMappedRegistryInjection<T> extends Registry<T> {
    Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> shearAPI$getDataMaps();
}
