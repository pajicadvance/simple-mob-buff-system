package me.pajic.smbs.system;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.util.AttachmentUtil;
import me.pajic.smbs.util.Buffs;
import me.pajic.smbs.util.MobExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
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

//? <26.1
//import me.pajic.smbs.mixin.MobAccessor;

public class BuffHandler {

    /**
     * Applies buffs to the mob according to data from the mod configuration and current local difficulty in the world.
     * @param mob The mob which the buffs are being applied to.
     * @param levelAccessor Server world accessor.
     * @param difficulty World difficulty data.
     */
    public static void applyBuffs(Mob mob, ServerLevelAccessor levelAccessor, DifficultyInstance difficulty) {
        RandomSource random = levelAccessor.getRandom();
        ((MobExtension) mob).smbs$setShouldDropRewards(true);
        Object2IntArrayMap<Identifier> buffs = getBuffs(mob);
        ((MobExtension) mob).smbs$increaseMaxBuffLevel(sumLevels(buffs));
        ((MobExtension) mob).smbs$setMaxBuffLevelFinal();
        float baseMaxHealth = mob.getMaxHealth();
        if (roll(random, difficulty, SMBS.CONFIG.minSpawnChance.get(), SMBS.CONFIG.maxSpawnChance.get())) {
            // Apply buffs and increase buff level for each buff
            buffs.forEach((buff, i) -> {
                if (roll(random, difficulty, SMBS.CONFIG.minChancePerBuff.get(), SMBS.CONFIG.maxChancePerBuff.get())) {
                    int maxAmplifier = i - 1;
                    int amplifier = Mth.clamp(
                            Math.round(difficulty.getSpecialMultiplier() * random.nextFloat() * maxAmplifier * maxAmplifier),
                            0, maxAmplifier
                    );
                    if (applyBuff(mob, buff, amplifier)) ((MobExtension) mob).smbs$increaseBuffLevel(1 + amplifier);
                }
            });
        }
        // Increase current and max buff level for each enchanted piece of equipment
        // Max buff level is increased too because we don't want this increase to
        // contribute to the enchanted book drop chance
        int increase = 0;
        if (getWeapon(mob).isEnchanted()) increase++;
        if (mob.getItemBySlot(EquipmentSlot.HEAD).isEnchanted()) increase++;
        if (mob.getItemBySlot(EquipmentSlot.CHEST).isEnchanted()) increase++;
        if (mob.getItemBySlot(EquipmentSlot.LEGS).isEnchanted()) increase++;
        if (mob.getItemBySlot(EquipmentSlot.FEET).isEnchanted()) increase++;
        ((MobExtension) mob).smbs$increaseBuffLevel(increase);
        ((MobExtension) mob).smbs$increaseMaxBuffLevel(increase);
        // Increase current buff level if mob is a zombie leader
        boolean isLeader = false;
        if (mob instanceof Zombie zombie && AttachmentUtil.isLeader(zombie)) {
            ((MobExtension) mob).smbs$increaseBuffLevel(6);
            isLeader = true;
        }
        // If mob has a health buff make sure it spawns with max possible health
        if (mob.getMaxHealth() > baseMaxHealth) mob.heal(mob.getMaxHealth());

        if (((MobExtension) mob).smbs$getBuffLevel() > 0) SMBS.debugLog(
                "Buff level {} mob {} at {} {} {}\nArmor bonus: {}\nLeader: {}",
                ((MobExtension) mob).smbs$getBuffLevel(), mob.getName().getString(), mob.getX(), mob.getY(), mob.getZ(), increase, isLeader
        );
    }

    private static boolean roll(RandomSource random, DifficultyInstance difficulty, float minPercent, float maxPercent) {
        return random.nextFloat() < Mth.lerp(getNormalizedDifficulty(difficulty), minPercent / 100, maxPercent / 100);
    }

    private static float getNormalizedDifficulty(DifficultyInstance difficulty) {
        return difficulty.getEffectiveDifficulty() < 2 ? 0 : (difficulty.getEffectiveDifficulty() - 2) / 4.75F;
    }

