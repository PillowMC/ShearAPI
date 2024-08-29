package net.pillowmc.shearapi.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public class MobMixin {
    private boolean spawnCancelled = false;

    /**
     * This method exists so that spawns can be cancelled from the {@link net.neoforged.neoforge.event.entity.living.MobSpawnEvent.FinalizeSpawn FinalizeSpawnEvent}
     * without needing to hook up an additional handler for the {@link net.neoforged.neoforge.event.entity.EntityJoinLevelEvent EntityJoinLevelEvent}.
     * @return if this mob will be blocked from spawning during {@link Level#addFreshEntity(Entity)}
     * @apiNote Not public-facing API.
     */
    @org.jetbrains.annotations.ApiStatus.Internal
    public final boolean isSpawnCancelled() {
        return this.spawnCancelled;
    }

    /**
     * Marks this mob as being disallowed to spawn during {@link Level#addFreshEntity(Entity)}.<p>
     * @throws UnsupportedOperationException if this entity has already been {@link Entity#isAddedToWorld() added to the world}.
     * @apiNote Not public-facing API.
     */
    @org.jetbrains.annotations.ApiStatus.Internal
    public final void setSpawnCancelled(boolean cancel) {
        if (((EntityMixin)(Object) this).isAddedToWorld()) {
            throw new UnsupportedOperationException("Late invocations of Mob#setSpawnCancelled are not permitted.");
        }
        this.spawnCancelled = cancel;
    }
}
