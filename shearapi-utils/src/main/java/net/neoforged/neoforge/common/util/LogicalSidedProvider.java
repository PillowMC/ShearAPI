/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.common.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.pillowmc.shearapi.utils.IClientLike;

public class LogicalSidedProvider<T> {
    public static final LogicalSidedProvider<BlockableEventLoop<? super TickTask>> WORKQUEUE = new LogicalSidedProvider<>((c) -> c.get().intoBlockableEventLoop(), Supplier::get);
    public static final LogicalSidedProvider<Optional<Level>> CLIENTWORLD = new LogicalSidedProvider<>((c) -> Optional.of(c.get().shearAPI$getClientLevel()), (s) -> Optional.empty());

    private static Supplier<IClientLike> client;
    private static Supplier<MinecraftServer> server;

    // INTERNAL, DO  NOT CALL
    public static void setClient(Supplier<IClientLike> client) {
        LogicalSidedProvider.client = client;
    }

    public static void setServer(Supplier<MinecraftServer> server) {
        LogicalSidedProvider.server = server;
    }

    private LogicalSidedProvider(Function<Supplier<IClientLike>, T> clientSide, Function<Supplier<MinecraftServer>, T> serverSide) {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    private final Function<Supplier<IClientLike>, T> clientSide;
    private final Function<Supplier<MinecraftServer>, T> serverSide;

    public T get(final LogicalSide side) {
        return side == LogicalSide.CLIENT ? clientSide.apply(client) : serverSide.apply(server);
    }
}
