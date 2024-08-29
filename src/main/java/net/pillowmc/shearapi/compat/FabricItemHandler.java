//package net.pillowmc.shearapi.compat;
//
//import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
//import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
//import net.minecraft.world.item.ItemStack;
//import net.neoforged.neoforge.items.IItemHandler;
//
//public class FabricItemHandler implements IItemHandler {
//    private Storage<ItemVariant> fabric;
//
//    public FabricItemHandler(Storage<ItemVariant> fabric) {
//        this.fabric = fabric;
//    }
//
//    @Override
//    public int getSlots() {
//        return 0;
//    }
//
//    @Override
//    public ItemStack getStackInSlot(int slot) {
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
//        return fabric.insert();
//    }
//
//    @Override
//    public ItemStack extractItem(int slot, int amount, boolean simulate) {
//        return null;
//    }
//
//    @Override
//    public int getSlotLimit(int slot) {
//        return 0;
//    }
//
//    @Override
//    public boolean isItemValid(int slot, ItemStack stack) {
//        return false;
//    }
//}
