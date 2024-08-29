package net.pillowmc.shearapi.registries;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.ModInitializer;
import org.objectweb.asm.tree.ClassNode;

public class ShearAPIRegistries implements ModInitializer {
    private static void addBaseMappedRegistry(ClassNode target) {
        // Sorry Fabric, but I have to do this.
        target.superName = "net/neoforged/neoforge/registries/BaseMappedRegistry";
    }
    @Override
    public void onInitialize() {
        ClassTinkerers.addTransformation("net/minecraft/core/MappedRegistry", ShearAPIRegistries::addBaseMappedRegistry);
    }
}
