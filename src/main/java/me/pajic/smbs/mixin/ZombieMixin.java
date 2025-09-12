package me.pajic.smbs.mixin;

import me.pajic.smbs.Main;
import me.pajic.smbs.util.ZombieExtension;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
public abstract class ZombieMixin extends Mob implements ZombieExtension {
    protected ZombieMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Unique private static final EntityDataAccessor<Boolean> IS_LEADER = SynchedEntityData.defineId(
            Zombie.class, EntityDataSerializers.BOOLEAN
    );

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
        entityData.set(IS_LEADER, true);
        Main.debugLog(
                "Leader {} at {} {} {}",
                getName().getString(), getX(), getY(), getZ()
        );
    }

    @Override
    public boolean smbs$isLeader() {
        return entityData.get(IS_LEADER);
    }

    @Inject(
            method = "defineSynchedData",
            at = @At("TAIL")
    )
    private void defineLeaderData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(IS_LEADER, false);
    }

    //? if < 1.21.8 {
    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void saveLeaderData(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("IsLeader", entityData.get(IS_LEADER));
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void readLeaderData(CompoundTag compound, CallbackInfo ci) {
        entityData.set(IS_LEADER, compound.getBoolean("IsLeader"));
    }
    //?}
    //? if >= 1.21.8 {
    /*@Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void saveLeaderData(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("IsLeader", entityData.get(IS_LEADER));
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void readLeaderData(ValueInput input, CallbackInfo ci) {
        entityData.set(IS_LEADER, input.getBooleanOr("IsLeader", false));
    }
    *///?}
}
