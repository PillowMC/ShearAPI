package net.pillowmc.shearapi.attachment;

import net.neoforged.neoforge.attachment.AttachmentHolder;

public interface IntoAttachmentHolder {
    default AttachmentHolder getAttachmentHolder() {
        return (AttachmentHolder)this;
    }
}
