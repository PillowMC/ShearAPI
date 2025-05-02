package net.pillowmc.shearapi.registries;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.neoforged.neoforge.registries.GameData;
import net.neoforged.neoforge.registries.RegistryManager;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ShearAPIRegistries implements ModInitializer {
    private static final List<? extends ResourceKey<? extends Registry<?>>> VANILLA_REGISTRIES_DATAPACK_REGISTRY_KEYS = VanillaRegistries.BUILDER.entries.stream().map(RegistrySetBuilder.RegistryStub::key).toList()
    @Override
    public void onInitialize() {
        RegistryManager.postNewRegistryEvent();
        GameData.postRegisterEvents();
    }

    public static void initValidStatesInDebugLevelSource() {
        DebugLevelSource.ALL_BLOCKS = StreamSupport.stream(BuiltInRegistries.BLOCK.spliterator(), false).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
        DebugLevelSource.GRID_WIDTH = Mth.ceil(Mth.sqrt(DebugLevelSource.ALL_BLOCKS.size()));
        DebugLevelSource.GRID_HEIGHT = Mth.ceil((float)DebugLevelSource.ALL_BLOCKS.size() / (float)DebugLevelSource.GRID_WIDTH);
    }

    public static boolean isVanillaRegistry(ResourceLocation location) {
        // Checks if the registry name is contained within the static view of both BuiltInRegistries and VanillaRegistries
        return RegistryManager.getVanillaRegistryKeys().contains(location)
                || VANILLA_REGISTRIES_DATAPACK_REGISTRY_KEYS.stream().anyMatch(k -> k.location().equals(location));
    }
}
