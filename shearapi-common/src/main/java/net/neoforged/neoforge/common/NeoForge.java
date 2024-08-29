/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.common;

import net.neoforged.bus.api.IEventBus;
import net.pillowmc.shearapi.event.ShearAPIEvent;

public class NeoForge {
    /**
     * The NeoForge event bus, used for most events.
     * Also known as the "game" bus.
     */
    public static final IEventBus EVENT_BUS = ShearAPIEvent.EVENT_BUS;
}
