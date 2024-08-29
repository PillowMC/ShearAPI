/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.attachment;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.pillowmc.shearapi.attachment.IntoAttachmentHolder;

public final class AttachmentUtils {
    private AttachmentUtils() {}

    // ShearAPI: use IntoAttachmentHolder.
    /**
     * Copy some attachments to another holder.
     */
    public static <H extends IntoAttachmentHolder> void copyAttachments(H from_, H to_, Predicate<AttachmentType<?>> filter) {
        AttachmentHolder from = from_.getAttachmentHolder(), to = to_.getAttachmentHolder();
        if (from.attachments == null) {
            return;
        }
        for (var entry : from.attachments.entrySet()) {
            AttachmentType<?> type = entry.getKey();
            if (type.serializer == null) {
                continue;
            }
            @SuppressWarnings("unchecked")
            var copyHandler = (IAttachmentCopyHandler<Object>) type.copyHandler;
            if (filter.test(type)) {
                Object copy = copyHandler.copy(to.getExposedHolder(), entry.getValue());
                if (copy != null) {
                    to.getAttachmentMap().put(type, copy);
                }
            }
        }
    }

    public static void copyStackAttachments(ItemStack from, ItemStack to) {
        copyAttachments(from, to, type -> true);
    }

    public static void copyChunkAttachmentsOnPromotion(AttachmentHolder.AsField from, AttachmentHolder.AsField to) {
        copyAttachments(from, to, type -> true);
    }
}
