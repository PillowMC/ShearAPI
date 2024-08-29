package net.pillowmc.shearapi.utils;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum CommonVars {
    ;
    public static final Codec<CompoundTag> CraftingHelper$TAG_CODEC = ExtraCodecs.withAlternative(TagParser.AS_CODEC, net.minecraft.nbt.CompoundTag.CODEC);

    @Nullable
    public static CompoundTag CraftingHelper$getTagForWriting(ItemStack stack) {
        // Check if not writing the NBT would still give the correct item.
        // Just checking for tag != null is not enough: damageable items get a tag set in the stack constructor,
        // but we don't want to write it to the recipe file.
        if (stack.getTag() == null || stack.getTag().equals(new ItemStack(stack.getItem(), stack.getCount()).getTag())) {
            return null;
        } else {
            return stack.getTag();
        }
    }
}
