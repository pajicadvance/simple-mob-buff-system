package me.pajic.smbs.system;

import me.pajic.smbs.SMBS;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StatBoostEffects {
	public static final MobEffect KNOCKBACK_RESISTANCE = new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
			Attributes.KNOCKBACK_RESISTANCE,
			SMBS.id("effect.knockback_resist"),
			0.2, AttributeModifier.Operation.ADD_VALUE
	);
	public static final MobEffect KNOCKBACK_BOOST = new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
			Attributes.ATTACK_KNOCKBACK,
			SMBS.id("effect.attack_knockback"),
			1, AttributeModifier.Operation.ADD_VALUE
	);
	public static final MobEffect FARSIGHT = new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
			Attributes.FOLLOW_RANGE,
			SMBS.id("effect.follow_range"),
			4, AttributeModifier.Operation.ADD_VALUE
	);
	public static final MobEffect SIZE_BOOST = new MobEffect(MobEffectCategory.BENEFICIAL, 0x8a8a8a).addAttributeModifier(
			Attributes.SCALE,
			SMBS.id("effect.scale"),
			0.2, AttributeModifier.Operation.ADD_VALUE
	);

    public static void init() {}
}
