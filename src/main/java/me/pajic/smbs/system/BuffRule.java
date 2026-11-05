package me.pajic.smbs.system;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.util.AllowableStrings;
import me.fzzyhmstrs.fzzy_config.util.Walkable;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.util.RegistrySuggestions;

import java.util.List;
import java.util.Set;

@Translation(prefix = "smbs.buffRule")
public class BuffRule implements Walkable {

    public ValidatedString name;
    public ValidatedSet<String> mobs;
    public ValidatedEnum<WeaponType> requiredWeapon;
    public ValidatedSet<String> customWeapons;
    public ValidatedString buffSet;
    public ValidatedString rangedBuffSet;
    public ValidatedFloat minSpawnChance = new ValidatedFloat(10F, 100F, 0F, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat maxSpawnChance = new ValidatedFloat(25F, 100F, 0F, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat minBuffChance = new ValidatedFloat(20F, 100F, 0F, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat maxBuffChance = new ValidatedFloat(75F, 100F, 0F, ValidatedNumber.WidgetType.TEXTBOX);

    public BuffRule(String name, Set<String> mobs, String buffSet, String rangedBuffSet) {
        this.name = new ValidatedString(name);
        this.mobs = new ValidatedString("", RegistrySuggestions.ENTITIES.allowable()).toSet(mobs);
        this.requiredWeapon = new ValidatedEnum<>(WeaponType.ANY);
        this.customWeapons = new ValidatedString("", RegistrySuggestions.ITEMS.allowable()).toSet();
        this.buffSet = new ValidatedString(buffSet, buffSetNames());
        this.rangedBuffSet = new ValidatedString(rangedBuffSet, buffSetNames());
    }

    public BuffRule(String name, Set<String> mobs, String buffSet) {
        this(name, mobs, buffSet, "");
    }

    public BuffRule() {
        this("", Set.of(), "");
    }

    private static AllowableStrings buffSetNames() {
        return new AllowableStrings(
                name -> true,
                () -> SMBS.CONFIG == null ? List.of() : SMBS.CONFIG.buffSets.get().stream().map(set -> set.name.get()).toList()
        );
    }
}
