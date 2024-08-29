package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.pillowmc.shearapi.registries.datamaps.injections.IHolderInjection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Holder.Reference.class)
public abstract class HolderMixin<T> implements IHolderInjection<T> {
    @Shadow @Final private HolderOwner<T> owner;

    @Shadow public abstract ResourceKey<T> key();

    @Override
    public <A> A getData(DataMapType<T, A> type) {
        if (owner instanceof HolderLookup.RegistryLookup<T> lookup) {
            return lookup.getData(type, key());
        }
        return null;
    }
}