    /**
     * Drops custom loot based on how high the buff level of the mob is.
     * @param mob The mob which is dropping the loot.
     * @param lastHurtByPlayerTime Time since the mob was last hit by the player.
     * @param level The current server world.
     */
    public static void dropRewards(Mob mob, int lastHurtByPlayerTime, ServerLevel level) {
        if (((MobExtension) mob).smbs$getShouldDropRewards() && lastHurtByPlayerTime > 0 && mobsDropLoot(level)) {
            // Determine max possible buff level
            if (!((MobExtension) mob).smbs$isMaxBuffLevelFinal()) {
                ((MobExtension) mob).smbs$increaseMaxBuffLevel(sumLevels(getBuffs(mob)));
                ((MobExtension) mob).smbs$setMaxBuffLevelFinal();
            }

            int maxBuffLevel = ((MobExtension) mob).smbs$getMaxBuffLevel();
            int buffLevel = ((MobExtension) mob).smbs$getBuffLevel();
            int buffLevelThreshold = Math.round(maxBuffLevel * (SMBS.CONFIG.enchantedBookThresholdPercentage.get() / 100));
            // Increase equipment drop chances
            if (SMBS.CONFIG.increasedEquipmentDropChancePerBuff.get() && maxBuffLevel > 0 && buffLevel > 0) {
                float multiplier = Mth.lerp(
                        Mth.clamp((float) buffLevel / maxBuffLevel, 0F, 1F), 1F, SMBS.CONFIG.maxEquipmentDropChanceIncrease.get()
                );
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    mob.setDropChance(slot, getDropChanceForSlot(mob, slot) * multiplier);
                }
            }
            // Drop enchanted book
            if (buffLevel > buffLevelThreshold) {
                List<Holder<Enchantment>> enchantments = new ArrayList<>();
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT).forEach(enchantments::add);
                Util.getRandomSafe(enchantments, level.getRandom()).ifPresent(enchantment -> {
                    int i = Mth.nextInt(
                            level.getRandom(), enchantment.value().getMinLevel(),
                            Math.min(buffLevel - buffLevelThreshold + 2, enchantment.value().getMaxLevel())
                    );
                    ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                    book.enchant(enchantment, i);
                    mob.spawnAtLocation(/*? if >=26.1 {*/level, /*?}*/book);
                });
            }
            // Drop XP orbs
            if (buffLevel > 0) {
                for (int i = 0; i < buffLevel; i++) {
                    level.addFreshEntity(new ExperienceOrb(level, mob.getX(), mob.getY(), mob.getZ(), SMBS.CONFIG.xpPerBuffLevel.get()));
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

    private static Object2IntArrayMap<Identifier> getBuffs(Mob mob) {
        Object2IntArrayMap<Identifier> buffs = new Object2IntArrayMap<>();
        Identifier mobTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        ItemStack weapon = getWeapon(mob);
        SMBS.CONFIG.buffRules.forEach(buffEntry -> {
            if (!buffEntry.mobs.get().contains(mobTypeKey)) return;
            switch (buffEntry.buffSetType.get()) {
                case WEAPON_BASED -> {
                    switch (buffEntry.weaponType.get()) {
                        case ANY -> {
                            if (isRangedWeapon(weapon)) addBuffs(buffs, SMBS.CONFIG.defaultRangedBuffs);
                            else addBuffs(buffs, SMBS.CONFIG.defaultMeleeBuffs);
                        }
                        case MELEE -> {
                            if (isMeleeWeapon(weapon)) addBuffs(buffs, SMBS.CONFIG.defaultMeleeBuffs);
                        }
                        case RANGED -> {
                            if (isRangedWeapon(weapon)) addBuffs(buffs, SMBS.CONFIG.defaultRangedBuffs);
                        }
                        case CUSTOM -> {

                        }
                    }
                }
                case CUSTOM -> {
                    switch (buffEntry.weaponType.get()) {
                        case ANY -> addBuffs(buffs, buffEntry.customBuffs);
                        case MELEE -> {
                            if (isMeleeWeapon(weapon)) addBuffs(buffs, buffEntry.customBuffs);
                        }
                        case RANGED -> {
                            if (isRangedWeapon(weapon)) addBuffs(buffs, buffEntry.customBuffs);
                        }
                        case CUSTOM -> {
                            if (weaponMatches(weapon, buffEntry.customWeapons)) addBuffs(buffs, buffEntry.customBuffs);
                        }
                    }
                }
            }
        });
        return buffs;
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
        for (String key : keys) {
            boolean isTag = key.startsWith("#");
            Identifier id = Identifier.tryParse(isTag ? key.substring(1) : key);
            if (id == null) continue;
            if (isTag) {
                if (stack.is(TagKey.create(Registries.ITEM, id))) return true;
            } else if (BuiltInRegistries.ITEM.getOptional(id).map(stack::is).orElse(false)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMeleeWeapon(ItemStack stack) {
        return weaponMatches(stack, SMBS.CONFIG.meleeWeapons);
    }

    private static boolean isRangedWeapon(ItemStack stack) {
        return weaponMatches(stack, SMBS.CONFIG.rangedWeapons);
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
