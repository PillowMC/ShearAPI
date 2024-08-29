package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.ParrotImitation;
import net.pillowmc.shearapi.registries.datamaps.DataMapMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(Parrot.class)
public class ParrotMixin {
    @Shadow
    public static Map<EntityType<?>, SoundEvent> MOB_SOUND_MAP = new DataMapMap<>((MappedRegistry<EntityType<?>>) BuiltInRegistries.ENTITY_TYPE,
            NeoForgeDataMaps.PARROT_IMITATIONS, (Class<EntityType<?>>) (Class<?>) EntityType.class, ParrotImitation::sound, ParrotImitation::new);
}
