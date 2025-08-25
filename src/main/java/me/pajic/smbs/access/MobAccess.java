package me.pajic.smbs.access;

public interface MobAccess {
    int smbs$getBuffLevel();
    void smbs$increaseBuffLevel(int increase);
    int smbs$getMaxBuffLevel();
    void smbs$increaseMaxBuffLevel(int increase);
    boolean smbs$getShouldDropRewards();
    void smbs$setShouldDropRewards(boolean shouldDropRewards);
}
