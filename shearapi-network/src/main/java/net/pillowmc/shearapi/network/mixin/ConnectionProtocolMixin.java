package net.pillowmc.shearapi.network.mixin;

import net.minecraft.network.ConnectionProtocol;
import net.pillowmc.shearapi.network.injection.IConnectionProtocolExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConnectionProtocol.class)
public class ConnectionProtocolMixin implements IConnectionProtocolExtension {

    @Override
    public boolean isPlay() {
        return (Object) this == ConnectionProtocol.PLAY;
    }

    @Override
    public boolean isConfiguration() {
        return (Object) this == ConnectionProtocol.CONFIGURATION;
    }
}
