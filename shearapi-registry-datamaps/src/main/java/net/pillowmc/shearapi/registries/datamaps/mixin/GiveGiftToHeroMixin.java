package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.ParrotImitation;
import net.neoforged.neoforge.registries.datamaps.builtin.RaidHeroGift;
import net.pillowmc.shearapi.registries.datamaps.DataMapMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(GiveGiftToHero.class)
public class GiveGiftToHeroMixin {
    @Shadow
    public static Map<VillagerProfession, ResourceLocation> GIFTS = new DataMapMap<>((MappedRegistry<VillagerProfession>) BuiltInRegistries.VILLAGER_PROFESSION,
            NeoForgeDataMaps.RAID_HERO_GIFTS, VillagerProfession.class, RaidHeroGift::lootTable, RaidHeroGift::new);
}
