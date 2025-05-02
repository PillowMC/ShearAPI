package net.pillowmc.shearapi.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.Optional;

public enum Utils {;
    public static Optional<Holder<Item>> getItemHolder(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).flatMap(BuiltInRegistries.ITEM::getHolder);
    }

    public static Optional<IClientLike> getClient() {
        return Optional.empty();
    }
}
