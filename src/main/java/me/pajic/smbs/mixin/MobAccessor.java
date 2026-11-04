package me.pajic.smbs.mixin;

//? <26.1 {

/*import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {

    @Accessor("handDropChances")
    float[] smbs$getHandDropChances();

    @Accessor("armorDropChances")
    float[] smbs$getArmorDropChances();

    @Accessor("bodyArmorDropChance")
    float smbs$getBodyArmorDropChance();
}
*///?}
