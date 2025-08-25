package me.pajic.smbs.config;

import me.fzzyhmstrs.fzzy_config.annotations.BlockArray;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.AllowableStrings;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import me.pajic.smbs.ClientMain;
import me.pajic.smbs.Main;
import me.pajic.smbs.system.*;
import me.pajic.smbs.util.Buffs;
import me.pajic.smbs.util.Mobs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
@Version(version = 1)
public class ModConfig extends Config {
    public ModConfig() {
        super(Main.CONFIG_RL);
    }

    public ValidatedFloat minSpawnChance = new ValidatedFloat(10, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat maxSpawnChance = new ValidatedFloat(25, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat minChancePerBuff = new ValidatedFloat(20, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedFloat maxChancePerBuff = new ValidatedFloat(75, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedInt xpPerBuffLevel = new ValidatedInt(3, Integer.MAX_VALUE, 1);
    public ValidatedFloat enchantedBookThresholdPercentage = new ValidatedFloat(66, 100, 1, ValidatedNumber.WidgetType.TEXTBOX);
    public ValidatedBoolean allowBuffedMobsFromSpawners = new ValidatedBoolean(false);

    @BlockArray
    public ValidatedMap<BuffEntry, Set<ResourceLocation>> buffRules = (new ValidatedMap.Builder())
            .keyHandler(new ValidatedAny<>(new BuffEntry()))
            .valueHandler(ValidatedIdentifier.ofRegistry(
                    Mobs.CREEPER,
                    BuiltInRegistries.ENTITY_TYPE
            ).toSet())
            .defaults(Map.ofEntries(
                    Map.entry(new BuffEntry(), Set.of(
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
                    Map.entry(new BuffEntry(
                            BuffSetType.CUSTOM,
                            Map.ofEntries(
                                    Map.entry(Buffs.HEALTH_BOOST, 3),
                                    Map.entry(Buffs.REGENERATION, 2),
                                    Map.entry(Buffs.SPEED, 2),
                                    Map.entry(Buffs.FARSIGHT, 2)
                            ),
                            WeaponType.ANY, Set.of()
                    ), Set.of(Mobs.BLAZE)),
                    Map.entry(new BuffEntry(
                            BuffSetType.CUSTOM,
                            Map.ofEntries(
                                    Map.entry(Buffs.HEALTH_BOOST, 3),
                                    Map.entry(Buffs.STRENGTH, 2),
                                    Map.entry(Buffs.REGENERATION, 2),
                                    Map.entry(Buffs.SPEED, 3),
                                    Map.entry(Buffs.WEAVING, 1)
                            ),
                            WeaponType.ANY, Set.of()
                    ), Set.of(Mobs.CAVE_SPIDER, Mobs.SPIDER)),
                    Map.entry(new BuffEntry(
                            BuffSetType.CUSTOM,
                            Map.ofEntries(
                                    Map.entry(Buffs.HEALTH_BOOST, 5),
                                    Map.entry(Buffs.REGENERATION, 2),
                                    Map.entry(Buffs.INFESTED, 1)
                            ),
                            WeaponType.ANY, Set.of()
                    ), Set.of(Mobs.CREEPER)),
                    Map.entry(new BuffEntry(
                            BuffSetType.CUSTOM,
                            Map.ofEntries(
                                    Map.entry(Buffs.HEALTH_BOOST, 3),
                                    Map.entry(Buffs.REGENERATION, 2),
                                    Map.entry(Buffs.KNOCKBACK_RESISTANCE, 2)
                            ),
                            WeaponType.ANY, Set.of()
                    ), Set.of(Mobs.GHAST)),
                    Map.entry(new BuffEntry(
                            BuffSetType.CUSTOM,
                            Map.ofEntries(
                                    Map.entry(Buffs.KNOCKBACK_BOOST, 3),
                                    Map.entry(Buffs.SIZE_BOOST, 3)
                            ),
                            WeaponType.ANY, Set.of()
                    ), Set.of(Mobs.HOGLIN, Mobs.ZOGLIN)),
                    Map.entry(new BuffEntry(
                            BuffSetType.CUSTOM,
                            Map.ofEntries(
                                    Map.entry(Buffs.HEALTH_BOOST, 3),
                                    Map.entry(Buffs.SPEED, 2),
                                    Map.entry(Buffs.INVISIBILITY, 1)
                            ),
                            WeaponType.ANY, Set.of()
                    ), Set.of(Mobs.WITCH))
            ))
            .build();

    public ValidatedMap<ResourceLocation, Integer> defaultMeleeBuffs = (new ValidatedMap.Builder())
            .keyHandler(ValidatedIdentifier.ofRegistry(
                    Buffs.HEALTH_BOOST,
                    BuiltInRegistries.MOB_EFFECT
            ))
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
    public ValidatedMap<ResourceLocation, Integer> defaultRangedBuffs = (new ValidatedMap.Builder())
            .keyHandler(ValidatedIdentifier.ofRegistry(
                    Buffs.HEALTH_BOOST,
                    BuiltInRegistries.MOB_EFFECT
            ))
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
            "", new AllowableStrings(ClientMain.itemSuggestions::contains, () -> ClientMain.itemSuggestions)
    ).toSet("#c:tools/melee_weapon");
    public ValidatedSet<String> rangedWeapons = new ValidatedString(
            "", new AllowableStrings(ClientMain.itemSuggestions::contains, () -> ClientMain.itemSuggestions)
    ).toSet("#c:tools/ranged_weapon");
}