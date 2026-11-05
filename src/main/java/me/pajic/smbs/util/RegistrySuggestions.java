package me.pajic.smbs.util;

import me.fzzyhmstrs.fzzy_config.util.AllowableStrings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;

public class RegistrySuggestions<T> {

	public static final RegistrySuggestions<Item> ITEMS = new RegistrySuggestions<>(Registries.ITEM, BuiltInRegistries.ITEM);
	public static final RegistrySuggestions<EntityType<?>> ENTITIES = new RegistrySuggestions<>(Registries.ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE);

	private final ResourceKey<? extends Registry<T>> registryKey;
	private final Registry<T> builtInRegistry;
	private volatile List<String> values = List.of();

	private RegistrySuggestions(ResourceKey<? extends Registry<T>> registryKey, Registry<T> builtInRegistry) {
		this.registryKey = registryKey;
		this.builtInRegistry = builtInRegistry;
	}

	public static void updateAll(LevelAccessor level) {
		ITEMS.update(level);
		ENTITIES.update(level);
	}

	private void update(LevelAccessor level) {
        //~ if <26.1 'lookupOrThrow' -> 'registryOrThrow'
        Registry<T> registry = level.registryAccess().lookupOrThrow(registryKey);
		List<String> newValues = new ArrayList<>(registry.keySet().stream().map(Identifier::toString).toList());
        //~ if <26.1 'listTagIds' -> 'getTagNames'
        registry.listTagIds().forEach(tag -> newValues.add("#" + tag.location()));
		values = List.copyOf(newValues);
	}

	public List<String> get() {
		return values;
	}

	public boolean isValid(String value) {
		if (value.startsWith("#")) return Identifier.tryParse(value.substring(1)) != null;
		Identifier id = Identifier.tryParse(value);
		return id != null && builtInRegistry.containsKey(id);
	}

	public AllowableStrings allowable() {
		return new AllowableStrings(this::isValid, this::get);
	}
}
