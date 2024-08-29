package net.pillowmc.shearapi.attachment;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Optional;

public class ItemStackNewConstructors {
    public static ItemStack newItemStack(ItemLike item, int count, Optional<CompoundTag> tag) {
        var stack = new ItemStack(item, count);
        tag.ifPresent(stack::setTag);
        return stack;
    }

    @ApiStatus.Internal
    public static ItemStack newItemStack(Holder<Item> item, int count, Optional<CompoundTag> tag, Optional<CompoundTag> attachmentsNbt) {
        var stack = new ItemStack(item, count, tag);
        attachmentsNbt.ifPresent(compoundTag -> stack.getAttachmentHolder().deserializeAttachments(compoundTag));
        return stack;
    }

    @ApiStatus.Internal
    public static ItemStack newItemStack(ItemLike item, int count, Optional<CompoundTag> tag, Optional<CompoundTag> attachmentsNbt) {
        var stack = newItemStack(item, count, tag);
        attachmentsNbt.ifPresent(compoundTag -> stack.getAttachmentHolder().deserializeAttachments(compoundTag));
        return stack;
    }

    public static ItemStack newItemStack(ItemLike item, int count, @Nullable CompoundTag attachmentsNbt) {
        var stack = new ItemStack(item, count);
        if (attachmentsNbt != null ) {
            stack.getAttachmentHolder().deserializeAttachments(attachmentsNbt);
        }
        return stack;
    }

    // Used in asm.
    @ApiStatus.Internal
    public static void setAttachment(ItemStack stack, @Nullable CompoundTag attachmentsNbt) {
        if (attachmentsNbt != null ) {
            stack.getAttachmentHolder().deserializeAttachments(attachmentsNbt);
        }
    }
}
