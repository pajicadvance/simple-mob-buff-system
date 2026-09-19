package me.pajic.smbs.system;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.util.AllowableStrings;
import me.fzzyhmstrs.fzzy_config.util.Walkable;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.pajic.smbs.util.Buffs;
import me.pajic.smbs.util.ItemSuggestions;
import me.pajic.smbs.util.Mobs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
@Translation(prefix = "smbs.buffEntry")
public class BuffEntry implements Walkable {

	public ValidatedSet<Identifier> mobs;
    public ValidatedEnum<BuffSetType> buffSetType;
    public ValidatedMap<Identifier, Integer> customBuffs;
    public ValidatedEnum<WeaponType> weaponType;
    public ValidatedSet<String> customWeapons;

    public BuffEntry(Set<Identifier> mobs, BuffSetType buffSetType, Map<Identifier, Integer> customBuffs, WeaponType weaponType, Set<String> customWeapons) {
		this.mobs = ValidatedIdentifier.ofRegistry(Mobs.CREEPER, BuiltInRegistries.ENTITY_TYPE).toSet(mobs);
        this.buffSetType = new ValidatedEnum<>(buffSetType);
        this.customBuffs = (new ValidatedMap.Builder())
                .keyHandler(ValidatedIdentifier
						.ofRegistry(
								Buffs.HEALTH_BOOST,
								BuiltInRegistries.MOB_EFFECT
						)
				)
                .valueHandler(new ValidatedInt(1, Integer.MAX_VALUE, 1))
                .defaults(customBuffs)
                .build();
        this.weaponType = new ValidatedEnum<>(weaponType);
        this.customWeapons = new ValidatedString(
                "", new AllowableStrings(ItemSuggestions.get()::contains, ItemSuggestions::get)
        ).toSet(customWeapons);
    }

	public BuffEntry(Set<Identifier> mobs) {
		this();
		this.mobs = ValidatedIdentifier.ofRegistry(Mobs.CREEPER, BuiltInRegistries.ENTITY_TYPE).toSet(mobs);
	}

    public BuffEntry() {
		this.mobs = ValidatedIdentifier.ofRegistry(Mobs.CREEPER, BuiltInRegistries.ENTITY_TYPE).toSet();
        this.buffSetType = new ValidatedEnum<>(BuffSetType.WEAPON_BASED);
        this.customBuffs = (new ValidatedMap.Builder())
                .keyHandler(ValidatedIdentifier
						.ofRegistry(
								Buffs.HEALTH_BOOST,
								BuiltInRegistries.MOB_EFFECT
						)
				)
                .valueHandler(new ValidatedInt(1, Integer.MAX_VALUE, 1))
                .build();
        this.weaponType = new ValidatedEnum<>(WeaponType.ANY);
        this.customWeapons = new ValidatedString(
                "", new AllowableStrings(ItemSuggestions.get()::contains, ItemSuggestions::get)
        ).toSet();
    }
}
