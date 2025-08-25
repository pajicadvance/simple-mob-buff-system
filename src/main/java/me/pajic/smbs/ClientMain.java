package me.pajic.smbs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientMain implements ClientModInitializer {
    public static final List<String> itemSuggestions = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            itemSuggestions.clear();
            itemSuggestions.addAll(BuiltInRegistries.ITEM.keySet().stream().map(ResourceLocation::toString).toList());
            BuiltInRegistries.ITEM./*? if > 1.21.1 {*//*listTagIds()*//*?}*//*? if 1.21.1 {*/getTagNames()/*?}*/
                    .forEach(tag -> itemSuggestions.add("#" + tag.location()));
        });
    }
}
