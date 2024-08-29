package net.pillowmc.shearapi.player.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.storage.PlayerDataStorage;

@Mixin(PlayerDataStorage.class)
public interface PlayerDataStorageMixin {
    @Accessor("playerDir")
    File getPlayerDataFolder();
}
