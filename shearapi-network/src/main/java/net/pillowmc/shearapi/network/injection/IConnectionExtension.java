package net.pillowmc.shearapi.network.injection;

import io.netty.channel.Channel;

public interface IConnectionExtension {
    Channel channel();
}
