package net.pillowmc.shearapi.utils;

import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;

public interface IClientLike {
    Level shearAPI$getClientLevel();
    default BlockableEventLoop<? super TickTask> intoBlockableEventLoop() {
        return (BlockableEventLoop<? super TickTask>)(Object) this;
    }
}
