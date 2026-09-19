package me.pajic.smbs.platform.fabric;

//? fabric {

//~ if <26.1 'ClientLevelEvents' -> 'ClientWorldEvents' {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.smbs.util.ItemSuggestions;import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        //~ if <26.1 'AFTER_CLIENT_LEVEL_CHANGE' -> 'AFTER_CLIENT_WORLD_CHANGE'
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> ItemSuggestions.update(level));
	}
}
//~}
//?}
