package net.pillowmc.shearapi.resource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.pillowmc.shearapi.event.ShearAPIEvent;

public class ShearAPIResourceInit implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonLifecycleEvents.TAGS_LOADED.register(((registries, client) -> ShearAPIEvent.EVENT_BUS.post(new TagsUpdatedEvent(registries, client, false))));
    }
}
