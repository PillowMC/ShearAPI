/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.registries;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.pillowmc.shearapi.attachment.ShearAPIAttachmentInit;
import net.pillowmc.shearapi.conditons.ShearAPIConditionsInit;
import net.pillowmc.shearapi.holderset.ShearAPIHolderSetInit;

/**
 * A class that exposes static references to NeoForge registries.
 * It is still advised that you register things with {@link RegisterEvent} or {@link DeferredRegister}, but queries and iterations can use this.
 *
 * <p>Vanilla's registries can be found in {@link BuiltInRegistries}, and their keys in {@link Registries}.
 */
public class NeoForgeRegistries {
    // Custom NeoForge registries
//    public static final Registry<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = new RegistryBuilder<>(Keys.ENTITY_DATA_SERIALIZERS).sync(true).create();
//    public static final Registry<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = new RegistryBuilder<>(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS).create();
//    public static final Registry<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = new RegistryBuilder<>(Keys.BIOME_MODIFIER_SERIALIZERS).create();
//    public static final Registry<Codec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = new RegistryBuilder<>(Keys.STRUCTURE_MODIFIER_SERIALIZERS).create();
//    public static final Registry<FluidType> FLUID_TYPES = new RegistryBuilder<>(Keys.FLUID_TYPES).create();
    public static final Registry<HolderSetType> HOLDER_SET_TYPES = ShearAPIHolderSetInit.HOLDER_SET_TYPES;
//    public static final Registry<ItemDisplayContext> DISPLAY_CONTEXTS = new RegistryBuilder<>(Keys.DISPLAY_CONTEXTS)
//            .sync(true)
//            .maxId(128 * 2) // 0 -> 127 gets positive ID, 128 -> 256 gets negative ID
//            .defaultKey(new ResourceLocation("none"))
//            .create();
//    public static final Registry<IngredientType<?>> INGREDIENT_TYPES = new RegistryBuilder<>(Keys.INGREDIENT_TYPES).create();
    public static final Registry<Codec<? extends ICondition>> CONDITION_SERIALIZERS = ShearAPIConditionsInit.CONDITION_SERIALIZERS;
//    public static final Registry<Codec<? extends ICustomEntityPredicate>> ENTITY_PREDICATE_SERIALIZERS = new RegistryBuilder<>(Keys.ENTITY_PREDICATE_SERIALIZERS).create();
//    public static final Registry<Codec<? extends ICustomItemPredicate>> ITEM_PREDICATE_SERIALIZERS = new RegistryBuilder<>(Keys.ITEM_PREDICATE_SERIALIZERS).create();
    public static final Registry<AttachmentType<?>> ATTACHMENT_TYPES = ShearAPIAttachmentInit.ATTACHMENT_TYPES;

    // Reminder: If you add a registry to NeoForge itself, remember to add it to NeoForgeRegistriesSetup#registerRegistries.

    public static final class Keys {
        // NeoForge
//        public static final ResourceKey<Registry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS = key("entity_data_serializers");
//        public static final ResourceKey<Registry<Codec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = key("global_loot_modifier_serializers");
//        public static final ResourceKey<Registry<Codec<? extends BiomeModifier>>> BIOME_MODIFIER_SERIALIZERS = key("biome_modifier_serializers");
//        public static final ResourceKey<Registry<Codec<? extends StructureModifier>>> STRUCTURE_MODIFIER_SERIALIZERS = key("structure_modifier_serializers");
//        public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = key("fluid_type");
        public static final ResourceKey<Registry<HolderSetType>> HOLDER_SET_TYPES = ShearAPIHolderSetInit.HOLDER_SET_TYPES_KEY;
//        public static final ResourceKey<Registry<ItemDisplayContext>> DISPLAY_CONTEXTS = key("display_contexts");
//        public static final ResourceKey<Registry<IngredientType<?>>> INGREDIENT_TYPES = key("ingredient_serializer");
        public static final ResourceKey<Registry<Codec<? extends ICondition>>> CONDITION_CODECS = ShearAPIConditionsInit.CONDITION_CODECS_KEY;
//        public static final ResourceKey<Registry<Codec<? extends ICustomEntityPredicate>>> ENTITY_PREDICATE_SERIALIZERS = key("entity_predicates");
//        public static final ResourceKey<Registry<Codec<? extends ICustomItemPredicate>>> ITEM_PREDICATE_SERIALIZERS = key("item_predicates");
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPES = ShearAPIAttachmentInit.ATTACHMENT_TYPES_KEY;

        // NeoForge Dynamic
//        public static final ResourceKey<Registry<BiomeModifier>> BIOME_MODIFIERS = key("biome_modifier");
//        public static final ResourceKey<Registry<StructureModifier>> STRUCTURE_MODIFIERS = key("structure_modifier");
    }
}
