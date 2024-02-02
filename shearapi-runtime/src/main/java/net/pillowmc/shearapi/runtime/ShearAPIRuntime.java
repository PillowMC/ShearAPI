package net.pillowmc.shearapi.runtime;

import java.lang.reflect.InvocationTargetException;

import net.fabricmc.loader.api.FabricLoader;

public enum ShearAPIRuntime {
    ;
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
                throw new RuntimeException("Pillow Loader detected, but ShearAPI (With Pillow) is missing!");
            }
        } else {
            try {
                return INSTANCE = (IRuntime) Class.forName("net.pillowmc.shearapi.withoutpillow.ShearAPIWithoutPillow").getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException
                    | ClassNotFoundException e) {
                throw new RuntimeException("Pillow Loader not detected, but ShearAPI (Without Pillow) is missing!", e);
            }
        }
    }
}
