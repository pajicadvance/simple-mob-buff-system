package me.pajic.smbs.mixin;

import me.pajic.smbs.SMBS;
import me.pajic.smbs.util.AttachmentUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Mob {
    protected ZombieMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author pajic
     * @reason Saves a boolean value to entity data indicating whether the zombie is a leader or not,
     * for use in the mob buff system. Also heals the zombie to max health to fix a vanilla bug.
     */
    @Inject(
            method = "handleAttributes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/zombie/Zombie;setCanBreakDoors(Z)V"
            )
    )
    private void markLeader(float difficulty, CallbackInfo ci) {
        heal(getMaxHealth());
		AttachmentUtil.setLeader((Zombie) (Object) this);
        SMBS.debugLog(
                "Leader {} at {} {} {}",
                getName().getString(), getX(), getY(), getZ()
        );
    }
}
