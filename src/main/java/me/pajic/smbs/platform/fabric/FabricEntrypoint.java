package me.pajic.smbs.platform.fabric;

//? fabric {

//~ if <26.1 'ServerLevelEvents' -> 'ServerWorldEvents' {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.system.StatBoostEffects;
import me.pajic.smbs.util.AttachmentUtil;
import me.pajic.smbs.util.Buffs;
import me.pajic.smbs.util.ItemSuggestions;import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		SMBS.onInitialize();
        AttachmentUtil.init();
        StatBoostEffects.init();
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
        ServerLevelEvents.LOAD.register((server, level) -> ItemSuggestions.update(level));
	}
}
//~}
//?}
