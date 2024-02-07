package net.pillowmc.shearapi.compat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import team.reborn.energy.api.EnergyStorage;

public class ShearAPITransferCompat implements ModInitializer {
    @Override
    public void onInitialize() {
        ShearAPIRuntime.getRuntime().getModBus().addListener(this::onRegisterCapabilities);
    }

    private final void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerGenericBlock(Capabilities.EnergyStorage.BLOCK, (level, pos, state, blockEntity, context) -> {
            return new TREnergyStorage(EnergyStorage.SIDED.getProvider(state.getBlock()).find(level, pos, state, blockEntity, null));
        });
        event.registerGenericItem(Capabilities.EnergyStorage.ITEM, (stack, __) -> {
            return new TREnergyStorage(ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM));
        });
    }

    public static int TR2FE(long tr) {
        return (int)(tr * 10);
    }

    public static long FE2TR(int fe) {
        return (int)(fe / 10);
    }
}
