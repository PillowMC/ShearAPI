package net.pillowmc.shearapi.attachment;

import net.minecraft.world.item.ItemStack;

public interface ItemStackInjection {
    boolean areAttachmentsCompatible(ItemStack other);
}
