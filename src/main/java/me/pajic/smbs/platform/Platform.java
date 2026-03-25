package me.pajic.smbs.platform;

public interface Platform {

	boolean isDevelopmentEnvironment();

	default boolean isDebug() {
		return isDevelopmentEnvironment();
	}
}
