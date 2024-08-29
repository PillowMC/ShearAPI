package net.pillowmc.shearapi.utils.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.pillowmc.shearapi.utils.IClientLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IClientLike {
    @Shadow
    @Unique
    public Level level;

    @Override
    public Level shearAPI$getClientLevel() {
        return level;
    }
}
