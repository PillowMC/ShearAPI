package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.pillowmc.shearapi.registries.datamaps.DataMapCompostingChanceRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CompostingChanceRegistry.class, remap = false)
public class CompostingChanceRegistryMixin {
    @Shadow(remap = false)
    public static CompostingChanceRegistry INSTANCE = new DataMapCompostingChanceRegistry();
}
