# Changelog

## 0.6.0
- Ring styles (`ring.style`): `SOLID` (default), `SEGMENTED_ARC`, `RUNIC`, `ROTATING_RUNES` and `MOTES`.
- `SEGMENTED_ARC`: curved arcs on two layers that slowly counter-rotate.
- `RUNIC`: a subtle pattern of glyph strokes between two thin circles.
- `ROTATING_RUNES`: enchanting-table (Standard Galactic) glyphs orbiting the ring.
- `MOTES`: no outline, just small glowing lights orbiting the footprint.
- `rotationSpeed` controls the animated styles. The styles keep a fixed default look, so `style` and `rotationSpeed` are the only ring-style options. All styles keep working with health tint, tiers, in-reach and draw-progress colours.
- Floating damage numbers when a mob is struck (`[damageNumbers]`, off by default). Damage is read from the mob's health, so it works without a server mod. Big hits are drawn larger in their own colour, and healing can optionally show too.

## 0.5.0
- Boss markers stay visible behind walls (`marker.throughWalls`: `OFF`, `BOSS` by default, or `ALL`).
- Health-tinted ring: green at full health, through yellow, to red when low (`ring.healthTint`, on by default).
- Optional text label above the marker with the target's name and health (`[label]`, off by default): name and health toggles, `VALUE` or `PERCENT` format, size, height and see-through options.
- Zoom while drawing a bow, charging a crossbow or winding up a trident (`[zoom]`, on by default, maximum factor 2.0), with an easing speed.

## 0.4.0
- Ranged mode: while you aim a bow, crossbow or trident (or a listed item), the highlight searches farther with a narrower cone.
- Activates while aiming (drawing, charging, or holding a loaded crossbow), with a short linger so it doesn't flicker. `trigger = HOLDING` makes it work whenever a ranged weapon is held.
- The ring and marker grow with distance so they stay readable at bow range (`scaleWithDistance`, `maxScale`).
- Draw progress: the ring fades up while you draw (`BRIGHTEN`) or switches colour at full draw (`COLOR`), controlled by `chargeBrightness`.
- `extraItems` lists extra items treated as ranged weapons, for example spell casters.
- Ranged items count as weapons for weapon-only mode.

## 0.3.0
- In-reach indicator: the ring brightens (or switches colour) while the target is inside your real weapon reach, so you can tell whether a hit will land. Modes: `OFF`, `BRIGHTEN`, `COLOR`.
- `rangeExtra` lets you fine-tune the reach check.
- Optional Better Combat support: when Better Combat is installed, the in-reach indicator uses the held weapon's own attack range (measured from shoulder height, like Better Combat does) instead of the vanilla reach. No dependency; `useBetterCombat` turns it off.
- Weapon-only mode: show the highlight only while the main hand holds a weapon (swords, plus optionally axes and ranged weapons). Extra items can be added by id. Off by default.

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
