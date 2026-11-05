package me.pajic.smbs.mixin;

import me.pajic.smbs.SMBS;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AreaEffectCloud.class)
public class AreaEffectCloudMixin {

    /**
     * @author pajic
     * @reason Creepers leave effect clouds which transfer the effects they had to other entities.
     * Since the mob buff system assigns effects that last forever,
     * we limit the effect duration when it gets transferred to another entity.
     */
    @ModifyArg(
            //~ if <26.1 'serverTick' -> 'tick'
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private MobEffectInstance preventInfiniteEffectTransfer(MobEffectInstance effectInstance) {
        int durationCap = SMBS.CONFIG.misc.cloudEffectDurationCap.get();
        return durationCap >= 0 && effectInstance.isInfiniteDuration() ? new MobEffectInstance(
                effectInstance.getEffect(),
                durationCap,
                effectInstance.getAmplifier(),
                effectInstance.isAmbient(),
                effectInstance.isVisible()
        ) : effectInstance;
    }
}
