package me.pajic.smbs.mixin;

import me.pajic.smbs.SMBS;
import me.pajic.smbs.system.BuffHandler;
import me.pajic.smbs.util.MobExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements MobExtension {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique private int smbs$buffLevel = 0;
    @Unique private int smbs$maxBuffLevel = 0;
    @Unique private boolean smbs$shouldDropRewards = false;
    @Unique private final Mob smbs$thisMob = (Mob) (Object) this;
    @Unique private Boolean smbs$fromSpawner = null;
    @Unique private boolean smbs$buffApplyAttempted = false;

    /**
     * @author pajic
     * @reason Gets the reason for the mob spawn for later checking.
     */
    @Inject(
            method = "finalizeSpawn",
            at = @At("HEAD")
    )
    private void getSpawnReason(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        smbs$fromSpawner = EntitySpawnReason.isSpawner(spawnType);
    }

    /**
     * @author pajic
     * @reason Once the mob starts ticking, invokes the mob buff system to apply mob buffs,
     * if buff application hasn't been attempted yet and the mob didn't spawn from a spawner.
     */
    @SuppressWarnings("resource")
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void applyBuffs(CallbackInfo ci) {
        if (!level().isClientSide() && !smbs$buffApplyAttempted && smbs$fromSpawner != null) {
            smbs$buffApplyAttempted = true;
            if (SMBS.CONFIG.allowBuffedMobsFromSpawners.get() || !smbs$fromSpawner) {
                BuffHandler.applyBuffs(smbs$thisMob, (ServerLevel) level(), ((ServerLevel) level()).getCurrentDifficultyAt(smbs$thisMob.getOnPos()));
            }
        }
    }

    /**
     * @author pajic
     * @reason Once the mob is killed, invokes the mob buff system to drop XP and loot based on the mob's buff level.
     */
    @Inject(
            method = "dropCustomDeathLoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;dropCustomDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V"
            )
    )
    private void dropRewards(ServerLevel level, DamageSource damageSource, boolean recentlyHit, CallbackInfo ci) {
        BuffHandler.dropRewards(smbs$thisMob, lastHurtByPlayerMemoryTime, level);
    }

    @Override
    public int smbs$getBuffLevel() {
        return smbs$buffLevel;
    }

    @Override
    public void smbs$increaseBuffLevel(int increase) {
        smbs$buffLevel += increase;
    }

    @Override
    public int smbs$getMaxBuffLevel() {
        return smbs$maxBuffLevel;
    }

    @Override
    public void smbs$increaseMaxBuffLevel(int increase) {
        smbs$maxBuffLevel += increase;
    }

    @Override
    public boolean smbs$getShouldDropRewards() {
        return smbs$shouldDropRewards;
    }

    @Override
    public void smbs$setShouldDropRewards(boolean shouldDropRewards) {
        this.smbs$shouldDropRewards = shouldDropRewards;
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void save(ValueOutput output, CallbackInfo ci) {
        output.putInt("BuffLevel", smbs$buffLevel);
        output.putInt("MaxBuffLevel", smbs$maxBuffLevel);
        output.putBoolean("ShouldDropRewards", smbs$shouldDropRewards);
        output.putBoolean("BuffApplyAttempted", smbs$buffApplyAttempted);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void load(ValueInput input, CallbackInfo ci) {
        smbs$buffLevel = input.getIntOr("BuffLevel", 0);
        smbs$maxBuffLevel = input.getIntOr("MaxBuffLevel", 0);
        smbs$shouldDropRewards = input.getBooleanOr("ShouldDropRewards", false);
        smbs$buffApplyAttempted = input.getBooleanOr("BuffApplyAttempted", true);
    }
}
