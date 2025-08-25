package me.pajic.smbs.mixin;

import me.pajic.smbs.Main;
import me.pajic.smbs.system.MobData;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if < 1.21.8 {
import net.minecraft.nbt.CompoundTag;
//?}
//? if >= 1.21.8 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}

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
                    target = "Lnet/minecraft/world/entity/monster/Zombie;setCanBreakDoors(Z)V"
            )
    )
    private void markLeader(float difficulty, CallbackInfo ci) {
        heal(getMaxHealth());
        entityData.set(MobData.IS_LEADER, true);
        Main.debugLog(
                "Leader {} at {} {} {}",
                getName().getString(), getX(), getY(), getZ()
        );
    }

    @Inject(
            method = "defineSynchedData",
            at = @At("TAIL")
    )
    private void defineLeaderData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(MobData.IS_LEADER, false);
    }

    //? if < 1.21.8 {
    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void saveLeaderData(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("IsLeader", entityData.get(MobData.IS_LEADER));
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void readLeaderData(CompoundTag compound, CallbackInfo ci) {
        entityData.set(MobData.IS_LEADER, compound.getBoolean("IsLeader"));
    }
    //?}
    //? if >= 1.21.8 {
    /*@Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void saveLeaderData(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("IsLeader", entityData.get(MobData.IS_LEADER));
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void readLeaderData(ValueInput input, CallbackInfo ci) {
        entityData.set(MobData.IS_LEADER, input.getBooleanOr("IsLeader", false));
    }
    *///?}
}
