package me.pajic.smbs.platform.fabric;

//? fabric {

import me.pajic.smbs.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform implements Platform {

	@Override
	public boolean isDebug() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}
//?}
