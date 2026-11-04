package me.pajic.smbs.util;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.Entity;

//? if fabric {
import me.pajic.smbs.SMBS;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
//?} else {
/*import net.neoforged.neoforge.attachment.AttachmentType;
*///?}

public class AttachmentUtil {

	//? if fabric {
	public static final AttachmentType<Boolean> LEADER = AttachmentRegistry.create(
			SMBS.id("leader"),
			builder -> builder
					.initializer(() -> false)
					.persistent(Codec.BOOL)
	);
	//?} else {
	/*public static final AttachmentType<Boolean> LEADER = AttachmentType.builder(() -> false)
			.serialize(Codec.BOOL.fieldOf("leader")/^? <26.1 {^/.codec()/^?}^/)
			.build();
	*///?}

	public static boolean isLeader(Entity entity) {
		//? if fabric
		return entity.getAttachedOrElse(LEADER, false);
		//? if neoforge
		//return entity.hasData(LEADER) && entity.getData(LEADER);
	}

	public static void setLeader(Entity entity) {
		//? if fabric
		entity.setAttached(LEADER, true);
		//? if neoforge
		//entity.setData(LEADER, true);
	}

	public static void init() {}
}
