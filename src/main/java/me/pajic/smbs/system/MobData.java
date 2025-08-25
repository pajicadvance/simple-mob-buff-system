package me.pajic.smbs.system;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.monster.Zombie;

public class MobData {
    public static EntityDataAccessor<Boolean> IS_LEADER = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);

    public static void init() {}
}
