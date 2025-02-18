package net.pillowmc.shearapi.holderset;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.holdersets.*;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;

public class ShearAPIHolderSetInit implements ModInitializer {
    public static final ResourceKey<Registry<HolderSetType>> HOLDER_SET_TYPES_KEY = ResourceKey.createRegistryKey(new ResourceLocation(ShearAPIRuntime.MOD_ID, "holder_set_type"));
    public static final Registry<HolderSetType> HOLDER_SET_TYPES = new RegistryBuilder<>(HOLDER_SET_TYPES_KEY).create();
    private static final DeferredRegister<HolderSetType> HOLDER_SET_TYPES_REGISTER = DeferredRegister.create(HOLDER_SET_TYPES, ShearAPIRuntime.MOD_ID);

    /**
     * Stock holder set type that represents any/all values in a registry. Can be used in a holderset object with {@code { "type": "neoforge:any" }}
     */
    public static final Holder<HolderSetType> ANY_HOLDER_SET = HOLDER_SET_TYPES_REGISTER.register("any", () -> AnyHolderSet::codec);

    /**
     * Stock holder set type that represents an intersection of other holdersets. Can be used in a holderset object with {@code { "type": "neoforge:and", "values": [list of holdersets] }}
     */
    public static final Holder<HolderSetType> AND_HOLDER_SET = HOLDER_SET_TYPES_REGISTER.register("and", () -> AndHolderSet::codec);

    /**
     * Stock holder set type that represents a union of other holdersets. Can be used in a holderset object with {@code { "type": "neoforge:or", "values": [list of holdersets] }}
     */
    public static final Holder<HolderSetType> OR_HOLDER_SET = HOLDER_SET_TYPES_REGISTER.register("or", () -> OrHolderSet::codec);

    /**
     * <p>Stock holder set type that represents all values in a registry except those in another given set.
     * Can be used in a holderset object with {@code { "type": "neoforge:not", "value": holderset }}</p>
     */
    public static final Holder<HolderSetType> NOT_HOLDER_SET = HOLDER_SET_TYPES_REGISTER.register("not", () -> NotHolderSet::codec);

    @Override
    public void onInitialize() {
        HOLDER_SET_TYPES_REGISTER.register(ShearAPIRuntime.getRuntime().getModBus());
    }
}
