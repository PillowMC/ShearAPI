package net.pillowmc.shearapi.network.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import net.pillowmc.shearapi.network.injection.IConnectionExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Connection.class)
public class ConnectionMixin implements IConnectionExtension {
    @Shadow
    public Channel channel;

    @Override
    public Channel channel() {
        return this.channel;
    }
}
