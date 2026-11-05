package me.pajic.smbs.system;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import org.jetbrains.annotations.NotNull;

public enum FilterMode implements EnumTranslatable {
    BLACKLIST, WHITELIST;

    @Override @NotNull
    public String prefix() {
        return "smbs.filterMode";
    }
}
