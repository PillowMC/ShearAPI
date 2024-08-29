package net.pillowmc.shearapi.registries.mixin;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.IRegistryExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Registry.class)
public interface RegistryMixin<T> extends IRegistryExtension<T> {
}
