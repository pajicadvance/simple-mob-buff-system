package me.pajic.smbs.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Spider.class)
public class SpiderMixin {

    /**
     * @author pajic
     * @reason Disables vanilla spider buffs as they're redundant in the new buff system.
     */
    @WrapWithCondition(
            method = "finalizeSpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Spider$SpiderEffectsGroupData;setRandomEffect(Lnet/minecraft/util/RandomSource;)V"
            )
    )
    private boolean disableVanillaBuff(Spider.SpiderEffectsGroupData instance, RandomSource random) {
        return false;
    }

    /**
     * @author pajic
     * @reason Disables vanilla spider buffs as they're redundant in the new buff system.
     */
    @WrapOperation(
            method = "finalizeSpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Spider;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            )
    )
    private boolean disableVanillaBuff(Spider instance, MobEffectInstance mobEffectInstance, Operation<Boolean> original) {
        return false;
    }
}
