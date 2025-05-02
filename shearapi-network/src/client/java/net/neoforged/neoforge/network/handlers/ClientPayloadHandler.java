/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.network.handlers;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.common.world.LevelChunkAuxiliaryLightManager;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.payload.AdvancedContainerSetDataPayload;
import net.neoforged.neoforge.network.payload.AdvancedOpenScreenPayload;
import net.neoforged.neoforge.network.payload.AuxiliaryLightDataPayload;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    private ClientPayloadHandler() {}

//    public void handle(TierSortingRegistryPayload payload, IPayloadContext context) {
//        TierSortingRegistry.handleSync(payload, context);
//    }

    public void handle(AdvancedOpenScreenPayload msg, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(msg.additionalData()));
            try {
                createMenuScreen(msg.name(), msg.menuType(), msg.windowId(), buf);
            } finally {
                buf.release();
            }
        }).exceptionally(e -> {
            context.packetHandler().disconnect(Component.translatable("neoforge.network.advanced_open_screen.failed", e.getMessage()));
            return null;
        });
    }

    private static <T extends AbstractContainerMenu> void createMenuScreen(Component name, MenuType<T> menuType, int windowId, FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        MenuScreens.getScreenFactory(menuType, mc, windowId, name).ifPresent(f -> {
            Screen s = f.create(menuType.create(windowId, mc.player.getInventory(), buf), mc.player.getInventory(), name);
            mc.player.containerMenu = ((MenuAccess<?>) s).getMenu();
            mc.setScreen(s);
        });
    }

    public void handle(AuxiliaryLightDataPayload msg, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            AuxiliaryLightManager lightManager = mc.level.getAuxLightManager(msg.pos());
            if (lightManager instanceof LevelChunkAuxiliaryLightManager manager) {
                manager.handleLightDataSync(msg.entries());
            }
        }).exceptionally(e -> {
            context.packetHandler().disconnect(Component.translatable("neoforge.network.aux_light_data.failed", msg.pos(), e.getMessage()));
            return null;
        });
    }

    public void handle(AdvancedContainerSetDataPayload msg, PlayPayloadContext context) {
        context.packetHandler().handle(msg.toVanillaPacket());
    }
}
