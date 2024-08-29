package net.pillowmc.shearapi.compat;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.neoforged.neoforge.energy.IEnergyStorage;
import team.reborn.energy.api.EnergyStorage;

public class NeoForgeEnergyStorage implements EnergyStorage {
    private IEnergyStorage neo;

    public NeoForgeEnergyStorage(IEnergyStorage neo) {
        this.neo = neo;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        int received = neo.receiveEnergy(ShearAPITransferCompat.TR2FE(maxAmount), true);
        transaction.addOuterCloseCallback(result -> neo.receiveEnergy(ShearAPITransferCompat.TR2FE(maxAmount), false));
        return received;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        int extracted = neo.extractEnergy(ShearAPITransferCompat.TR2FE(maxAmount), true);
        transaction.addOuterCloseCallback(result -> neo.extractEnergy(ShearAPITransferCompat.TR2FE(maxAmount), false));
        return extracted;
    }

    @Override
    public long getAmount() {
        return ShearAPITransferCompat.FE2TR(neo.getEnergyStored());
    }

    @Override
    public long getCapacity() {
        return ShearAPITransferCompat.FE2TR(neo.getMaxEnergyStored());
    }

    @Override
    public boolean supportsExtraction() {
        return neo.canExtract();
    }

    @Override
    public boolean supportsInsertion() {
        return neo.canReceive();
    }
}
