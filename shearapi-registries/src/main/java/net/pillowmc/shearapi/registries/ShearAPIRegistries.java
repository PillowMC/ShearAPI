package net.pillowmc.shearapi.registries;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.neoforged.neoforge.registries.GameData;
import net.neoforged.neoforge.registries.RegistryManager;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ShearAPIRegistries implements ModInitializer {
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
}
