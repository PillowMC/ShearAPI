package net.pillowmc.shearapi.conditions.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin {
    @Redirect(method = "method_53274", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;listOf()Lcom/mojang/serialization/Codec;", ordinal = 1))
    private static Codec<List<LootItemFunction>> redirectLootItemFunctions(Codec<LootItemFunction> instance) {
        return ConditionalOps.decodeListWithElementConditions(instance);
    }
}
