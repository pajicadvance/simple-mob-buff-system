package me.pajic.smbs.platform.neoforge;

//? neoforge {

/*import me.pajic.smbs.SMBS;
import me.pajic.smbs.system.StatBoostEffects;
import me.pajic.smbs.util.AttachmentUtil;
import me.pajic.smbs.util.Buffs;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(SMBS.MOD_ID)
@EventBusSubscriber(modid = SMBS.MOD_ID)
public class NeoforgeEntrypoint {

	@SubscribeEvent
	private static void onCommonSetup(FMLCommonSetupEvent event) {
		SMBS.onInitialize();
	}

	@SubscribeEvent
	private static void initRegistry(RegisterEvent event) {
		StatBoostEffects.init();
		event.register(
				Registries.MOB_EFFECT,
				registry -> {
					registry.register(Buffs.KNOCKBACK_RESISTANCE, StatBoostEffects.KNOCKBACK_RESISTANCE);
					registry.register(Buffs.KNOCKBACK_BOOST, StatBoostEffects.KNOCKBACK_BOOST);
					registry.register(Buffs.FARSIGHT, StatBoostEffects.FARSIGHT);
					registry.register(Buffs.SIZE_BOOST, StatBoostEffects.SIZE_BOOST);
				}
		);
	}

	@SubscribeEvent
	private static void registerDataAttachments(RegisterEvent event) {
		AttachmentUtil.init();
		event.register(
				NeoForgeRegistries.ATTACHMENT_TYPES.key(),
				registry -> registry.register(
						SMBS.id("leader"), AttachmentUtil.LEADER
				)
		);
	}
}
*///?}
