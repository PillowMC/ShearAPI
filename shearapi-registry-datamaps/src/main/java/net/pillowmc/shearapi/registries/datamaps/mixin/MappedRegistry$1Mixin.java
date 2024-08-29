package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.GetDataInjection;
import net.pillowmc.shearapi.registries.datamaps.injections.IRegistryExtensionInjection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = {"net/minecraft/core/MappedRegistry$1"})
public class MappedRegistry$1Mixin<T> implements GetDataInjection<T> {
    @Shadow @Final
    MappedRegistry field_36468;

    @Override
    public <A> @Nullable A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        return ((IRegistryExtensionInjection<T>)this.field_36468).getData(type, key);
    }
}
