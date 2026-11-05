# Simple Mob Buff System

This mod gives a chance for mobs to spawn with buffs and makes them drop additional rewards based on how strong they are.

While similar in concept to mods like [Champions](https://modrinth.com/mod/champions) and [Eldritch Mobs](https://modrinth.com/mod/eldritch-mobs), it differs significantly in execution. Just as the mod name implies, it tries to be as simple and vanilla-friendly as possible while maintaining game balance, configurability and mod compatibility. This is achieved by building the system entirely using mechanics which already exist in the game.

**The mod comes with a balanced configuration out of the box**. All spawns are based on the game difficulty and the current local difficulty, all the buffs the mobs receive are status effects you know from vanilla, and there's no need to worry about getting mauled by ridiculously overpowered mobs or getting showered with overly generous loot - you can install and play without ever touching or even looking at the configuration.

Buffed mobs will emit particle effects based on the buffs they have. This is the only way you can tell if a mob is buffed or not without other mods. I recommend [Jade](https://modrinth.com/mod/jade) for getting information on mob health and the buffs it has and [Particle Effects](https://modrinth.com/mod/particle-effects) for unique particle textures for each effect.

The mod configuration is extensive and allows you to do pretty much whatever you want. Apply any effect to any mob, modded or vanilla. Want invisible creepers? Horses that move at the speed of light? Polar bears the size of a mountain? _Not that I recommend any of those_, but you sure can do it with this mod!

With that being said, if you want to know how to configure the mod and how it works, read on.

## Editing the configuration

The mod uses [Fzzy Config](https://modrinth.com/mod/fzzy-config) to provide a relatively simple to use and cohesive in-game configuration. You'll also need [Mod Menu](https://modrinth.com/mod/modmenu) to actually access the configuration screen. Every option has a description and there are pointers and instructions on how to do stuff. There are also suggestions and auto-completion for fields where status effects, mobs or items are inputted.

## How it works

Buffs are assigned to mobs through rules. Each rule lists the mobs it applies to (entity IDs or entity tags), an optional weapon the mob needs to spawn with, and the buff set it applies. A buff set is a named group of buffs, each with a max level, and the mob can get any level of a buff from 1 to max. A rule can also have a separate buff set which is used instead when the mob spawns with a ranged weapon. By default, mobs spawned via spawners are prevented from being buffed due to balance concerns (you can change which spawn sources are allowed in the config).

When a mob is killed, it drops additional rewards based on its "buff level". The buff level is determined based on how strong the mob is - the amount of buffs it has and their levels, and the amount of enchanted equipment it's wearing. Zombie-type mobs will have a higher buff level if they're leaders.

The additional rewards are the following:
- Additional XP orbs based on how high the buff level is, each orb granting 3 XP points by default.
- If the buff level surpasses a percentage of the maximum possible buff level for the mob (default 66%), an enchanted book with a random quality depending on how much the buff level surpassed the threshold.

By default, most mobs receive buffs from either the melee buff set or the ranged buff set depending on the type of weapon they spawn with, whereas certain mobs receive buffs from buff sets unique to them. All buff sets and rules can be modified in the mod configuration.

Any status effect, mob and item can be used in buff sets and rules, including ones added by other mods.

Buff application works the following way:
- When a mob spawns, each rule matching the mob first determines if it's going to try to apply its buffs in the first place. The chance for this is set per rule, and ranges from a minimum chance (default 10%) at local difficulty 2 and below to a maximum chance (default 25%) at the highest possible local difficulty (6.75).
- If this check passes, the rule tries to apply each buff from its buff set one by one. The chance for each buff to be applied is also set per rule, and ranges from a minimum chance (default 20%) at local difficulty 2 and below to a maximum chance (default 75%) at the highest possible local difficulty (6.75).
- If several rules give the mob the same buff, the highest level is kept.
- The level of each buff is determined by the following formula: `difficultyMultiplier * randomNumber * maxLevel ^ 2`, limited from 1 to `maxLevel`, where
    - `difficultyMultiplier` is a number from 0 to 1 based on the current local difficulty (0 at local difficulty 2 and below, 1 at local difficulty 4 and above)
    - `randomNumber` is a randomly generated number from 0 to 1
    - `maxLevel` is the maximum level of the buff

The local difficulty ranges and the exponent in the level formula can be changed in the config.
