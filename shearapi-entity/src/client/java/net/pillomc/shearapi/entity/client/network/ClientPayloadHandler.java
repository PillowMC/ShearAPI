package net.pillomc.shearapi.entity.client.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.payload.AdvancedAddEntityPayload;

import java.util.Objects;

public class ClientPayloadHandler {
    public static void handle(AdvancedAddEntityPayload advancedAddEntityPayload, PlayPayloadContext context) {
        context.workHandler().submitAsync(
                        () -> {
                            Entity entity = Objects.requireNonNull(Minecraft.getInstance().level).getEntity(advancedAddEntityPayload.entityId());
                            if (entity instanceof IEntityWithComplexSpawn entityAdditionalSpawnData) {
                                final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(advancedAddEntityPayload.customPayload()));
                                try {
                                    entityAdditionalSpawnData.readSpawnData(buf);
                                } finally {
                                    buf.release();
                                }
                            }
                        })
                .exceptionally(e -> {
                    context.packetHandler().disconnect(Component.translatable("neoforge.network.advanced_add_entity.failed", e.getMessage()));
                    return null;
                });
    }
}
