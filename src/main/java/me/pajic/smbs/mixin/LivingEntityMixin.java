package me.pajic.smbs.mixin;

import me.pajic.smbs.system.StatBoosts;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow @Final private static EntityDataAccessor<List<ParticleOptions>> DATA_EFFECT_PARTICLES;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author pajic
     * @reason The four custom stat boosts are added to mobs as attribute effects,
     * so that we don't have to register them as MobEffects in order to keep the mod fully server-side.
     * This mixin manually adds matching particles to mobs which have the custom stat boosts.
     */
    @Inject(
            method = "updateInvisibilityStatus",
            at = @At("TAIL")
    )
    private void addStatBoostParticles(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Mob mob)) return;
        List<ParticleOptions> boostParticles = StatBoosts.getParticles(mob);
        if (boostParticles.isEmpty()) return;
        List<ParticleOptions> particles = new ArrayList<>(entityData.get(DATA_EFFECT_PARTICLES));
        particles.addAll(boostParticles);
        entityData.set(DATA_EFFECT_PARTICLES, particles);
    }
}
