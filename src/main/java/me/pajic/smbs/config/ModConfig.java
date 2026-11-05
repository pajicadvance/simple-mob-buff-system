package me.pajic.smbs.config;

import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.system.BuffRule;
import me.pajic.smbs.system.BuffSet;
import me.pajic.smbs.system.FilterMode;
import me.pajic.smbs.system.SpawnSource;
import me.pajic.smbs.util.Buffs;
import me.pajic.smbs.util.Mobs;
import me.pajic.smbs.util.RegistrySuggestions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Version(version = 3)
public class ModConfig extends Config {

	private final ValidatedAny<BuffSet> buffSet = new ValidatedAny<>(new BuffSet());
	private final ValidatedAny<BuffRule> buffRule = new ValidatedAny<>(new BuffRule());

	public ModConfig() {
		super(SMBS.id("config-v3"));
		ValidatedField.Companion.attachProvider(buffSet, Provider.Companion.getWIDGET_TITLE(), (entry, c) ->
				entry.name.get().isEmpty() ? Component.translatable("smbs.buffSet.unnamed") : Component.literal(entry.name.get())
		);
		ValidatedField.Companion.attachProvider(buffRule, Provider.Companion.getWIDGET_TITLE(), (entry, c) -> {
			if (!entry.name.get().isEmpty()) return Component.literal(entry.name.get());
			if (entry.mobs.get().isEmpty()) return Component.translatable("smbs.buffRule.empty");
			MutableComponent title = Component.empty();
			boolean first = true;
			for (String mob : entry.mobs.get()) {
				if (!first) title.append(", ");
				first = false;
				Identifier id = mob.startsWith("#") ? null : Identifier.tryParse(mob);
				title.append(id == null
						? Component.literal(mob)
						: Component.translatableWithFallback("entity." + id.getNamespace() + "." + id.getPath(), id.getPath())
				);
			}
			return title;
		});
	}

	public Spawning spawning = new Spawning();
	public Rewards rewards = new Rewards();
	public Weapons weapons = new Weapons();

	public ValidatedList<BuffSet> buffSets = buffSet.toList(
			new BuffSet("melee", Map.of(
					Buffs.HEALTH_BOOST, 5,
					Buffs.RESISTANCE, 2,
					Buffs.STRENGTH, 2,
					Buffs.KNOCKBACK_RESISTANCE, 1,
					Buffs.KNOCKBACK_BOOST, 1
			)),
			new BuffSet("ranged", Map.of(
					Buffs.HEALTH_BOOST, 3,
					Buffs.SPEED, 2,
					Buffs.INVISIBILITY, 1,
					Buffs.FARSIGHT, 4
			)),
			new BuffSet("blaze", Map.of(
					Buffs.HEALTH_BOOST, 3,
					Buffs.REGENERATION, 2,
					Buffs.SPEED, 2,
					Buffs.FARSIGHT, 2
			)),
			new BuffSet("spider", Map.of(
					Buffs.HEALTH_BOOST, 3,
					Buffs.STRENGTH, 2,
					Buffs.REGENERATION, 2,
					Buffs.SPEED, 3,
					Buffs.WEAVING, 1
			)),
			new BuffSet("creeper", Map.of(
					Buffs.HEALTH_BOOST, 5,
					Buffs.REGENERATION, 2,
					Buffs.INFESTED, 1
			)),
			new BuffSet("ghast", Map.of(
					Buffs.HEALTH_BOOST, 3,
					Buffs.REGENERATION, 2,
					Buffs.KNOCKBACK_RESISTANCE, 2
			)),
			new BuffSet("hoglin", Map.of(
					Buffs.KNOCKBACK_BOOST, 3,
					Buffs.SIZE_BOOST, 3
			)),
			new BuffSet("witch", Map.of(
					Buffs.HEALTH_BOOST, 3,
					Buffs.SPEED, 2,
					Buffs.INVISIBILITY, 1
			))
	);

	public ValidatedList<BuffRule> rules = buffRule.toList(
			new BuffRule("Melee and ranged mobs", Set.of(
					Mobs.BOGGED.toString(),
					Mobs.DROWNED.toString(),
					Mobs.HUSK.toString(),
					Mobs.MAGMA_CUBE.toString(),
					Mobs.PHANTOM.toString(),
					Mobs.PIGLIN.toString(),
					Mobs.PIGLIN_BRUTE.toString(),
					Mobs.PILLAGER.toString(),
					Mobs.SKELETON.toString(),
					Mobs.SLIME.toString(),
					Mobs.STRAY.toString(),
					Mobs.VINDICATOR.toString(),
					Mobs.WITHER_SKELETON.toString(),
					Mobs.ZOMBIE.toString(),
					Mobs.ZOMBIE_VILLAGER.toString(),
					Mobs.ZOMBIFIED_PIGLIN.toString()
			), "melee", "ranged"),
			new BuffRule("Blazes", Set.of(Mobs.BLAZE.toString()), "blaze"),
			new BuffRule("Spiders", Set.of(Mobs.CAVE_SPIDER.toString(), Mobs.SPIDER.toString()), "spider"),
			new BuffRule("Creepers", Set.of(Mobs.CREEPER.toString()), "creeper"),
			new BuffRule("Ghasts", Set.of(Mobs.GHAST.toString()), "ghast"),
			new BuffRule("Hoglins", Set.of(Mobs.HOGLIN.toString(), Mobs.ZOGLIN.toString()), "hoglin"),
			new BuffRule("Witches", Set.of(Mobs.WITCH.toString()), "witch")
	);

