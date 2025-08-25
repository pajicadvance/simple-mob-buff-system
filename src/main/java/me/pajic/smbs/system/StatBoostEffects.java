package me.pajic.smbs.system;

import me.pajic.smbs.Main;
import me.pajic.smbs.util.Buffs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StatBoostEffects {

    public static void init() {
        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                Buffs.KNOCKBACK_RESISTANCE,
                new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
                        Attributes.KNOCKBACK_RESISTANCE,
                        Main.id("effect.knockback_resist"),
                        0.2, AttributeModifier.Operation.ADD_VALUE
                )
        );
        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                Buffs.KNOCKBACK_BOOST,
                new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
                        Attributes.ATTACK_KNOCKBACK,
                        Main.id("effect.attack_knockback"),
                        1, AttributeModifier.Operation.ADD_VALUE
                )
        );
        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                Buffs.FARSIGHT,
                new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
                        Attributes.FOLLOW_RANGE,
                        Main.id("effect.follow_range"),
                        4, AttributeModifier.Operation.ADD_VALUE
                )
        );
        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                Buffs.SIZE_BOOST,
                new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
                        Attributes.SCALE,
                        Main.id("effect.scale"),
                        0.2, AttributeModifier.Operation.ADD_VALUE
                )
        );
    }
}
