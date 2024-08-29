package net.pillowmc.shearapi.utils.mixin;

import net.minecraft.world.level.portal.PortalForcer;
import net.neoforged.neoforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PortalForcer.class)
public class PortalForcerMixin implements ITeleporter {
}
