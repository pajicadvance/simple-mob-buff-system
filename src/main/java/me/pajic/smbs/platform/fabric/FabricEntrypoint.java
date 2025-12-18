package me.pajic.smbs.platform.fabric;

//? fabric {

import me.pajic.smbs.SMBS;
import me.pajic.smbs.system.StatBoostEffects;
import me.pajic.smbs.util.AttachmentUtil;
import me.pajic.smbs.util.Buffs;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

@SuppressWarnings("unused")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		SMBS.onInitialize();
		AttachmentUtil.init();
		StatBoostEffects.init();
		initRegistry();
	}

	private void initRegistry() {
		Registry.register(
				BuiltInRegistries.MOB_EFFECT,
				Buffs.KNOCKBACK_RESISTANCE,
				StatBoostEffects.KNOCKBACK_RESISTANCE
		);
		Registry.register(
				BuiltInRegistries.MOB_EFFECT,
				Buffs.KNOCKBACK_BOOST,
				StatBoostEffects.KNOCKBACK_BOOST
		);
		Registry.register(
				BuiltInRegistries.MOB_EFFECT,
				Buffs.FARSIGHT,
				StatBoostEffects.FARSIGHT
		);
		Registry.register(
				BuiltInRegistries.MOB_EFFECT,
				Buffs.SIZE_BOOST,
				StatBoostEffects.SIZE_BOOST
		);
	}
}
//?}
