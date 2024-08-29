package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.GetDataInjection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HolderLookup.RegistryLookup.class)
public class RegistryLookupMixin<T> implements GetDataInjection<T> {
    @Override
    public <A> @Nullable A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        return null;
    }
}
