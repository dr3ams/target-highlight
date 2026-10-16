# Changelog

## 0.2.0
- Colour tiers: normal, custom and boss enemies each get their own colour and opacity.
- Bosses are detected from the `forge:bosses` entity tag, plus an extra `bossIds` list in the config.
- `customIds` list for minibosses, elites or anything else you want in a separate colour.
- `blacklist` in the config: entities that are never highlighted. Supports `modid:*` for a whole mod.
- Entities in the boss/custom lists are targetable even when `hostileOnly` is on.
- Default lists: Ender Dragon and Wither as bosses; Elder Guardian and Warden as custom tier.
- Default `[color]` keys keep working: `hex` and `alpha` now mean the normal tier.

## 0.1.0
- First release.
- Highlights the enemy nearest the crosshair with a ground ring and a head marker.
- Sticky target selection, forgiving line-of-sight check, configurable cone, reach, colour and sizes.
- Toggle keybind (unbound by default) and a client config file.
