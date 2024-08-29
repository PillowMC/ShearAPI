package net.pillowmc.shearapi.attachment.mixin;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentInternals;
import net.neoforged.neoforge.attachment.AttachmentUtils;
import net.pillowmc.shearapi.attachment.IntoAttachmentHolder;
import net.pillowmc.shearapi.attachment.ItemStackInjection;
import net.pillowmc.shearapi.attachment.ItemStackNewConstructors;
import net.pillowmc.shearapi.utils.CommonVars;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;

import static net.neoforged.neoforge.attachment.AttachmentHolder.ATTACHMENTS_NBT_KEY;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IntoAttachmentHolder, ItemStackInjection {
    @Shadow
    @Unique
    @Nullable
    private CompoundTag tag;

    @Redirect(method = "method_28376", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P3;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/util/Function3;)Lcom/mojang/datafixers/kinds/App;"))
    private static <A> App<RecordCodecBuilder.Mu<ItemStack>, A> redirectClCODEC(Products.P3<RecordCodecBuilder.Mu<ItemStack>, Holder<Item>, Integer, Optional<CompoundTag>> p3, Applicative<RecordCodecBuilder.Mu<ItemStack>, ?> instance, Function3<Holder<Item>, Integer, Optional<CompoundTag>, ItemStack> function) {
        var p4 = p3.and(ExtraCodecs.strictOptionalField(CompoundTag.CODEC, ATTACHMENTS_NBT_KEY).forGetter(s -> Optional.ofNullable(s.getAttachmentHolder().serializeAttachments())));
        p4.apply(instance, ItemStackNewConstructors::newItemStack);
        return null;
    }

    @Redirect(method = "method_55067", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P2;apply(Lcom/mojang/datafixers/kinds/Applicative;Ljava/util/function/BiFunction;)Lcom/mojang/datafixers/kinds/App;"))
    private static <A> App<RecordCodecBuilder.Mu<ItemStack>, A> redirect_method_55067(Products.P2<RecordCodecBuilder.Mu<ItemStack>, Holder<Item>, Optional<CompoundTag>> p2, Applicative<RecordCodecBuilder.Mu<ItemStack>, ?> instance, BiFunction<Holder<Item>, Optional<CompoundTag>, ItemStack> function) {
        var p3 = p2.and(ExtraCodecs.strictOptionalField(TagParser.AS_CODEC, ATTACHMENTS_NBT_KEY).forGetter(s -> Optional.ofNullable(s.getAttachmentHolder().serializeAttachments())));
        p3.apply(instance, (holder, optional, tag) -> ItemStackNewConstructors.newItemStack(holder, 1, optional, tag));
        return null;
    }

    @Redirect(method = "method_55066", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P2;apply(Lcom/mojang/datafixers/kinds/Applicative;Ljava/util/function/BiFunction;)Lcom/mojang/datafixers/kinds/App;"))
    private static <A> App<RecordCodecBuilder.Mu<ItemStack>, A> redirect_method_55066(Products.P2<RecordCodecBuilder.Mu<ItemStack>, Holder<Item>, Integer> p2, Applicative<RecordCodecBuilder.Mu<ItemStack>, ?> instance, BiFunction<Holder<Item>, Integer, ItemStack> function) {
        var p4 = p2.and(ExtraCodecs.strictOptionalField(CommonVars.CraftingHelper$TAG_CODEC, "nbt").forGetter(stack -> java.util.Optional.ofNullable(CommonVars.CraftingHelper$getTagForWriting(stack))))
                .and(ExtraCodecs.strictOptionalField(CommonVars.CraftingHelper$TAG_CODEC, ATTACHMENTS_NBT_KEY).forGetter(s -> Optional.ofNullable(s.getAttachmentHolder().serializeAttachments())));
        p4.apply(instance, ItemStackNewConstructors::newItemStack);
        return null;
    }

    @Redirect(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;verifyTagAfterLoad(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void redirectVerifyTagAfterLoad(Item instance, CompoundTag compoundTag) {
        if (this.tag.contains(ATTACHMENTS_NBT_KEY, Tag.TAG_COMPOUND)) { // Neo: Read contained attachments
            this.getAttachmentHolder().deserializeAttachments(this.tag.getCompound(ATTACHMENTS_NBT_KEY));
            this.tag = AttachmentInternals.cleanTag(this.tag);
        }
        if (this.tag != null)
            instance.verifyTagAfterLoad(this.tag);
    }

    @Redirect(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;"))
    private Tag redirectPut(CompoundTag instance, String string, Tag tag) {
        tag = AttachmentInternals.addAttachmentsToTag(this.tag, (ItemStack)(Object) this, true);
        if (tag != null) {
            return instance.put("tag", tag);
        }
        return null;
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setPopTime(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectCopy(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        AttachmentUtils.copyStackAttachments((ItemStack)(Object) this, itemStack);
    }

    @Inject(method = "isSameItemSameTags", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private static void injectIsSameItemSameTags(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<Boolean> cir) {
        if (!itemStack.areAttachmentsCompatible(itemStack2)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean areAttachmentsCompatible(ItemStack other) {
        return AttachmentHolder.areAttachmentsCompatible(this.getAttachmentHolder(), other.getAttachmentHolder());
    }
}
