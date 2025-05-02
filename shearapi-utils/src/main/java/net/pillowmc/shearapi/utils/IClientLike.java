package net.pillowmc.shearapi.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;

public interface IClientLike {
    Level shearAPI$getClientLevel();
    void shearAPI$sendPacket(Packet<?> packet);
    default BlockableEventLoop<? super TickTask> intoBlockableEventLoop() {
        return (BlockableEventLoop<? super TickTask>) this;
    }
}
