package net.pillowmc.shearapi.registries.datamaps.injections;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IRegistryExtensionInjection<T> extends GetDataInjection<T> {

    /**
     * {@return the data map of the given {@code type}}
     *
     * @param <A> the data type
     */
    <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type);
}
