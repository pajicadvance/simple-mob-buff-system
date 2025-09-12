package me.pajic.smbs;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.pajic.smbs.config.ModConfig;
import me.pajic.smbs.system.StatBoostEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String MOD_ID = "smbs";
    public static final ResourceLocation CONFIG_RL = id("config");
    public static ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);
    private static final Logger LOGGER = LoggerFactory.getLogger("Simple Mob Buff System");
    private static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

    @Override
    public void onInitialize() {
        StatBoostEffects.init();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void debugLog(String message, Object ... args) {
        if (DEBUG) LOGGER.info(message, args);
    }
}
