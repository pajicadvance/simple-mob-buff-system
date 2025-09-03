package me.pajic.smbs.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.pajic.smbs.Main;
import me.pajic.smbs.access.MobAccess;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
public class CatalystListenerMixin {

    @WrapMethod(method = "handleGameEvent")
    private boolean noSculkSpreadIfMobNotBuffed(ServerLevel level, Holder<GameEvent> gameEvent, GameEvent.Context context, Vec3 pos, Operation<Boolean> original) {
        if (Main.CONFIG.onlyBuffedMobsSpreadSculk.get()) {
            return context.sourceEntity() instanceof Mob mob && ((MobAccess) mob).smbs$getBuffLevel() > 0 ?
                    original.call(level, gameEvent, context, pos) : false;
        }
        return original.call(level, gameEvent, context, pos);
    }
}
