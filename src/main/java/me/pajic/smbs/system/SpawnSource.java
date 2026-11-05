package me.pajic.smbs.system;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import net.minecraft.world.entity.EntitySpawnReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SpawnSource implements EnumTranslatable {
    NATURAL, STRUCTURE, SPAWNER, TRIAL_SPAWNER, REINFORCEMENT, CONVERSION, SPAWN_EGG, COMMAND, OTHER;

    public static SpawnSource of(EntitySpawnReason reason) {
        return switch (reason.name()) {
            case "NATURAL", "CHUNK_GENERATION", "PATROL", "EVENT", "JOCKEY" -> NATURAL;
            case "STRUCTURE" -> STRUCTURE;
            case "SPAWNER" -> SPAWNER;
            case "TRIAL_SPAWNER" -> TRIAL_SPAWNER;
            case "REINFORCEMENT" -> REINFORCEMENT;
            case "CONVERSION" -> CONVERSION;
            case "SPAWN_EGG", "SPAWN_ITEM_USE", "DISPENSER" -> SPAWN_EGG;
            case "COMMAND" -> COMMAND;
            default -> OTHER;
        };
    }

    public static @Nullable SpawnSource byName(String name) {
        for (SpawnSource source : values()) {
            if (source.name().equals(name)) return source;
        }
        return null;
    }

    @Override @NotNull
    public String prefix() {
        return "smbs.spawnSource";
    }
}
