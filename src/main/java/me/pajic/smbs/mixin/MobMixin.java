package me.pajic.smbs.mixin;

import me.pajic.smbs.Main;
import me.pajic.smbs.access.MobAccess;
import me.pajic.smbs.system.BuffHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if < 1.21.8 {
import net.minecraft.nbt.CompoundTag;
//?}
//? if >= 1.21.8 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements MobAccess {
    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique private int buffLevel = 0;
    @Unique private int maxBuffLevel = 0;
    @Unique private boolean shouldDropRewards = false;
    @Unique private final Mob thisMob = (Mob) (Object) this;
    @Unique private boolean fromSpawner = false;
    @Unique private boolean buffApplyAttempted = false;

    /**
     * @author pajic
     * @reason Gets the reason for the mob spawn for later checking.
     */
    @Inject(
            method = "finalizeSpawn",
            at = @At("HEAD")
    )
    private void getSpawnReason(ServerLevelAccessor level, DifficultyInstance difficulty, /*? if < 1.21.8 {*/MobSpawnType spawnType/*?}*//*? if >= 1.21.8 {*//*EntitySpawnReason spawnType*//*?}*/, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        fromSpawner = /*? if < 1.21.8 {*/MobSpawnType/*?}*//*? if >= 1.21.8 {*//*EntitySpawnReason*//*?}*/.isSpawner(spawnType);
    }

    /**
     * @author pajic
     * @reason Once the mob starts ticking, invokes the mob buff system to apply mob buffs,
     * if buff application hasn't been attempted yet and the mob didn't spawn from a spawner.
     */
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void applyBuffs(CallbackInfo ci) {
        if (!level().isClientSide() && !buffApplyAttempted) {
            buffApplyAttempted = true;
            if (!Main.CONFIG.allowBuffedMobsFromSpawners.get() && fromSpawner) return;
            BuffHandler.applyBuffs(thisMob, (ServerLevel) level(), level().getCurrentDifficultyAt(thisMob.getOnPos()));
        }
    }

    /**
     * @author pajic
     * @reason Once the mob is killed, invokes the mob buff system to drop XP and loot based on the mob's buff level.
     */
    @Inject(
            method = "dropCustomDeathLoot",
            at = @At("TAIL")
    )
    private void dropRewards(ServerLevel level, DamageSource damageSource, boolean recentlyHit, CallbackInfo ci) {
        BuffHandler.dropRewards(thisMob, /*? if < 1.21.8 {*/lastHurtByPlayerTime/*?}*//*? if >= 1.21.8 {*//*lastHurtByPlayerMemoryTime*//*?}*/, level);
    }

    @Override
    public int smbs$getBuffLevel() {
        return buffLevel;
    }

    @Override
    public void smbs$increaseBuffLevel(int increase) {
        buffLevel += increase;
    }

    @Override
    public int smbs$getMaxBuffLevel() {
        return maxBuffLevel;
    }

    @Override
    public void smbs$increaseMaxBuffLevel(int increase) {
        maxBuffLevel += increase;
    }

    @Override
    public boolean smbs$getShouldDropRewards() {
        return shouldDropRewards;
    }

    @Override
    public void smbs$setShouldDropRewards(boolean shouldDropRewards) {
        this.shouldDropRewards = shouldDropRewards;
    }

    //? if < 1.21.8 {
    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void save(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("BuffLevel", buffLevel);
        compound.putInt("MaxBuffLevel", maxBuffLevel);
        compound.putBoolean("ShouldDropRewards", shouldDropRewards);
        compound.putBoolean("BuffApplyAttempted", buffApplyAttempted);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void load(CompoundTag compound, CallbackInfo ci) {
        buffLevel = compound.getInt("BuffLevel");
        maxBuffLevel = compound.getInt("MaxBuffLevel");
        shouldDropRewards = compound.getBoolean("ShouldDropRewards");
        buffApplyAttempted = compound.getBoolean("BuffApplyAttempted");
    }
    //?}
    //? if >= 1.21.8 {
    /*@Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void save(ValueOutput output, CallbackInfo ci) {
        output.putInt("BuffLevel", buffLevel);
        output.putInt("MaxBuffLevel", maxBuffLevel);
        output.putBoolean("ShouldDropRewards", shouldDropRewards);
        output.putBoolean("BuffApplyAttempted", buffApplyAttempted);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void load(ValueInput input, CallbackInfo ci) {
        buffLevel = input.getIntOr("BuffLevel", 0);
        maxBuffLevel = input.getIntOr("MaxBuffLevel", 0);
        shouldDropRewards = input.getBooleanOr("ShouldDropRewards", false);
        buffApplyAttempted = input.getBooleanOr("BuffApplyAttempted", true);
    }
    *///?}
}
