package net.pillowmc.shearapi.capabilities;

import net.fabricmc.api.ModInitializer;
import net.neoforged.neoforge.capabilities.CapabilityHooks;

public class ShearAPICapabilitiesInit implements ModInitializer {

    @Override
    public void onInitialize() {
        CapabilityHooks.init();
    }
    
}
