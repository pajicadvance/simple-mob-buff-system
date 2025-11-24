package me.pajic.smbs.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ItemSuggestions {
	private static final List<String> VALUES = new ArrayList<>();

	public static void update() {
		VALUES.clear();
		VALUES.addAll(BuiltInRegistries.ITEM.keySet().stream().map(ResourceLocation::toString).toList());
		BuiltInRegistries.ITEM./*? if > 1.21.1 {*/listTagIds()/*?} else {*//*getTagNames()*//*?}*/
				.forEach(tag -> VALUES.add("#" + tag.location()));
	}

	public static List<String> get() {
		return VALUES;
	}
}
