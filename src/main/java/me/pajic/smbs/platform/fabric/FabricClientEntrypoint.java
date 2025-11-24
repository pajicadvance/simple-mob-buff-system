package me.pajic.smbs.platform.fabric;

//? fabric {

import me.pajic.smbs.SMBS;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		SMBS.onInitializeClient();
	}
}
//?}
