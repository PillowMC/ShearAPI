package net.pillowmc.shearapi.registries.mixin;

import net.minecraft.resources.ResourceLocation;
import net.pillowmc.shearapi.registries.injection.ResourceLocationInjection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements ResourceLocationInjection {

    @Shadow public abstract String getNamespace();

    @Shadow public abstract String getPath();

    public int compareNamespaced(ResourceLocation o) {
        int ret = this.getNamespace().compareTo(o.getNamespace());
        return ret != 0 ? ret : this.getPath().compareTo(o.getPath());
    }
}
