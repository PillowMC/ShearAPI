package net.pillowmc.shearapi.registries.testmod;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import org.slf4j.Logger;

public class NeoForgeRegisterTest implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("shearapi-registries");
    private static final DeferredItem<Item> TEST_ITEM = ITEMS.registerSimpleItem("test_neoforge_item");
    @Override
    public void onInitialize() {
        LOGGER.info("Testing NeoForge Registries");
        ITEMS.register(ShearAPIRuntime.getRuntime().getModBus());
    }
}
