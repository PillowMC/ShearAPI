package net.pillowmc.shearapi.conditons;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;

public class ShearAPIConditionsInit implements ModInitializer {
    public static final ResourceKey<Registry<Codec<? extends ICondition>>> CONDITION_CODECS_KEY = ResourceKey.createRegistryKey(new ResourceLocation(ShearAPIRuntime.MOD_ID, "condition_codecs"));
    public static final Registry<Codec<? extends ICondition>> CONDITION_SERIALIZERS = new RegistryBuilder<>(CONDITION_CODECS_KEY).create();
    private static final DeferredRegister<Codec<? extends ICondition>> CONDITION_CODECS_REGISTER = DeferredRegister.create(CONDITION_SERIALIZERS, ShearAPIRuntime.MOD_ID);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<AndCondition>> AND_CONDITION = CONDITION_CODECS_REGISTER.register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<FalseCondition>> FALSE_CONDITION = CONDITION_CODECS_REGISTER.register("false", () -> FalseCondition.CODEC);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<ItemExistsCondition>> ITEM_EXISTS_CONDITION = CONDITION_CODECS_REGISTER.register("item_exists", () -> ItemExistsCondition.CODEC);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<ModLoadedCondition>> MOD_LOADED_CONDITION = CONDITION_CODECS_REGISTER.register("mod_loaded", () -> ModLoadedCondition.CODEC);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<NotCondition>> NOT_CONDITION = CONDITION_CODECS_REGISTER.register("not", () -> NotCondition.CODEC);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<OrCondition>> OR_CONDITION = CONDITION_CODECS_REGISTER.register("or", () -> OrCondition.CODEC);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<TagEmptyCondition>> TAG_EMPTY_CONDITION = CONDITION_CODECS_REGISTER.register("tag_empty", () -> TagEmptyCondition.CODEC);
    public static final DeferredHolder<Codec<? extends ICondition>, Codec<TrueCondition>> TRUE_CONDITION = CONDITION_CODECS_REGISTER.register("true", () -> TrueCondition.CODEC);
    @Override
    public void onInitialize() {
        CONDITION_CODECS_REGISTER.register(ShearAPIRuntime.getRuntime().getModBus());
    }
}
