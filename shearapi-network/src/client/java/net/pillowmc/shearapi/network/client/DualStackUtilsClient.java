package net.pillowmc.shearapi.network.client;

import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.neoforged.neoforge.network.DualStackUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

public class DualStackUtilsClient {

    /**
     * Resolve the address and see if Java and the OS return an IPv6 or IPv4 one, then let Netty know
     * accordingly (it doesn't understand the {@code java.net.preferIPv6Addresses=system} property).
     *
     * @param hostAddress The address you want to check
     * @return true if IPv6, false if IPv4
     */
    public static boolean checkIPv6(final String hostAddress) {
        final Optional<InetSocketAddress> hostAddr = ServerNameResolver.DEFAULT
                .resolveAddress(ServerAddress.parseString(hostAddress))
                .map(ResolvedServerAddress::asInetSocketAddress);

        if (hostAddr.isPresent()) return DualStackUtils.checkIPv6(hostAddr.get().getAddress());
        else return false;
    }
}
