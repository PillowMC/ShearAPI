package net.pillowmc.shearapi.registries.datamaps.mixin;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ComposterBlock;
import net.pillowmc.shearapi.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {
    @Redirect(method = {"use", "insertItem"}, at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z"))
    private static boolean redirectContainsKey(Object2FloatMap<Item> instance, Object o) {
        var value = Utils.getItemHolder((Item)o).get().getData(net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps.COMPOSTABLES);
        return value.chance() > 0.0F;
    }

    @Redirect(method = "addItem", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;getFloat(Ljava/lang/Object;)F"))
    private static float redirectGetFloat(Object2FloatMap<Item> instance, Object o) {
        var value = Utils.getItemHolder((Item)o).get().getData(net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps.COMPOSTABLES);
        return value.chance();
    }

    @Mixin(targets = {"net.minecraft.world.level.block.ComposterBlock.InputContainer"})
    static class InputContainerMixin {
        @Redirect(method = "canPlaceItemThroughFace", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z"))
        private static boolean redirectContainsKey(Object2FloatMap<Item> instance, Object o) {
            var value = Utils.getItemHolder((Item)o).get().getData(net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps.COMPOSTABLES);
            return value.chance() > 0.0F;
        }
    }
}
