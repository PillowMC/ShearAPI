package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.IBaseMappedRegistryInjection;
import net.pillowmc.shearapi.registries.datamaps.injections.IRegistryExtensionInjection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(value = BaseMappedRegistry.class, remap = false)
public abstract class BaseMappedRegistryMixin<T> implements IRegistryExtensionInjection<T>, IBaseMappedRegistryInjection<T> {
    final Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> dataMaps = new IdentityHashMap<>();

    @Override
    public <A> @Nullable A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        final var innerMap = dataMaps.get(type);
        return innerMap == null ? null : (A) innerMap.get(key);
    }

    @Override
    public <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
        return (Map<ResourceKey<T>, A>) dataMaps.getOrDefault(type, Map.of());
    }

    @Override
    public Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> shearAPI$getDataMaps() {
        return dataMaps;
    }

    @Inject(method = "clear", at = @At("TAIL"), remap = false)
    public void injectClear(boolean full, CallbackInfo ci) {
        if (full) {
            dataMaps.clear();
        }
    }
}
