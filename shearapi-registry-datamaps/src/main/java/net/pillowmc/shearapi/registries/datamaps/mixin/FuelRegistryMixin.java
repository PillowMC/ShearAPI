package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.pillowmc.shearapi.registries.datamaps.DataMapFuelRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FuelRegistry.class)
public class FuelRegistryMixin {
    @Shadow
    public static FuelRegistry INSTANCE = new DataMapFuelRegistry();
}
