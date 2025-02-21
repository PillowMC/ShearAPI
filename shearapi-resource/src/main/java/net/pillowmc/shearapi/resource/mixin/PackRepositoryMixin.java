package net.pillowmc.shearapi.resource.mixin;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Mutable
    @Shadow
    @Final
    private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void construct(RepositorySource[] repositorySources, CallbackInfo ci) {
        this.sources = new LinkedHashSet<>(this.sources);
        PackType type = PackType.CLIENT_RESOURCES;

        for (RepositorySource provider : this.sources) {
            if (provider instanceof FolderRepositorySource && (((FolderRepositorySource) provider).packSource == PackSource.WORLD || ((FolderRepositorySource) provider).packSource == PackSource.SERVER)) {
                type = PackType.SERVER_DATA;
                break;
            }
        }

        ShearAPIRuntime.getRuntime().postModBusEvent(new AddPackFindersEvent(type, sources::add));
    }
}
