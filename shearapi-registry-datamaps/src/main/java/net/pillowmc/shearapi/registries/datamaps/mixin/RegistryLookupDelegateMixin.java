package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.GetDataInjection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HolderLookup.RegistryLookup.Delegate.class)
public abstract class RegistryLookupDelegateMixin<T> implements GetDataInjection<T> {
    @Shadow protected abstract HolderLookup.RegistryLookup<T> parent();

    @Override
    public <A> @Nullable A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        return this.parent().getData(type, key);
    }
}
