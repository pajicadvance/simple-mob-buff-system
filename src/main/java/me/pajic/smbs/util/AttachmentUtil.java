package me.pajic.smbs.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
//? if fabric {
import me.pajic.smbs.SMBS;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
//?} else {
/*import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
*///?}

public class AttachmentUtil {

	//? if fabric {
	public static final AttachmentType<Boolean> LEADER = AttachmentRegistry.create(
			SMBS.id("leader"),
			builder -> builder
					.initializer(() -> false)
					.persistent(Codec.BOOL)
					.syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
	);
	//?} else {
	/*public static final AttachmentType<Boolean> LEADER = AttachmentType.builder(() -> false)
			.serialize(Codec.BOOL.fieldOf("leader"))
			.sync(new AttachmentSyncHandler<>() {
				@Override
				public void write(@NotNull RegistryFriendlyByteBuf buf, @NotNull Boolean attachment, boolean initialSync) {
					ByteBufCodecs.BOOL.encode(buf, attachment);
				}
				@Override
				public Boolean read(@NotNull IAttachmentHolder holder, @NotNull RegistryFriendlyByteBuf buf, Boolean previousValue) {
					return ByteBufCodecs.BOOL.decode(buf);
				}
			})
			.build();
	*///?}

	public static boolean isLeader(Entity entity) {
		//? if fabric
		return entity.getAttachedOrSet(LEADER, false);
		//? if neoforge
		//return entity.getData(LEADER);
	}

	public static void setLeader(Entity entity) {
		//? if fabric
		entity.setAttached(LEADER, true);
		//? if neoforge
		//entity.setData(LEADER, true);
	}

	public static void init() {}
}
