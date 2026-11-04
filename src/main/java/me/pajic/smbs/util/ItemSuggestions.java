package me.pajic.smbs.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemSuggestions {

	private static volatile List<String> VALUES = List.of();
	private static volatile Set<String> LOOKUP = Set.of();

	public static void update(LevelAccessor level) {
        //~ if <26.1 'lookupOrThrow' -> 'registryOrThrow'
        Registry<Item> registry = level.registryAccess().lookupOrThrow(Registries.ITEM);
		List<String> values = new ArrayList<>(registry.keySet().stream().map(Identifier::toString).toList());
        //~ if <26.1 'listTagIds' -> 'getTagNames'
        //~ if <26.1 'tag.location()' -> 'tag.location()'
        registry.listTagIds().forEach(tag -> values.add("#" + tag.location()));
		LOOKUP = Set.copyOf(values);
		VALUES = List.copyOf(values);
	}

	public static List<String> get() {
		return VALUES;
	}

	public static boolean contains(String value) {
		return LOOKUP.contains(value);
	}
}
