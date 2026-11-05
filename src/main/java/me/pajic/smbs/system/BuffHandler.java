package me.pajic.smbs.system;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.config.ModConfig;
import me.pajic.smbs.util.AttachmentUtil;
import me.pajic.smbs.util.Buffs;
import me.pajic.smbs.util.MobExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

//? <26.1
//import me.pajic.smbs.mixin.MobAccessor;

public class BuffHandler {

    private record RuleBuffs(BuffRule rule, Object2IntArrayMap<Identifier> buffs) {}

    public static boolean canBuff(SpawnSource source, ServerLevel level) {
        ModConfig.Spawning spawning = SMBS.CONFIG.spawning;
        if (!spawning.enabled.get() || !spawning.allowedSpawnSources.get().contains(source)) return false;
        //~ if <26.1 'identifier' -> 'location'
        boolean listed = spawning.dimensions.get().contains(level.dimension().identifier());
        return (spawning.dimensionFilterMode.get() == FilterMode.WHITELIST) == listed;
    }

    /**
     * Applies buffs to the mob according to data from the mod configuration and current local difficulty in the world.
     * @param mob The mob which the buffs are being applied to.
     * @param levelAccessor Server world accessor.
     * @param difficulty World difficulty data.
     */
    public static void applyBuffs(Mob mob, ServerLevelAccessor levelAccessor, DifficultyInstance difficulty) {
        RandomSource random = levelAccessor.getRandom();
        ModConfig config = SMBS.CONFIG;
        ((MobExtension) mob).smbs$setShouldDropRewards(true);
        List<RuleBuffs> ruleBuffs = getRuleBuffs(mob);
        ((MobExtension) mob).smbs$increaseMaxBuffLevel(sumLevels(mergeBuffs(ruleBuffs)));
        ((MobExtension) mob).smbs$setMaxBuffLevelFinal();
        float baseMaxHealth = mob.getMaxHealth();
        float chanceProgress = getProgress(difficulty, config.spawning.minChanceDifficulty.get(), config.spawning.maxChanceDifficulty.get());
        float levelProgress = getProgress(difficulty, config.spawning.minLevelDifficulty.get(), config.spawning.maxLevelDifficulty.get());
        float levelExponent = config.spawning.levelExponent.get();
        Object2IntArrayMap<Identifier> rolledBuffs = new Object2IntArrayMap<>();
        for (RuleBuffs entry : ruleBuffs) {
            BuffRule rule = entry.rule();
            if (!roll(random, chanceProgress, rule.minSpawnChance.get(), rule.maxSpawnChance.get())) continue;
            entry.buffs().forEach((buff, maxLevel) -> {
                if (roll(random, chanceProgress, rule.minBuffChance.get(), rule.maxBuffChance.get())) {
                    int maxAmplifier = maxLevel - 1;
                    int amplifier = Mth.clamp(
                            Math.round(levelProgress * random.nextFloat() * (float) Math.pow(maxAmplifier, levelExponent)),
                            0, maxAmplifier
                    );
                    rolledBuffs.mergeInt(buff, amplifier, Math::max);
                }
            });
        }
        List<Identifier> selectedBuffs = new ArrayList<>(rolledBuffs.keySet());
        int maxBuffs = config.spawning.maxBuffsPerMob.get();
        if (maxBuffs > 0 && selectedBuffs.size() > maxBuffs) {
            Util.shuffle(selectedBuffs, random);
            selectedBuffs = selectedBuffs.subList(0, maxBuffs);
        }
        // Apply buffs and increase buff level for each buff
        for (Identifier buff : selectedBuffs) {
            int amplifier = rolledBuffs.getInt(buff);
            if (applyBuff(mob, buff, amplifier)) ((MobExtension) mob).smbs$increaseBuffLevel(1 + amplifier);
        }
        // Increase current and max buff level for each enchanted piece of equipment
        // Max buff level is increased too because we don't want this increase to
        // contribute to the enchanted book drop chance
        int enchantedPieces = 0;
        if (getWeapon(mob).isEnchanted()) enchantedPieces++;
        if (mob.getItemBySlot(EquipmentSlot.HEAD).isEnchanted()) enchantedPieces++;
        if (mob.getItemBySlot(EquipmentSlot.CHEST).isEnchanted()) enchantedPieces++;
        if (mob.getItemBySlot(EquipmentSlot.LEGS).isEnchanted()) enchantedPieces++;
        if (mob.getItemBySlot(EquipmentSlot.FEET).isEnchanted()) enchantedPieces++;
        int increase = enchantedPieces * config.rewards.enchantedEquipmentBonus.get();
        ((MobExtension) mob).smbs$increaseBuffLevel(increase);
        if (!config.rewards.equipmentCountsTowardBook.get()) ((MobExtension) mob).smbs$increaseMaxBuffLevel(increase);
        // Increase current buff level if mob is a zombie leader
        boolean isLeader = false;
        if (mob instanceof Zombie zombie && AttachmentUtil.isLeader(zombie)) {
            ((MobExtension) mob).smbs$increaseBuffLevel(config.rewards.leaderBuffLevelBonus.get());
            isLeader = true;
        }
        // If mob has a health buff make sure it spawns with max possible health
        if (mob.getMaxHealth() > baseMaxHealth) mob.heal(mob.getMaxHealth());

        if (((MobExtension) mob).smbs$getBuffLevel() > 0) SMBS.debugLog(
                "Buff level {} mob {} at {} {} {}\nArmor bonus: {}\nLeader: {}",
                ((MobExtension) mob).smbs$getBuffLevel(), mob.getName().getString(), mob.getX(), mob.getY(), mob.getZ(), increase, isLeader
        );
    }

