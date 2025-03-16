package net.pillowmc.shearapi.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public enum ShearAPIRuntime {
    ;
    public static final String MOD_ID = "neoforge";
    private static IRuntime INSTANCE = null;
    public static IRuntime getRuntime() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        if (FabricLoader.getInstance().isModLoaded("pillow-loader")) {
            try {
                return INSTANCE = (IRuntime) Class.forName("net.pillowmc.shearapi.withpillow.ShearAPIWithPillow").getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException
                    | ClassNotFoundException e) {
                throw new RuntimeException("Pillow Loader is detected, but ShearAPI (With Pillow) is missing!");
            }
        } else {
            try {
                return INSTANCE = (IRuntime) Class.forName("net.pillowmc.shearapi.withoutpillow.ShearAPIWithoutPillow").getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException
                    | ClassNotFoundException e) {
                throw new RuntimeException("Pillow Loader is not detected, but ShearAPI (Without Pillow) is missing!", e);
            }
        }
    }

    public static void onInitialize() {
        var bus = getRuntime().getModBus();
        FabricLoader.getInstance()
                .getEntrypoints("shearapi-runtime:mod_bus_subscriber", Object.class)
                .forEach(bus::register);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricLoader.getInstance()
                .getEntrypoints("shearapi-runtime:client_mod_bus_subscriber", Object.class)
                .forEach(bus::register);
        }
    }
}
