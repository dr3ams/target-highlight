# target-highlight
<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/2601b091-5c1a-4d86-827c-ebcda8023832" />

A small client-side Forge mod for Minecraft 1.20.1 that highlights the enemy nearest your crosshair with a configurable ground ring and a marker above its head.

# Features

- Picks the enemy nearest the crosshair inside a configurable cone, within your reach plus a bonus
- Sticky target: a score advantage keeps the highlight from flickering between nearby enemies
- Forgiving line-of-sight check (feet, body and head), so fences and thin cover don't hide targets
- Ground ring and head marker, both optional, with configurable size, colour and opacity
- Colour tiers: normal, custom and boss enemies each get their own colour and opacity
- Blacklist in the config for entities that should never be highlighted
- In-reach indicator: the ring brightens or changes colour when the target is inside your real weapon reach
- Optional weapon-only mode: the highlight appears only while you hold a weapon
- Ranged mode: while you aim a bow, crossbow or trident, it searches farther with a narrower cone, scales with distance, and the ring follows your draw
- Boss markers stay visible behind walls
- Five ring styles for different themes: solid, segmented arcs, runic pattern, rotating enchanting-table runes, orbiting motes
- Optional floating damage numbers (off by default)
- Extras: health-tinted ring and zoom while drawing (on by default), optional name and health label (off by default)
- Toggle keybind (unbound by default, set it under Controls > Target Highlight)
- Client-side only: not needed on the server
- Optional Better Combat support: if installed, the in-reach indicator uses each weapon's own attack range

It highlights only. It never moves your camera, changes your attacks, or alters what you hit.

## Configuration

`config/targethighlight-client.toml` is created on first launch. Every value has a comment in the file.

| Section | Option | Default |
|---|---|---|
| general | `enabled` | `true` |
| acquisition | `hostileOnly` | `true` |
| acquisition | `reachBonus` (blocks added to your reach) | `1.0` |
| acquisition | `coneHalfAngle` (degrees) | `35.0` |
| acquisition | `acquireIntervalTicks` | `2` |
| acquisition | `loseGraceTicks` | `10` |
| acquisition | `stickyBonus` | `8.0` |
| acquisition | `distanceWeight` | `0.5` |
| acquisition | `requireLineOfSight` | `true` |
| acquisition | `blacklist` (entity ids or `modid:*`) | empty |
| tiers | `bossFromTag` (use the `forge:bosses` tag) | `true` |
| tiers | `bossIds` (entity ids or `modid:*`) | `minecraft:ender_dragon`, `minecraft:wither` |
| tiers | `customIds` (entity ids or `modid:*`) | `minecraft:elder_guardian`, `minecraft:warden` |
| inReach | `mode` (`OFF`, `BRIGHTEN`, `COLOR`) | `BRIGHTEN` |
| inReach | `alphaBoost`, `hex`, `rangeExtra` | `90`, `7CFF7C`, `0.0` |
| inReach | `useBetterCombat` | `true` |
| weaponOnly | `enabled` | `false` |
| weaponOnly | `includeRanged`, `includeAxes` | `true`, `true` |
| weaponOnly | `extraItems` (item ids or `modid:*`) | empty |
| ranged | `enabled` | `true` |
| ranged | `trigger` (`AIMING`, `HOLDING`) | `AIMING` |
| ranged | `lingerTicks` | `10` |
| ranged | `range`, `coneHalfAngle` | `40.0`, `7.0` |
| ranged | `scaleWithDistance`, `maxScale` | `true`, `4.0` |
| ranged | `chargeBrightness` | `true` |
| ranged | `extraItems` (item ids or `modid:*`) | empty |
| ring | `healthTint` | `true` |
| ring | `style` (`SOLID`, `SEGMENTED_ARC`, `RUNIC`, `ROTATING_RUNES`, `MOTES`) | `SOLID` |
| ring | `rotationSpeed` (degrees per second, 0 = static) | `20.0` |
| marker | `throughWalls` (`OFF`, `BOSS`, `ALL`) | `BOSS` |
| label | `enabled` | `false` |
| label | `showName`, `showHealth` | `true`, `true` |
| label | `healthFormat` (`VALUE`, `PERCENT`), `scale`, `height`, `seeThrough` | `VALUE`, `1.0`, `0.15`, `false` |
| zoom | `enabled` | `true` |
| zoom | `maxZoom`, `speed` | `2.0`, `0.3` |
| damageNumbers | `enabled`, `onlyTarget`, `showHealing` | `false`, `false`, `false` |
| damageNumbers | `minDamage`, `range`, `durationTicks`, `scale` | `0.5`, `32.0`, `24`, `1.0` |
| damageNumbers | `hex`, `bigHitPercent`, `bigHex` | `FF6B5A`, `20`, `FFD040` |
| ring | `enabled`, `padding`, `thickness`, `segments`, `yOffset` | `true`, `0.25`, `0.08`, `40`, `0.03` |
| marker | `enabled`, `height`, `size` | `true`, `0.45`, `0.12` |
| color | `hex`, `alpha` (normal enemies) | `FFD75A`, `100` |
| color | `customHex`, `customAlpha` | `5AC8FF`, `100` |
| color | `bossHex`, `bossAlpha` | `FF5050`, `140` |

# Compatibility
- Minecraft 1.20.1, Forge 47.x
- No dependencies
- Client-side only: you don't need it on the server
- Works with modpacks. If you see a conflict or odd rendering (for example with a shader pack), please report it.
  
# Feedback

This is an early release and feedback is very welcome. Please report problems with your logs/latest.log (or crash report), your Forge version, and whether you use shaders.

Inspired by the target-assist idea in the [Visceral Combat](https://www.curseforge.com/minecraft/mc-mods/visceral-combat) mod.
