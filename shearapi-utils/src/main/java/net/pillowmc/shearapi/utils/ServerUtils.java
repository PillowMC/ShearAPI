package net.pillowmc.shearapi.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerUtils {
    private static MinecraftServer currentServer = null;
    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    static {
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> currentServer = server);
    }
}
