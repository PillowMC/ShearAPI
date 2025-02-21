package net.pillowmc.shearapi.registries.datamaps;

import io.netty.util.AttributeKey;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.DataMapLoader;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.neoforged.neoforge.registries.datamaps.network.KnownRegistryDataMapsReplyPayload;
import net.pillowmc.shearapi.event.ShearAPIEvent;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import net.pillowmc.shearapi.runtime.ShearAPIVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DataMapManager {
    private static DataMapLoader DATA_MAPS;
    private static Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> dataMaps = Map.of();
    @ApiStatus.Internal
    public void shearapi$fabricInitialize(){
        ShearAPIRuntime.getRuntime().getModBus().addListener((OnGameConfigurationEvent event) -> {
            event.register(new RegistryDataMapNegotiation(event.getListener()));
        });
        ShearAPIRuntime.getRuntime().getModBus().addListener((RegisterPayloadHandlerEvent event) -> {
            final IPayloadRegistrar registrar = event.registrar(ShearAPIRuntime.MOD_ID)
                    .versioned(ShearAPIVersion.getSpec())
                    .optional();
            registrar
                    .configuration(
                            KnownRegistryDataMapsReplyPayload.ID,
                            KnownRegistryDataMapsReplyPayload::new,
                            handlers -> handlers.server(DataMapManager::handleKnownDataMapsReply));
        });
        ShearAPIRuntime.getRuntime().getModBus().addListener(((DataMapCompostingChanceRegistry)CompostingChanceRegistry.INSTANCE)::onDataMapsLoadedEvent);
        ShearAPIRuntime.getRuntime().getModBus().addListener(((DataMapFuelRegistry)FuelRegistry.INSTANCE)::onDataMapsLoadedEvent);
        ShearAPIRuntime.getRuntime().getModBus().addListener(((DataMapMap<?, ?, ?>) Parrot.MOB_SOUND_MAP)::onDataMapsLoadedEvent);
        ShearAPIRuntime.getRuntime().getModBus().addListener(((DataMapMap<?, ?, ?>) GiveGiftToHero.GIFTS)::onDataMapsLoadedEvent);
        ShearAPIRuntime.getRuntime().getModBus().addListener(((Object2IntDataMapMap<?, ?>) VibrationSystem.VIBRATION_FREQUENCY_FOR_EVENT)::onDataMapsLoadedEvent);
        ShearAPIEvent.EVENT_BUS.addListener((AddReloadListenerEvent event) -> event.addListener(DATA_MAPS = new DataMapLoader(event.getConditionContext(), event.getRegistryAccess())));
        ShearAPIEvent.EVENT_BUS.addListener((TagsUpdatedEvent event) -> DATA_MAPS.apply());
        initDataMaps();
    };

    @ApiStatus.Internal
    public static void initDataMaps() {
        final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> dataMapTypes = new HashMap<>();
        ShearAPIRuntime.getRuntime().postModBusEvent(new RegisterDataMapTypesEvent(dataMapTypes));
        dataMaps = new IdentityHashMap<>();
        dataMapTypes.forEach((key, values) -> dataMaps.put(key, Collections.unmodifiableMap(values)));
        dataMaps = Collections.unmodifiableMap(dataMapTypes);
    }

    @Nullable
    public static <R> DataMapType<R, ?> getDataMap(ResourceKey<? extends Registry<R>> registry, ResourceLocation key) {
        final var map = dataMaps.get(registry);
        return map == null ? null : (DataMapType<R, ?>) map.get(key);
    }

    /**
     * {@return a view of all registered data maps}
     */
    public static Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> getDataMaps() {
        return dataMaps;
    }

    public static final AttributeKey<Map<ResourceKey<Registry<?>>, Collection<ResourceLocation>>> ATTRIBUTE_KNOWN_DATA_MAPS = AttributeKey.valueOf("neoforge:known_data_maps");

    @ApiStatus.Internal
    public static void handleKnownDataMapsReply(final KnownRegistryDataMapsReplyPayload payload, final ConfigurationPayloadContext context) {
        context.channelHandlerContext().attr(ATTRIBUTE_KNOWN_DATA_MAPS).set(payload.dataMaps());
        context.taskCompletedHandler().onTaskCompleted(RegistryDataMapNegotiation.TYPE);
    }
}
