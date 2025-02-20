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
import net.neoforged.neoforge.registries.IRegistryExtension;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import net.neoforged.neoforge.registries.callback.ClearCallback;
import net.neoforged.neoforge.registries.callback.RegistryCallback;
import net.pillowmc.shearapi.registries.injection.MappedRegistryInjection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.HashMap;
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
//    @ApiStatus.Internal
    @Unique
    private final List<AddCallback<T>> addCallbacks = new ArrayList<>();
    @Unique
    protected final List<BakeCallback<T>> bakeCallbacks = new ArrayList<>();
//    @ApiStatus.Internal
    @Unique
    private final List<ClearCallback<T>> clearCallbacks = new ArrayList<>();
//    @ApiStatus.Internal
    @Unique
    private final Map<ResourceLocation, ResourceLocation> aliases = new HashMap<>();

    private int maxId = Integer.MAX_VALUE - 1;
    private boolean sync;

    @Override
    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @Override
    public boolean doesSync() {
        return this.sync;
    }

    @Override
    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    @Override
    public int getMaxId() {
        return this.maxId;
    }

    @Override
    public void addCallback(RegistryCallback<T> callback) {
        if (callback instanceof AddCallback<T> addCallback)
            this.addCallbacks.add(addCallback);
        if (callback instanceof BakeCallback<T> bakeCallback)
            this.bakeCallbacks.add(bakeCallback);
        if (callback instanceof ClearCallback<T> clearCallback)
            this.clearCallbacks.add(clearCallback);
    }

    @Override
    public void addAlias(ResourceLocation from, ResourceLocation to) {
        if (from.equals(to))
            return;
        if (this.aliases.containsKey(from)) {
            ResourceLocation old = this.aliases.get(from);
            if (!old.equals(to))
                throw new IllegalStateException("Duplicate alias with key \"" + from + "\" attempting to map to \"" + to + "\", found existing mapping \"" + old + "\"");
        }
        if (resolve(from).equals(to))
            throw new IllegalStateException("Infinite alias loop detected: from " + from + " to " + to);
        this.aliases.put(from, to);
    }

    @Override
    public ResourceLocation resolve(ResourceLocation name) {
        if (((MappedRegistry<T>)(Object)this).containsKey(name))
            return name;

        ResourceLocation alias = this.aliases.get(name);
        if (alias == null)
            return name;

        return resolve(alias);
    }

    @Override
    public ResourceKey<T> resolve(ResourceKey<T> key) {
        ResourceLocation resolvedName = resolve(key.location());
        // Try to reuse the key if possible
        return resolvedName == key.location() ? key : ResourceKey.create(((MappedRegistry<T>)(Object)this).key(), resolvedName);
    }

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
        this.addCallbacks.forEach(addCallback -> addCallback.onAdd((Registry<T>) this, i, resourceKey, object));
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
        this.clearCallbacks.forEach(clearCallback -> clearCallback.onClear((Registry<T>) this, full));
        this.aliases.clear();
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
