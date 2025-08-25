package me.pajic.smbs.system;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.pajic.smbs.Main;
import me.pajic.smbs.access.MobAccess;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuffHandler {

    /**
     * Applies buffs to the mob according to data from the mod configuration based on the current local difficulty in the world.
     * @param mob The mob which the buffs are being applied to.
     * @param levelAccessor Server world accessor.
     * @param difficulty World difficulty data.
     */
    public static void applyBuffs(Mob mob, ServerLevelAccessor levelAccessor, DifficultyInstance difficulty) {
        RandomSource random = levelAccessor.getRandom();
        ((MobAccess) mob).smbs$setShouldDropRewards(true);
        ResourceLocation mobTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        Set<BuffEntry> possibleBuffEntries = new HashSet<>();
        Main.CONFIG.buffRules.forEach((buffEntry, mobTypeKeys) -> {
            if (mobTypeKeys.contains(mobTypeKey)) possibleBuffEntries.add(buffEntry);
        });
        Object2IntArrayMap<Holder<MobEffect>> buffs = getBuffs(possibleBuffEntries, mob);
        if (roll(random, difficulty, Main.CONFIG.minSpawnChance.get(), Main.CONFIG.maxSpawnChance.get())) {
            // Apply buffs and increase buff level for each buff
            buffs.forEach((mobEffect, i) -> {
                if (roll(random, difficulty, Main.CONFIG.minChancePerBuff.get(), Main.CONFIG.maxChancePerBuff.get())) {
                    int maxAmplifier = i - 1;
                    int amplifier = Mth.clamp(
                            Math.round(difficulty.getSpecialMultiplier() * random.nextFloat() * maxAmplifier * maxAmplifier),
                            0, maxAmplifier
                    );
                    mob.addEffect(new MobEffectInstance(mobEffect, -1, amplifier));
                    ((MobAccess) mob).smbs$increaseBuffLevel(1 + amplifier);
                }
            });
        }
        if (mob.getMaxHealth() > 20.0F) mob.heal(mob.getMaxHealth());
        if (((MobAccess) mob).smbs$getBuffLevel() > 0) Main.debugLog(
                "Buff level {} mob {} at {} {} {}",
                ((MobAccess) mob).smbs$getBuffLevel(), mob.getName().getString(), mob.getX(), mob.getY(), mob.getZ()
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
        if (((MobAccess) mob).smbs$getShouldDropRewards() && lastHurtByPlayerTime > 0 && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            // Determine max possible buff level
            ResourceLocation mobTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
            Set<BuffEntry> possibleBuffEntries = new HashSet<>();
            Main.CONFIG.buffRules.forEach((buffEntry, mobTypeKeys) -> {
                if (mobTypeKeys.contains(mobTypeKey)) possibleBuffEntries.add(buffEntry);
            });
            getBuffs(possibleBuffEntries, mob).forEach((mobEffect, i) -> ((MobAccess) mob).smbs$increaseMaxBuffLevel(i));
            // Increase current buff level if mob is a zombie leader
            boolean isLeader = false;
            if (mob instanceof Zombie zombie && zombie.getEntityData().get(MobData.IS_LEADER)) {
                ((MobAccess) mob).smbs$increaseBuffLevel(6);
                isLeader = true;
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
            ((MobAccess) mob).smbs$increaseBuffLevel(increase);
            ((MobAccess) mob).smbs$increaseMaxBuffLevel(increase);

            int maxBuffLevel = ((MobAccess) mob).smbs$getMaxBuffLevel();
            int buffLevel = ((MobAccess) mob).smbs$getBuffLevel();
            int buffLevelThreshold = Math.round(maxBuffLevel * (Main.CONFIG.enchantedBookThresholdPercentage.get() / 100));
            // Drop enchanted book
            if (buffLevel > buffLevelThreshold) {
                List<Holder<Enchantment>> enchantments = new ArrayList<>();
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT).forEach(enchantments::add);
                Util.getRandomSafe(enchantments, level.random).ifPresent(enchantment -> {
                    int i = Mth.nextInt(
                            level.getRandom(), enchantment.value().getMinLevel(),
                            Math.min(buffLevel - buffLevelThreshold + 2, enchantment.value().getMaxLevel())
                    );
                    ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                    book.enchant(enchantment, i);
                    mob.spawnAtLocation(/*? if >= 1.21.8 {*//*level,*//*?}*/ book);
                });
            }
            // Drop XP orbs
            if (buffLevel > 0) {
                for (int i = 0; i < buffLevel; i++) {
                    level.addFreshEntity(new ExperienceOrb(level, mob.getX(), mob.getY(), mob.getZ(), Main.CONFIG.xpPerBuffLevel.get()));
                }
                Main.debugLog(
                        "\nCurrent buff level: {}\nLevel required for book: {}\nMaximum buff level: {}\nArmor bonus: {}\nLeader: {}",
                        buffLevel, buffLevelThreshold + 1, maxBuffLevel, increase, isLeader
                );
            }
        }
    }

    private static Object2IntArrayMap<Holder<MobEffect>> getBuffs(Set<BuffEntry> possibleBuffEntries, Mob mob) {
        Object2IntArrayMap<Holder<MobEffect>> buffs = new Object2IntArrayMap<>();
        ItemStack weapon = getWeapon(mob);
        possibleBuffEntries.forEach(buffEntry -> {
            switch (buffEntry.buffSetType.get()) {
                case WEAPON_BASED -> {
                    switch (buffEntry.weaponType.get()) {
                        case ANY -> {
                            if (isRangedWeapon(weapon)) addBuffs(buffs, Main.CONFIG.defaultRangedBuffs);
                            else addBuffs(buffs, Main.CONFIG.defaultMeleeBuffs);
                        }
                        case MELEE -> {
                            if (isMeleeWeapon(weapon)) addBuffs(buffs, Main.CONFIG.defaultMeleeBuffs);
                        }
                        case RANGED -> {
                            if (isRangedWeapon(weapon)) addBuffs(buffs, Main.CONFIG.defaultRangedBuffs);
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

    private static void addBuffs(Object2IntArrayMap<Holder<MobEffect>> buffs, ValidatedMap<ResourceLocation, Integer> addition) {
        addition.forEach((rl, i) -> buffs.put(
                BuiltInRegistries.MOB_EFFECT./*? if < 1.21.8 {*/getHolder/*?}*//*? if >= 1.21.8 {*//*get*//*?}*/(rl).orElseThrow(), i.intValue())
        );
    }

    private static ItemStack getWeapon(Mob mob) {
        ItemStack mainHand = mob.getMainHandItem();
        ItemStack offHand = mob.getOffhandItem();
        return mainHand.isEmpty() ? offHand : mainHand;
    }

    private static boolean weaponMatches(ItemStack stack, ValidatedSet<String> keys) {
        Set<Item> items = new HashSet<>();
        Set<TagKey<Item>> itemTags = new HashSet<>();
        keys.forEach(string -> {
            if (string.startsWith("#")) itemTags.add(TagKey.create(Registries.ITEM, ResourceLocation.parse(string.substring(1))));
            else items.add(BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(string)).orElseThrow());
        });
        return items.contains(stack.getItem()) ||
                itemTags.stream().anyMatch(itemTag -> BuiltInRegistries.ITEM
                        ./*? if < 1.21.8 {*/getTag/*?}*//*? if >= 1.21.8 {*//*get*//*?}*/(itemTag).orElseThrow().contains(stack.getItemHolder())
                );
    }

    private static boolean isMeleeWeapon(ItemStack stack) {
        return weaponMatches(stack, Main.CONFIG.meleeWeapons);
    }

    private static boolean isRangedWeapon(ItemStack stack) {
        return weaponMatches(stack, Main.CONFIG.rangedWeapons);
    }
}
