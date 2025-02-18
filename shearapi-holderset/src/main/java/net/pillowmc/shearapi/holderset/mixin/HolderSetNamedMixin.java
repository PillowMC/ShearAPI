package net.pillowmc.shearapi.holderset.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.pillowmc.shearapi.holderset.injection.IHolderSetExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(HolderSet.Named.class)
public class HolderSetNamedMixin<T> implements IHolderSetExtension<T> {
    @Inject(method = "bind", at = @At("TAIL"))
    private void runInvalidationListeners(List<Holder<T>> list, CallbackInfo ci) {
        for (Runnable runnable : this.invalidationCallbacks) {
            runnable.run();
        }
    }

    @Unique
    private final List<Runnable> invalidationCallbacks = new java.util.ArrayList<>();
    @Override
    public void addInvalidationListener(Runnable runnable) {
        invalidationCallbacks.add(runnable);
    }
}
