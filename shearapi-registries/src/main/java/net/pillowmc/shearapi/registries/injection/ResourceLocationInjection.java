package net.pillowmc.shearapi.registries.injection;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationInjection {
    default int compareNamespaced(ResourceLocation o) {
        throw new AssertionError("This should be implemented by mixin!");
    }
}
