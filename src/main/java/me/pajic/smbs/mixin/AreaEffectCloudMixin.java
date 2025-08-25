package me.pajic.smbs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AreaEffectCloud.class)
public class AreaEffectCloudMixin {

    /**
     * @author pajic
     * @reason Creepers leave effect clouds which transfer the effects they had to other entities.
     * Since the mob buff system assigns effects that last forever,
     * we limit the effect duration when it gets transferred to a player.
     */
    @ModifyArg(
            method = /*? if < 1.21.8 {*/"tick"/*?}*//*? if >= 1.21.8 {*//*"serverTick"*//*?}*/,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private MobEffectInstance preventInfiniteEffectOnPlayer(MobEffectInstance effectInstance, @Local LivingEntity livingEntity) {
        return livingEntity instanceof Player && effectInstance.getDuration() == -1 ? new MobEffectInstance(
                effectInstance.getEffect(),
                200,
                effectInstance.getAmplifier(),
                effectInstance.isAmbient(),
                effectInstance.isVisible()
        ) : effectInstance;
    }
}
