package me.pajic.smbs.platform.neoforge;

//? neoforge {

/*import me.pajic.smbs.SMBS;
import me.pajic.smbs.util.AttachmentUtil;
import me.pajic.smbs.util.RegistrySuggestions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
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
    private static void onLevelLoad(LevelEvent.Load event) {
        RegistrySuggestions.updateAll(event.getLevel());
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
