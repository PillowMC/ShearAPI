/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.attachment;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class AttachmentInternals {
    /**
     * Marks a stack that has attachments and an empty NBT tag ({}).
     * If this marker is absent, we set the tag back to null after reading the attachments.
     */
    private static final String EMPTY_TAG_KEY = "neoforge:empty";

    @Nullable
    public static CompoundTag addAttachmentsToTag(@Nullable CompoundTag tag, ItemStack stack, boolean fullCopy) {
        // Store all serializable attachments as an nbt subtag
        CompoundTag attachmentsTag = stack.getAttachmentHolder().serializeAttachments();
        if (attachmentsTag != null) {
            if (tag == null)
                tag = new CompoundTag();
            else {
                tag = tag.copy();
                if (tag.isEmpty()) // Make sure we can differentiate between null and empty.
                    tag.putBoolean(EMPTY_TAG_KEY, true);
            }
            tag.put(AttachmentHolder.ATTACHMENTS_NBT_KEY, attachmentsTag);
        } else if (fullCopy && tag != null)
            tag = tag.copy();
        return tag;
    }

    /**
     * Perform the inverse operation of {@link #addAttachmentsToTag} on stack reception.
     */
    public static ItemStack reconstructItemStack(Item item, int count, @Nullable CompoundTag tag) {
        ItemStack itemstack;
        if (tag != null && tag.contains(AttachmentHolder.ATTACHMENTS_NBT_KEY, Tag.TAG_COMPOUND)) {
            // Read serialized caps
            itemstack = new ItemStack(item, count);
            itemstack.getAttachmentHolder().deserializeAttachments(tag.getCompound(AttachmentHolder.ATTACHMENTS_NBT_KEY));
            tag = cleanTag(tag);
        } else {
            itemstack = new ItemStack(item, count);
        }
        itemstack.setTag(tag);
        return itemstack;
    }

    /**
     * Clean tag of its contained attachments (as set by {@link #addAttachmentsToTag}).
     */
    @Nullable
    public static CompoundTag cleanTag(CompoundTag tag) {
        tag.remove(AttachmentHolder.ATTACHMENTS_NBT_KEY);
        // If the tag is now empty and the empty marker is absent, replace by null.
        if (tag.contains(EMPTY_TAG_KEY))
            tag.remove(EMPTY_TAG_KEY);
        else if (tag.isEmpty())
            tag = null;
        return tag;
    }

    public static void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        AttachmentUtils.copyAttachments(oldPlayer, newPlayer, alive ? type -> true : type -> type.copyOnDeath);
    }

    @SubscribeEvent
    public static void onLivingConvert(LivingConversionEvent.Post event) {
        AttachmentUtils.copyAttachments(event.getEntity(), event.getOutcome(), type -> type.copyOnDeath);
    }

    static {
        ServerPlayerEvents.COPY_FROM.register(AttachmentInternals::onPlayerClone);
    }

    private AttachmentInternals() {}
}
