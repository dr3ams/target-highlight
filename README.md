# target-highlight
<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/2601b091-5c1a-4d86-827c-ebcda8023832" />

A small client-side Forge mod for Minecraft 1.20.1 that highlights the enemy nearest your crosshair with a soft ground ring and a marker above its head.

# What it does
- Picks the enemy closest to your crosshair within a cone in front of you, up to your weapon reach (plus a configurable bonus)
- Keeps the current target "sticky", so the highlight doesn't flicker when two enemies are close together
- Ignores enemies hidden behind solid cover, while fences and other thin obstacles don't hide them
- Draws a ring on the ground under the target and a diamond marker above its head
- Drops the highlight as soon as the target dies

It highlights only. It never moves your camera, changes your attacks, or alters what you hit.

# Fully configurable

Everything lives in config/targethighlight-client.toml):

- Targeting: hostile-only or any creature, reach bonus, cone angle, how sticky the target is, how long it lingers, scan rate, line-of-sight on/off
- Ring: on/off, size padding, thickness, smoothness, height above the ground
- Marker: on/off, height above the head, size
- Look: colour (hex) and opacity

Prefer a subtle highlight? Lower the opacity or turn off the marker. Want it bolder? Raise the opacity or change the colour.

# Keybind

A "Toggle Target Highlight" key is available under Controls > Target Highlight. It is unbound by default so it won't clash with your other mods.

# Compatibility
- Minecraft 1.20.1, Forge 47.x
- No dependencies
- Client-side only: you don't need it on the server
- Works with modpacks. If you see a conflict or odd rendering (for example with a shader pack), please report it.
  
# Feedback

This is an early release and feedback is very welcome. Please report problems with your logs/latest.log (or crash report), your Forge version, and whether you use shaders.

Inspired by the target-assist idea in the [Visceral Combat](https://www.curseforge.com/minecraft/mc-mods/visceral-combat) mod.
