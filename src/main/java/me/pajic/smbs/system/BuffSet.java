package me.pajic.smbs.system;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.util.Walkable;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import me.pajic.smbs.util.Buffs;
import net.minecraft.resources.Identifier;

import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Translation(prefix = "smbs.buffSet")
public class BuffSet implements Walkable {

    public ValidatedString name;
    public ValidatedMap<Identifier, Integer> buffs;

    public BuffSet(String name, Map<Identifier, Integer> buffs) {
        this.name = new ValidatedString(name);
        this.buffs = (new ValidatedMap.Builder())
                .keyHandler(Buffs.validatedKey())
                .valueHandler(new ValidatedInt(1, 255, 1, ValidatedNumber.WidgetType.TEXTBOX))
                .defaults(buffs)
                .build();
    }

    public BuffSet() {
        this("", Map.of());
    }
}
