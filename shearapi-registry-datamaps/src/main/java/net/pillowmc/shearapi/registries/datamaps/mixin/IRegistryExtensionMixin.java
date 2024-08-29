package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.neoforged.neoforge.registries.IRegistryExtension;
import net.pillowmc.shearapi.registries.datamaps.injections.IRegistryExtensionInjection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IRegistryExtension.class)
public interface IRegistryExtensionMixin<T> extends IRegistryExtensionInjection<T> {
}
