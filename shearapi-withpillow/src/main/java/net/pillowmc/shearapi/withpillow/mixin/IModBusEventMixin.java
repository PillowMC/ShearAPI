package net.pillowmc.shearapi.withpillow.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.pillowmc.shearapi.runtime.IModBusEvent;

@Mixin(IModBusEvent.class)
public interface IModBusEventMixin extends net.neoforged.fml.event.IModBusEvent {
    
}
