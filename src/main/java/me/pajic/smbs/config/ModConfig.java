package me.pajic.smbs.config;

import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.AllowableStrings;
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.system.BuffEntry;
import me.pajic.smbs.system.BuffSetType;
import me.pajic.smbs.system.WeaponType;
import me.pajic.smbs.util.Buffs;
import me.pajic.smbs.util.ItemSuggestions;
import me.pajic.smbs.util.Mobs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
@Version(version = 2)
public class ModConfig extends Config {

	private final ValidatedAny<BuffEntry> buffEntry = new ValidatedAny<>(new BuffEntry());

    public ModConfig() {
        super(SMBS.id("config-v2"));
		ValidatedField.Companion.attachProvider(buffEntry, Provider.Companion.getWIDGET_TITLE(), (entry, _) -> {
			if (entry.mobs.get().isEmpty()) return Component.translatable("smbs.buffEntry.empty");
			MutableComponent title = Component.empty();
			List<Component> mobs = new ArrayList<>();
			entry.mobs.get().forEach(id ->
					mobs.add(Component.translatableWithFallback("entity." + id.getNamespace() + "." + id.getPath(), id.getPath()))
			);
			for (int i = 0; i < mobs.size(); i++) {
				if (i != 0) title.append(", ");
				title.append(mobs.get(i));
			}
			return title;
		});
    }

    public ValidatedFloat minSpawnChance = new ValidatedFloat(10, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat maxSpawnChance = new ValidatedFloat(25, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat minChancePerBuff = new ValidatedFloat(20, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat maxChancePerBuff = new ValidatedFloat(75, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedInt xpPerBuffLevel = new ValidatedInt(3, Integer.MAX_VALUE, 1);
    public ValidatedFloat enchantedBookThresholdPercentage = new ValidatedFloat(66, 100, 1, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedBoolean increasedEquipmentDropChancePerBuff = new ValidatedBoolean(true);
    public ValidatedFloat maxEquipmentDropChanceIncrease = new ValidatedFloat(10F, Float.MAX_VALUE, 1F);
    public ValidatedBoolean allowBuffedMobsFromSpawners = new ValidatedBoolean(false);
    public ValidatedBoolean onlyBuffedMobsSpreadSculk = new ValidatedBoolean(false);

	public ValidatedList<BuffEntry> buffRules = buffEntry.toList(
			new BuffEntry(Set.of(
					Mobs.BOGGED,
					Mobs.DROWNED,
					Mobs.HUSK,
					Mobs.MAGMA_CUBE,
					Mobs.PHANTOM,
					Mobs.PIGLIN,
					Mobs.PIGLIN_BRUTE,
					Mobs.PILLAGER,
					Mobs.SKELETON,
					Mobs.SLIME,
					Mobs.STRAY,
					Mobs.VINDICATOR,
					Mobs.WITHER_SKELETON,
					Mobs.ZOMBIE,
					Mobs.ZOMBIE_VILLAGER,
					Mobs.ZOMBIFIED_PIGLIN
			)),
			new BuffEntry(Set.of(Mobs.BLAZE),
					BuffSetType.CUSTOM,
					Map.ofEntries(
							Map.entry(Buffs.HEALTH_BOOST, 3),
							Map.entry(Buffs.REGENERATION, 2),
							Map.entry(Buffs.SPEED, 2),
							Map.entry(Buffs.FARSIGHT, 2)
					),
					WeaponType.ANY, Set.of()
			),
			new BuffEntry(Set.of(Mobs.CAVE_SPIDER, Mobs.SPIDER),
					BuffSetType.CUSTOM,
					Map.ofEntries(
							Map.entry(Buffs.HEALTH_BOOST, 3),
							Map.entry(Buffs.STRENGTH, 2),
							Map.entry(Buffs.REGENERATION, 2),
							Map.entry(Buffs.SPEED, 3),
							Map.entry(Buffs.WEAVING, 1)
					),
					WeaponType.ANY, Set.of()
			),
			new BuffEntry(Set.of(Mobs.CREEPER),
					BuffSetType.CUSTOM,
					Map.ofEntries(
							Map.entry(Buffs.HEALTH_BOOST, 5),
							Map.entry(Buffs.REGENERATION, 2),
							Map.entry(Buffs.INFESTED, 1)
					),
					WeaponType.ANY, Set.of()
			),
			new BuffEntry(Set.of(Mobs.GHAST),
					BuffSetType.CUSTOM,
					Map.ofEntries(
							Map.entry(Buffs.HEALTH_BOOST, 3),
							Map.entry(Buffs.REGENERATION, 2),
							Map.entry(Buffs.KNOCKBACK_RESISTANCE, 2)
					),
					WeaponType.ANY, Set.of()
			),
			new BuffEntry(Set.of(Mobs.HOGLIN, Mobs.ZOGLIN),
					BuffSetType.CUSTOM,
					Map.ofEntries(
							Map.entry(Buffs.KNOCKBACK_BOOST, 3),
							Map.entry(Buffs.SIZE_BOOST, 3)
					),
					WeaponType.ANY, Set.of()
			),
			new BuffEntry(Set.of(Mobs.WITCH),
					BuffSetType.CUSTOM,
					Map.ofEntries(
							Map.entry(Buffs.HEALTH_BOOST, 3),
							Map.entry(Buffs.SPEED, 2),
							Map.entry(Buffs.INVISIBILITY, 1)
					),
					WeaponType.ANY, Set.of()
			)
	);

    public ValidatedMap<Identifier, Integer> defaultMeleeBuffs = (new ValidatedMap.Builder())
            .keyHandler(ValidatedIdentifier
					.ofRegistry(
							Buffs.HEALTH_BOOST,
							BuiltInRegistries.MOB_EFFECT
					)
			)
            .valueHandler(ValidatedNumber.withIncrement(new ValidatedInt(
                    1, Integer.MAX_VALUE, 1
            ), 1))
            .defaults(Map.ofEntries(
                    Map.entry(Buffs.HEALTH_BOOST, 5),
                    Map.entry(Buffs.RESISTANCE, 2),
                    Map.entry(Buffs.STRENGTH, 2),
                    Map.entry(Buffs.KNOCKBACK_RESISTANCE, 1),
                    Map.entry(Buffs.KNOCKBACK_BOOST, 1)
            ))
            .build();
    public ValidatedMap<Identifier, Integer> defaultRangedBuffs = (new ValidatedMap.Builder())
            .keyHandler(ValidatedIdentifier
					.ofRegistry(
							Buffs.HEALTH_BOOST,
							BuiltInRegistries.MOB_EFFECT
					)
			)
            .valueHandler(ValidatedNumber.withIncrement(new ValidatedInt(
                    1, Integer.MAX_VALUE, 1
            ), 1))
            .defaults(Map.ofEntries(
                    Map.entry(Buffs.HEALTH_BOOST, 3),
                    Map.entry(Buffs.SPEED, 2),
                    Map.entry(Buffs.INVISIBILITY, 1),
                    Map.entry(Buffs.FARSIGHT, 4)
            ))
            .build();

    public ValidatedSet<String> meleeWeapons = new ValidatedString(
            "", new AllowableStrings(ItemSuggestions.get()::contains, ItemSuggestions::get)
    ).toSet("#c:tools/melee_weapon");
    public ValidatedSet<String> rangedWeapons = new ValidatedString(
            "", new AllowableStrings(ItemSuggestions.get()::contains, ItemSuggestions::get)
    ).toSet("#c:tools/ranged_weapon");
}
