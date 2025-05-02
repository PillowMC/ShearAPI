package net.pillowmc.shearapi.utils.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.Level;
import net.pillowmc.shearapi.utils.IClientLike;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements IClientLike {
    @Shadow
    public ClientLevel level;

    @Shadow
    @Nullable
    public abstract ClientPacketListener getConnection();

    @Override
    public void shearAPI$sendPacket(Packet<?> packet) {
        Objects.requireNonNull(this.getConnection()).send(packet);
    }

    @Override
    public Level shearAPI$getClientLevel() {
        return level;
    }
}
