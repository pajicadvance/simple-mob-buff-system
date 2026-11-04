package me.pajic.smbs.util;

import me.fzzyhmstrs.fzzy_config.util.AllowableIdentifiers;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.pajic.smbs.SMBS;
import me.pajic.smbs.system.StatBoosts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Buffs {
    public static final Identifier HEALTH_BOOST = Identifier.withDefaultNamespace("health_boost");
    public static final Identifier REGENERATION = Identifier.withDefaultNamespace("regeneration");
    public static final Identifier RESISTANCE = Identifier.withDefaultNamespace("resistance");
    public static final Identifier STRENGTH = Identifier.withDefaultNamespace("strength");
    public static final Identifier SPEED = Identifier.withDefaultNamespace("speed");
    public static final Identifier INVISIBILITY = Identifier.withDefaultNamespace("invisibility");
    public static final Identifier WEAVING = Identifier.withDefaultNamespace("weaving");
    public static final Identifier INFESTED = Identifier.withDefaultNamespace("infested");
    public static final Identifier FARSIGHT = SMBS.id("follow_range");
    public static final Identifier KNOCKBACK_BOOST = SMBS.id("attack_knockback");
    public static final Identifier KNOCKBACK_RESISTANCE = SMBS.id("knockback_resist");
    public static final Identifier SIZE_BOOST = SMBS.id("scale");

    public static boolean isValid(Identifier id) {
        return StatBoosts.get(id).isPresent() || BuiltInRegistries.MOB_EFFECT.containsKey(id);
    }

    public static List<Identifier> all() {
        List<Identifier> ids = new ArrayList<>(BuiltInRegistries.MOB_EFFECT.keySet());
        ids.addAll(StatBoosts.ids());
        return ids;
    }

    public static ValidatedIdentifier validatedKey() {
        return new ValidatedIdentifier(HEALTH_BOOST, new AllowableIdentifiers(Buffs::isValid, Buffs::all));
    }
}
