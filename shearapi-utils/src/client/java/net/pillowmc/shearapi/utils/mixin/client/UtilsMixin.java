package net.pillowmc.shearapi.utils.mixin.client;

import net.minecraft.client.Minecraft;
import net.pillowmc.shearapi.utils.IClientLike;
import net.pillowmc.shearapi.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(value = Utils.class, remap = false)
public class UtilsMixin {
    /**
     * @author heipiao233
     * @reason Get client.
     */
    @Overwrite
    public static Optional<IClientLike> getClient() {
        return Optional.of((IClientLike) Minecraft.getInstance());
    }
}
