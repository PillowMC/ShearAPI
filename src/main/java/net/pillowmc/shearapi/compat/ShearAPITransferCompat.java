package net.pillowmc.shearapi.compat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import team.reborn.energy.api.EnergyStorage;

public class ShearAPITransferCompat implements ModInitializer {
    @Override
    public void onInitialize() {
        ShearAPIRuntime.getRuntime().getModBus().addListener(this::onRegisterCapabilities);
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.shearapi$registerGenericBlock(Capabilities.EnergyStorage.BLOCK, (level, pos, state, blockEntity, context) -> {
            var storage = EnergyStorage.SIDED.find(level, pos, state, blockEntity, null);
            if (storage instanceof NeoForgeEnergyStorage) {
                return null;
            }
            return new TREnergyStorage(storage);
        });
//        event.shearapi$registerGenericBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, context) -> new FabricItemHandler(ItemStorage.SIDED.find(level, pos, state, blockEntity, null)));
        event.shearapi$registerGenericItem(Capabilities.EnergyStorage.ITEM, (stack, __) -> {
            var storage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
            if (storage instanceof NeoForgeEnergyStorage) {
                return null;
            }
            return new TREnergyStorage(storage);
        });

        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> new NeoForgeEnergyStorage(Capabilities.EnergyStorage.BLOCK.shearapi$getCapabilityWithoutGeneric(world, pos, state, blockEntity, context)));
        EnergyStorage.ITEM.registerFallback((itemStack, context) -> new NeoForgeEnergyStorage(Capabilities.EnergyStorage.ITEM.shearapi$getCapabilityWithoutGeneric(itemStack, null)));
    }

    public static int TR2FE(long tr) {
        return (int)(tr * 10);
    }

    public static long FE2TR(int fe) {
        return fe / 10;
    }
}
