package net.pillowmc.shearapi.registries.datamaps.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;
import net.pillowmc.shearapi.registries.datamaps.Object2IntDataMapMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.ToIntFunction;

@Mixin(VibrationSystem.class)
public class VibrationSystemMixin {
    @Shadow
    public static ToIntFunction<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = new Object2IntDataMapMap<>((MappedRegistry<GameEvent>) BuiltInRegistries.GAME_EVENT, NeoForgeDataMaps.VIBRATION_FREQUENCIES, GameEvent.class, VibrationFrequency::frequency, VibrationFrequency::new);
}
