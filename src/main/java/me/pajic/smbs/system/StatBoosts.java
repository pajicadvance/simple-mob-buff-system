package me.pajic.smbs.system;

import me.pajic.smbs.SMBS;
import me.pajic.smbs.mixin.LivingEntityAccessor;
import me.pajic.smbs.util.Buffs;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public record StatBoosts(Holder<Attribute> attribute, DoubleSupplier amountPerLevel, IntSupplier color) {

	private static final Map<Identifier, StatBoosts> BOOSTS = Map.of(
			Buffs.KNOCKBACK_RESISTANCE, new StatBoosts(
					Attributes.KNOCKBACK_RESISTANCE,
					() -> SMBS.CONFIG.statBoosts.knockbackResistanceAmount.get(),
					() -> SMBS.CONFIG.statBoosts.knockbackResistanceColor.get().argb()
			),
			Buffs.KNOCKBACK_BOOST, new StatBoosts(
					Attributes.ATTACK_KNOCKBACK,
					() -> SMBS.CONFIG.statBoosts.attackKnockbackAmount.get(),
					() -> SMBS.CONFIG.statBoosts.attackKnockbackColor.get().argb()
			),
			Buffs.FARSIGHT, new StatBoosts(
					Attributes.FOLLOW_RANGE,
					() -> SMBS.CONFIG.statBoosts.followRangeAmount.get(),
					() -> SMBS.CONFIG.statBoosts.followRangeColor.get().argb()
			),
			Buffs.SIZE_BOOST, new StatBoosts(
					Attributes.SCALE,
					() -> SMBS.CONFIG.statBoosts.scaleAmount.get(),
					() -> SMBS.CONFIG.statBoosts.scaleColor.get().argb()
			)
	);

	public static Optional<StatBoosts> get(Identifier id) {
		return Optional.ofNullable(BOOSTS.get(id));
	}

	public static Set<Identifier> ids() {
		return BOOSTS.keySet();
	}

	public static List<ParticleOptions> getParticles(Mob mob) {
		List<ParticleOptions> particles = new ArrayList<>();
		if (!SMBS.CONFIG.statBoosts.showParticles.get()) return particles;
		BOOSTS.forEach((id, boost) -> {
			AttributeInstance instance = mob.getAttribute(boost.attribute);
			if (instance != null && instance.hasModifier(id)) {
				particles.add(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, boost.color.getAsInt()));
			}
		});
		return particles;
	}

	public boolean apply(Mob mob, Identifier id, int amplifier) {
		AttributeInstance instance = mob.getAttribute(attribute);
		if (instance == null) return false;
		instance.addOrReplacePermanentModifier(new AttributeModifier(id, amountPerLevel.getAsDouble() * (amplifier + 1), AttributeModifier.Operation.ADD_VALUE));
		((LivingEntityAccessor) mob).smbs$setEffectsDirty(true);
		return true;
	}
}
