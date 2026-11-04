package me.pajic.smbs.system;

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

public record StatBoosts(Holder<Attribute> attribute, double amountPerLevel, int color) {

	private static final Map<Identifier, StatBoosts> BOOSTS = Map.of(
			Buffs.KNOCKBACK_RESISTANCE, new StatBoosts(Attributes.KNOCKBACK_RESISTANCE, 0.2, 0xFF7A6A58),
			Buffs.KNOCKBACK_BOOST, new StatBoosts(Attributes.ATTACK_KNOCKBACK, 1, 0xFFC7642E),
			Buffs.FARSIGHT, new StatBoosts(Attributes.FOLLOW_RANGE, 4, 0xFF5FA8D3),
			Buffs.SIZE_BOOST, new StatBoosts(Attributes.SCALE, 0.2, 0xFF9B59B6)
	);

	public static Optional<StatBoosts> get(Identifier id) {
		return Optional.ofNullable(BOOSTS.get(id));
	}

	public static Set<Identifier> ids() {
		return BOOSTS.keySet();
	}

	public static List<ParticleOptions> getParticles(Mob mob) {
		List<ParticleOptions> particles = new ArrayList<>();
		BOOSTS.forEach((id, boost) -> {
			AttributeInstance instance = mob.getAttribute(boost.attribute);
			if (instance != null && instance.hasModifier(id)) {
				particles.add(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, boost.color));
			}
		});
		return particles;
	}

	public boolean apply(Mob mob, Identifier id, int amplifier) {
		AttributeInstance instance = mob.getAttribute(attribute);
		if (instance == null) return false;
		instance.addOrReplacePermanentModifier(new AttributeModifier(id, amountPerLevel * (amplifier + 1), AttributeModifier.Operation.ADD_VALUE));
		((LivingEntityAccessor) mob).smbs$setEffectsDirty(true);
		return true;
	}
}