	public StatBoostSettings statBoosts = new StatBoostSettings();
	public Misc misc = new Misc();

	public static class Spawning extends ConfigSection {
		public ValidatedBoolean enabled = new ValidatedBoolean(true);
		public ValidatedFloat minChanceDifficulty = new ValidatedFloat(2F, 6.75F, 0F, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedFloat maxChanceDifficulty = new ValidatedFloat(6.75F, 6.75F, 0F, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedFloat minLevelDifficulty = new ValidatedFloat(2F, 6.75F, 0F, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedFloat maxLevelDifficulty = new ValidatedFloat(4F, 6.75F, 0F, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedFloat levelExponent = new ValidatedFloat(2F, 5F, 0.1F);
		public ValidatedInt maxBuffsPerMob = new ValidatedInt(0, 100, 0, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedChoiceList<SpawnSource> allowedSpawnSources = new ValidatedEnum<>(SpawnSource.NATURAL)
				.toList(SpawnSource.values())
				.toChoiceList(
						List.of(
								SpawnSource.NATURAL,
								SpawnSource.STRUCTURE,
								SpawnSource.REINFORCEMENT,
								SpawnSource.CONVERSION,
								SpawnSource.SPAWN_EGG,
								SpawnSource.COMMAND,
								SpawnSource.OTHER
						),
						ValidatedChoiceList.WidgetType.POPUP,
						(source, k) -> source.translation(source.name()),
						(source, k) -> source.description("")
				);
		public ValidatedEnum<FilterMode> dimensionFilterMode = new ValidatedEnum<>(FilterMode.BLACKLIST);
		public ValidatedSet<Identifier> dimensions = new ValidatedIdentifier(Identifier.withDefaultNamespace("overworld")).toSet();
	}

	public static class Rewards extends ConfigSection {
		public ValidatedBoolean xpEnabled = new ValidatedBoolean(true);
		public ValidatedInt xpPerBuffLevel = new ValidatedInt(3, 10, 1);
		public ValidatedBoolean bookEnabled = new ValidatedBoolean(true);
		public ValidatedFloat bookThresholdPercentage = new ValidatedFloat(66F, 100F, 1F, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedIdentifier bookEnchantmentTag = new ValidatedIdentifier(Identifier.withDefaultNamespace("on_random_loot"));
		public ValidatedInt bookBonusLevels = new ValidatedInt(2, 5, 0);
		public ValidatedBoolean increasedEquipmentDrops = new ValidatedBoolean(true);
		public ValidatedFloat maxEquipmentDropMultiplier = new ValidatedFloat(10F, 100F, 1F, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedInt leaderBuffLevelBonus = new ValidatedInt(6, 10, 0);
		public ValidatedInt enchantedEquipmentBonus = new ValidatedInt(1, 5, 0);
		public ValidatedBoolean equipmentCountsTowardBook = new ValidatedBoolean(false);
	}

	public static class Weapons extends ConfigSection {
		public ValidatedSet<String> meleeWeapons = new ValidatedString("", RegistrySuggestions.ITEMS.allowable()).toSet("#c:tools/melee_weapon");
		public ValidatedSet<String> rangedWeapons = new ValidatedString("", RegistrySuggestions.ITEMS.allowable()).toSet("#c:tools/ranged_weapon");
	}

	public static class StatBoostSettings extends ConfigSection {
		public ValidatedBoolean showParticles = new ValidatedBoolean(true);
		public ValidatedDouble knockbackResistanceAmount = new ValidatedDouble(0.2, 1.0, 0.0);
		public ValidatedColor knockbackResistanceColor = new ValidatedColor(122, 106, 88);
		public ValidatedDouble attackKnockbackAmount = new ValidatedDouble(1.0, 5.0, 0.0);
		public ValidatedColor attackKnockbackColor = new ValidatedColor(199, 100, 46);
		public ValidatedDouble followRangeAmount = new ValidatedDouble(4.0, 64.0, 0.0);
		public ValidatedColor followRangeColor = new ValidatedColor(95, 168, 211);
		public ValidatedDouble scaleAmount = new ValidatedDouble(0.2, 2.0, 0.0);
		public ValidatedColor scaleColor = new ValidatedColor(155, 89, 182);
	}

	public static class Misc extends ConfigSection {
		public ValidatedBoolean disableVanillaSpiderEffects = new ValidatedBoolean(true);
		public ValidatedBoolean healZombieLeaders = new ValidatedBoolean(true);
		public ValidatedInt cloudEffectDurationCap = new ValidatedInt(200, 72000, -1, ValidatedNumber.WidgetType.TEXTBOX);
		public ValidatedBoolean onlyBuffedMobsSpreadSculk = new ValidatedBoolean(false);
		public ValidatedBoolean debugLogging = new ValidatedBoolean(false);
	}
}
