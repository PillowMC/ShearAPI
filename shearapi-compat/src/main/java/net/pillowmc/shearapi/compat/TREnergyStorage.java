package net.pillowmc.shearapi.compat;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class TREnergyStorage implements IEnergyStorage {
    private final team.reborn.energy.api.EnergyStorage trStorage;

    public TREnergyStorage(team.reborn.energy.api.EnergyStorage tr) {
        this.trStorage = tr;
    }

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
        try (var t = Transaction.openOuter()) {
            var ret = this.trStorage.insert(ShearAPITransferCompat.FE2TR(maxReceive), t);
            if (!simulate) t.commit();
            return ShearAPITransferCompat.TR2FE(ret);
        }
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
        try (var t = Transaction.openOuter()) {
            var ret = this.trStorage.extract(ShearAPITransferCompat.FE2TR(maxExtract), t);
            if (!simulate) t.commit();
            return ShearAPITransferCompat.TR2FE(ret);
        }
	}

	@Override
	public int getEnergyStored() {
		return ShearAPITransferCompat.TR2FE(this.trStorage.getAmount());
	}

	@Override
	public int getMaxEnergyStored() {
		return ShearAPITransferCompat.TR2FE(this.trStorage.getCapacity());
	}

	@Override
	public boolean canExtract() {
		return this.trStorage.supportsExtraction();
	}

	@Override
	public boolean canReceive() {
		return this.trStorage.supportsInsertion();
	}
    
}
