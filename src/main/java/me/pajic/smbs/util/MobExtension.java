package me.pajic.smbs.util;

public interface MobExtension {
    int smbs$getBuffLevel();
    void smbs$increaseBuffLevel(int increase);
    int smbs$getMaxBuffLevel();
    void smbs$increaseMaxBuffLevel(int increase);
    boolean smbs$isMaxBuffLevelFinal();
    void smbs$setMaxBuffLevelFinal();
    boolean smbs$getShouldDropRewards();
    void smbs$setShouldDropRewards(boolean shouldDropRewards);
}
