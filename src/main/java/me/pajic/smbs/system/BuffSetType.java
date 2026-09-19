package me.pajic.smbs.system;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import org.jetbrains.annotations.NotNull;

public enum BuffSetType implements EnumTranslatable {
    WEAPON_BASED, CUSTOM;

    @Override @NotNull
    public String prefix() {
        return "smbs.buffSetType";
    }
}
