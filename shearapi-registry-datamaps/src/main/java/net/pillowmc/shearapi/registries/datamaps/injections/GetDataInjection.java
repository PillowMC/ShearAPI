package net.pillowmc.shearapi.registries.datamaps.injections;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

public interface GetDataInjection<T> {
    /**
     * {@return the data map value attached with the object with the key, or {@code null} if there's no attached value}
     *
     * @param type the type of the data map
     * @param key  the object to get the value for
     * @param <A>  the data type
     */
    @Nullable
    <A> A getData(DataMapType<T, A> type, ResourceKey<T> key);
}
