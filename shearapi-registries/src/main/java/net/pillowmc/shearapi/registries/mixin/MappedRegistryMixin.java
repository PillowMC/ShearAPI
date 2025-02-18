package net.pillowmc.shearapi.registries.mixin;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import net.neoforged.neoforge.registries.IRegistryExtension;
import net.pillowmc.shearapi.registries.injection.MappedRegistryInjection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> implements IRegistryExtension<T>, MappedRegistryInjection<T> {
    @Shadow public abstract int getId(@Nullable T object);

    @Shadow @Nullable public abstract T get(@Nullable ResourceKey<T> resourceKey);

    @Shadow @Nullable public abstract T get(@Nullable ResourceLocation resourceLocation);

    @Shadow @Final private Map<T, Holder.Reference<T>> byValue;

    @Shadow protected abstract void validateWrite();

    @Shadow @Final private ObjectList<Holder.Reference<T>> byId;

    @Shadow @Final private Reference2IntMap<T> toId;

    @Shadow private int nextId;

    @Shadow @Nullable private List<Holder.Reference<T>> holdersInOrder;

    @Shadow @Final private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Shadow @Final private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow private volatile Map<TagKey<T>, HolderSet.Named<T>> tags;

    @Shadow @Final private Map<T, Lifecycle> lifecycles;

    @Shadow @Nullable private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    @Shadow protected abstract void validateWrite(ResourceKey<T> resourceKey);

    @Shadow @Nullable public abstract ResourceLocation getKey(T object);

    @Shadow private boolean frozen;

    @Override
    public int getId(ResourceKey<T> key) {
        return getId(get(key));
    }

    @Override
    public int getId(ResourceLocation name) {
        return getId(get(name));
    }

    @Override
    public boolean containsValue(Object value) {
        return byValue.containsKey(value);
    }

    @Inject(method = "registerMapping", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z", ordinal = 0))
    public void injectRegisterMapping(int i, ResourceKey<T> resourceKey, T object, Lifecycle lifecycle, CallbackInfoReturnable<Holder.Reference<T>> cir) {
        if (i > this.getMaxId())
            throw new IllegalStateException(String.format(java.util.Locale.ENGLISH, "Invalid id %d - maximum id range of %d exceeded.", i, this.getMaxId()));
    }

    @Inject(method = "registerMapping", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;", by = 3, shift = At.Shift.BY), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectRegisterMappingBind(int i, ResourceKey<T> resourceKey, T object, Lifecycle lifecycle, CallbackInfoReturnable<Holder.Reference<T>> cir, Holder.Reference<T> reference) {
        reference.bindValue(object);
    }

    @Inject(method = "registerMapping", at = @At(value = "RETURN"))
    public void injectRegisterMappingBind(int i, ResourceKey<T> resourceKey, T object, Lifecycle lifecycle, CallbackInfoReturnable<Holder.Reference<T>> cir) {
        ((BaseMappedRegistry<T>)(Object)this).addCallbacks.forEach(addCallback -> addCallback.onAdd((Registry<T>) this, i, resourceKey, object));
    }

    @Redirect(method = "registerMapping", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectList;size(I)V", remap = false))
    public void redirectRegisterMappingSize(ObjectList<T> instance, int i) {
        while (this.byId.size() < i) this.byId.add(null);
    }

    @ModifyArg(method = "get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object modifyArgGet(Object key) {
        return resolve((ResourceKey<T>) key);
    }

    @ModifyArg(method = "getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object modifyArgGetHolder(Object key) {
        return resolve((ResourceKey<T>) key);
    }

    @ModifyArg(method = "getOrCreateHolderOrThrow", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"))
    public Object modifyArgGetOrCreateHolderOrThrow(Object key) {
        return resolve((ResourceKey<T>) key);
    }

    @ModifyArg(method = "get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object modifyArgGetByLocation(Object key) {
        return resolve((ResourceLocation) key);
    }

    @Redirect(method = "freeze", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    public void redirectForEachInFreeze(Map<T, Holder.Reference<T>> instance, BiConsumer<T, Holder.Reference<T>> v) {
    }

    @Redirect(method = "freeze", at = @At(value = "FIELD", target = "Lnet/minecraft/core/MappedRegistry;unregisteredIntrusiveHolders:Ljava/util/Map;", ordinal = 3))
    public void redirectUnregisteredIntrusiveHoldersInFreeze(MappedRegistry<T> instance, Map<T, Holder.Reference<T>> value) {
        // Neo: We freeze/unfreeze vanilla registries more than once, so we need to keep the unregistered intrusive holders map around.
    }

    @Override
    public void clear(boolean full) {
        validateWrite();
        ((BaseMappedRegistry<T>)(Object)this).clearCallbacks.forEach(clearCallback -> clearCallback.onClear((Registry<T>) this, full));
        ((BaseMappedRegistry<T>)(Object)this).aliases.clear();
        byId.clear();
        toId.clear();
        nextId = 0;
        if (holdersInOrder != null) holdersInOrder = null;
        if (full) {
            byLocation.clear();
            byKey.clear();
            byValue.clear();
            tags.clear();
            lifecycles.clear();
            if (unregisteredIntrusiveHolders != null) {
                unregisteredIntrusiveHolders.clear();
                unregisteredIntrusiveHolders = null;
            }
        }
    }

    @Override
    public void registerIdMapping(ResourceKey<T> key, int id) {
        this.validateWrite(key);
        if (id > this.getMaxId())
            throw new IllegalStateException(String.format(java.util.Locale.ENGLISH, "Invalid id %d - maximum id range of %d exceeded.", id, this.getMaxId()));
        if (0 <= id && id < this.byId.size() && this.byId.get(id) != null) { // Don't use byId() method, it will return the default value if the entry is absent
            throw new IllegalStateException("Duplicate id " + id + " for " + key + " and " + this.getKey(this.byId.get(id).value()));
        }
        if (this.nextId <= id) {
            this.nextId = id + 1;
        }
        var holder = byKey.get(key);
        while (this.byId.size() < (id + 1)) this.byId.add(null);
        this.byId.set(id, holder);
        this.toId.put(holder.value(), id);
    }

    @Override
    public void unfreeze() {
        frozen = false;
    }
}
