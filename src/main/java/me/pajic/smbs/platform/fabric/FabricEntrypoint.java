package me.pajic.smbs.platform.fabric;

//? fabric {

//~ if <26.1 'ServerLevelEvents' -> 'ServerWorldEvents' {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.util.AttachmentUtil;
import me.pajic.smbs.util.ItemSuggestions;import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		SMBS.onInitialize();
        AttachmentUtil.init();
        ServerLevelEvents.LOAD.register((s, level) -> ItemSuggestions.update(level));
	}
}
//~}
//?}
