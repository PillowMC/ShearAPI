/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.common.extensions;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
// import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

public interface ILevelExtension {

    /**
     * The maximum radius to scan for entities when trying to check bounding boxes. Vanilla's default is
     * 2.0D But mods that add larger entities may increase this.
     */
    // TODO: (ShearAPI) implement this
    default public double getMaxEntityRadius() {
        throw new AssertionError("This should be implemented by mixin!");
    }

    /**
     * Increases the max entity radius, this is safe to call with any value.
     * The setter will verify the input value is larger then the current setting.
     *
     * @param value New max radius to set.
     * @return The new max radius
     */
    // TODO: (ShearAPI) implement this
    default public double increaseMaxEntityRadius(double value){
        throw new AssertionError("This should be implemented by mixin!");
    }
    
    /**
     * All part entities in this world. Used when collecting entities in an AABB to fix parts being
     * ignored whose parent entity is in a chunk that does not intersect with the AABB.
     */
    // TODO: (ShearAPI) implement this
    // public default Collection<PartEntity<?>> getPartEntities() {
    //     return Collections.emptyList();
    // }
}