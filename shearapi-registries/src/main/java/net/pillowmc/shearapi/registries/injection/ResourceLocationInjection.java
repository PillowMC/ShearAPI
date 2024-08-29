package net.pillowmc.shearapi.registries.injection;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationInjection {
    int compareNamespaced(ResourceLocation o);
}
