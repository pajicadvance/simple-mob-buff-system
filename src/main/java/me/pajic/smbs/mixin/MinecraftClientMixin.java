package me.pajic.smbs.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import me.pajic.smbs.util.ItemSuggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftClientMixin {

	@Inject(
			//? if < 1.21.11
			//method = "updateLevelInEngines",
			//? if >= 1.21.11
			method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V",
			at = @At("TAIL")
	)
	private void afterClientWorldChange(ClientLevel clientLevel, /*? if >= 1.21.11 {*/boolean bl,/*?}*/ CallbackInfo ci) {
		ItemSuggestions.update();
	}
}