    private static boolean roll(RandomSource random, float progress, float minPercent, float maxPercent) {
        return random.nextFloat() < Mth.lerp(progress, minPercent / 100, maxPercent / 100);
    }

    private static float getProgress(DifficultyInstance difficulty, float minDifficulty, float maxDifficulty) {
        float low = Math.min(minDifficulty, maxDifficulty);
        float high = Math.max(minDifficulty, maxDifficulty);
        float value = difficulty.getEffectiveDifficulty();
        if (high <= low) return value >= high ? 1F : 0F;
        return Mth.clamp((value - low) / (high - low), 0F, 1F);
    }

    /**
     * Drops custom loot based on how high the buff level of the mob is.
     * @param mob The mob which is dropping the loot.
     * @param lastHurtByPlayerTime Time since the mob was last hit by the player.
     * @param level The current server world.
     */
    public static void dropRewards(Mob mob, int lastHurtByPlayerTime, ServerLevel level) {
        ModConfig.Rewards rewards = SMBS.CONFIG.rewards;
        if (((MobExtension) mob).smbs$getShouldDropRewards() && lastHurtByPlayerTime > 0 && mobsDropLoot(level)) {
            // Determine max possible buff level
            if (!((MobExtension) mob).smbs$isMaxBuffLevelFinal()) {
                ((MobExtension) mob).smbs$increaseMaxBuffLevel(sumLevels(mergeBuffs(getRuleBuffs(mob))));
                ((MobExtension) mob).smbs$setMaxBuffLevelFinal();
            }

            int maxBuffLevel = ((MobExtension) mob).smbs$getMaxBuffLevel();
            int buffLevel = ((MobExtension) mob).smbs$getBuffLevel();
            int buffLevelThreshold = Math.round(maxBuffLevel * (rewards.bookThresholdPercentage.get() / 100));
            // Increase equipment drop chances
            if (rewards.increasedEquipmentDrops.get() && maxBuffLevel > 0 && buffLevel > 0) {
                float multiplier = Mth.lerp(
                        Mth.clamp((float) buffLevel / maxBuffLevel, 0F, 1F), 1F, rewards.maxEquipmentDropMultiplier.get()
                );
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    mob.setDropChance(slot, getDropChanceForSlot(mob, slot) * multiplier);
                }
            }
            // Drop enchanted book
            if (rewards.bookEnabled.get() && buffLevel > buffLevelThreshold) {
                List<Holder<Enchantment>> enchantments = new ArrayList<>();
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .get(TagKey.create(Registries.ENCHANTMENT, rewards.bookEnchantmentTag.get()))
                        .ifPresent(tag -> tag.forEach(enchantments::add));
                Util.getRandomSafe(enchantments, level.getRandom()).ifPresent(enchantment -> {
                    int i = Mth.nextInt(
                            level.getRandom(), enchantment.value().getMinLevel(),
                            Math.min(buffLevel - buffLevelThreshold + rewards.bookBonusLevels.get(), enchantment.value().getMaxLevel())
                    );
                    ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                    book.enchant(enchantment, i);
                    mob.spawnAtLocation(/*? if >=26.1 {*/level, /*?}*/book);
                });
            }
            // Drop XP orbs
            if (rewards.xpEnabled.get() && buffLevel > 0) {
                for (int i = 0; i < buffLevel; i++) {
                    level.addFreshEntity(new ExperienceOrb(level, mob.getX(), mob.getY(), mob.getZ(), rewards.xpPerBuffLevel.get()));
                }
				SMBS.debugLog(
                        "\nCurrent buff level: {}\nLevel required for book: {}\nMaximum buff level: {}",
                        buffLevel, buffLevelThreshold + 1, maxBuffLevel
                );
            }
        }
    }

    private static int sumLevels(Object2IntArrayMap<Identifier> buffs) {
        int sum = 0;
        for (int level : buffs.values()) sum += level;
        return sum;
    }

    private static boolean applyBuff(Mob mob, Identifier buff, int amplifier) {
        Optional<StatBoosts> statBoost = StatBoosts.get(buff);
        return statBoost.map(statBoosts -> statBoosts.apply(mob, buff, amplifier))
                //~ if <26.1 'MOB_EFFECT.get' -> 'MOB_EFFECT.getHolder'
                .orElseGet(() -> BuiltInRegistries.MOB_EFFECT.get(buff)
                .map(effect -> mob.addEffect(new MobEffectInstance(effect, -1, amplifier)))
                .orElse(false));
    }

    private static Object2IntArrayMap<Identifier> mergeBuffs(List<RuleBuffs> ruleBuffs) {
        Object2IntArrayMap<Identifier> merged = new Object2IntArrayMap<>();
        ruleBuffs.forEach(entry -> entry.buffs().forEach((buff, level) -> merged.mergeInt(buff, level, Math::max)));
        return merged;
    }

    @SuppressWarnings("deprecation")
    private static List<RuleBuffs> getRuleBuffs(Mob mob) {
        List<RuleBuffs> result = new ArrayList<>();
        Identifier mobTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        ItemStack weapon = getWeapon(mob);
        boolean ranged = isRangedWeapon(weapon);
        for (BuffRule rule : SMBS.CONFIG.rules.get()) {
            boolean mobMatches = keysMatch(
                    rule.mobs,
                    mobTypeKey::equals,
                    tag -> mob.getType().builtInRegistryHolder().is(TagKey.create(Registries.ENTITY_TYPE, tag))
            );
            if (!mobMatches || !weaponConditionMet(rule, weapon, ranged)) continue;
            String setName = ranged && !rule.rangedBuffSet.get().isEmpty() ? rule.rangedBuffSet.get() : rule.buffSet.get();
            Object2IntArrayMap<Identifier> buffs = new Object2IntArrayMap<>();
            findBuffSet(setName).ifPresent(set -> addBuffs(buffs, set.buffs));
            if (!buffs.isEmpty()) result.add(new RuleBuffs(rule, buffs));
        }
        return result;
    }

    private static boolean weaponConditionMet(BuffRule rule, ItemStack weapon, boolean ranged) {
        return switch (rule.requiredWeapon.get()) {
            case ANY -> true;
            case MELEE -> isMeleeWeapon(weapon);
            case RANGED -> ranged;
            case CUSTOM -> weaponMatches(weapon, rule.customWeapons);
        };
    }

    private static Optional<BuffSet> findBuffSet(String name) {
        if (name.isEmpty()) return Optional.empty();
        return SMBS.CONFIG.buffSets.get().stream().filter(set -> set.name.get().equals(name)).<BuffSet>map(set -> set).findFirst();
    }

    private static void addBuffs(Object2IntArrayMap<Identifier> buffs, ValidatedMap<Identifier, Integer> addition) {
        addition.forEach((buff, i) -> {
            if (Buffs.isValid(buff)) buffs.mergeInt(buff, i, Math::max);
        });
    }

    private static ItemStack getWeapon(Mob mob) {
        ItemStack mainHand = mob.getMainHandItem();
        ItemStack offHand = mob.getOffhandItem();
        return mainHand.isEmpty() ? offHand : mainHand;
    }

    private static boolean weaponMatches(ItemStack stack, ValidatedSet<String> keys) {
        return keysMatch(
                keys,
                id -> BuiltInRegistries.ITEM.getOptional(id).map(stack::is).orElse(false),
                tag -> stack.is(TagKey.create(Registries.ITEM, tag))
        );
    }

    private static boolean keysMatch(ValidatedSet<String> keys, Predicate<Identifier> idMatches, Predicate<Identifier> tagMatches) {
        for (String key : keys) {
            boolean isTag = key.startsWith("#");
            Identifier id = Identifier.tryParse(isTag ? key.substring(1) : key);
            if (id == null) continue;
            if (isTag ? tagMatches.test(id) : idMatches.test(id)) return true;
        }
        return false;
    }

    private static boolean isMeleeWeapon(ItemStack stack) {
        return weaponMatches(stack, SMBS.CONFIG.weapons.meleeWeapons);
    }

    private static boolean isRangedWeapon(ItemStack stack) {
        return weaponMatches(stack, SMBS.CONFIG.weapons.rangedWeapons);
    }

    private static float getDropChanceForSlot(Mob mob, EquipmentSlot slot) {
        //? >=26.1 {
        return mob.getDropChances().byEquipment(slot);
        //?} else {
        /*return switch (slot.getType()) {
            case HAND -> ((MobAccessor) mob).smbs$getHandDropChances()[slot.getIndex()];
            case HUMANOID_ARMOR -> ((MobAccessor) mob).smbs$getArmorDropChances()[slot.getIndex()];
            case ANIMAL_ARMOR -> ((MobAccessor) mob).smbs$getBodyArmorDropChance();
        };
        *///?}
    }

    private static boolean mobsDropLoot(ServerLevel level) {
        //? <26.1
        //return level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        //? >=26.1
        return level.getGameRules().get(GameRules.MOB_DROPS);
    }
}
