package me.pajic.smbs.system;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import org.jetbrains.annotations.NotNull;

public enum WeaponType implements EnumTranslatable {
    ANY, MELEE, RANGED, CUSTOM;

    @Override @NotNull
    public String prefix() {
        return "smbs.weaponType";
    }
}
