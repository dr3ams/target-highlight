package com.dreams01.targethighlight;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/** All tunable values. File: config/targethighlight-client.toml */
public final class THConfig {
    public static final ForgeConfigSpec SPEC;

    // --- general ---
    public static final ForgeConfigSpec.BooleanValue ENABLED;

    // --- acquisition ---
    public static final ForgeConfigSpec.BooleanValue HOSTILE_ONLY;
    public static final ForgeConfigSpec.DoubleValue REACH_BONUS;
    public static final ForgeConfigSpec.DoubleValue CONE_HALF_ANGLE;
    public static final ForgeConfigSpec.IntValue ACQUIRE_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue LOSE_GRACE_TICKS;
    public static final ForgeConfigSpec.DoubleValue STICKY_BONUS;
    public static final ForgeConfigSpec.DoubleValue DISTANCE_WEIGHT;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_LINE_OF_SIGHT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    // --- tiers ---
    public static final ForgeConfigSpec.BooleanValue BOSS_FROM_TAG;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BOSS_IDS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CUSTOM_IDS;

    // --- in-reach indicator ---
    public static final ForgeConfigSpec.EnumValue<ReachMode> IN_REACH_MODE;
    public static final ForgeConfigSpec.IntValue IN_REACH_ALPHA_BOOST;
    public static final ForgeConfigSpec.ConfigValue<String> IN_REACH_HEX;
    public static final ForgeConfigSpec.DoubleValue IN_REACH_EXTRA;
    public static final ForgeConfigSpec.BooleanValue IN_REACH_USE_BETTER_COMBAT;

    // --- weapon-only mode ---
    public static final ForgeConfigSpec.BooleanValue WEAPON_ONLY;
    public static final ForgeConfigSpec.BooleanValue WEAPON_INCLUDE_RANGED;
    public static final ForgeConfigSpec.BooleanValue WEAPON_INCLUDE_AXES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WEAPON_EXTRA_ITEMS;

    // --- ranged mode ---
    public static final ForgeConfigSpec.BooleanValue RANGED_ENABLED;
    public static final ForgeConfigSpec.EnumValue<RangedTrigger> RANGED_TRIGGER;
    public static final ForgeConfigSpec.IntValue RANGED_LINGER_TICKS;
    public static final ForgeConfigSpec.DoubleValue RANGED_RANGE;
    public static final ForgeConfigSpec.DoubleValue RANGED_CONE_HALF_ANGLE;
    public static final ForgeConfigSpec.BooleanValue RANGED_SCALE_WITH_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue RANGED_MAX_SCALE;
    public static final ForgeConfigSpec.BooleanValue RANGED_CHARGE_BRIGHTNESS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RANGED_EXTRA_ITEMS;

    // --- label ---
    public static final ForgeConfigSpec.BooleanValue LABEL_ENABLED;
    public static final ForgeConfigSpec.BooleanValue LABEL_SHOW_NAME;
    public static final ForgeConfigSpec.BooleanValue LABEL_SHOW_HEALTH;
    public static final ForgeConfigSpec.EnumValue<LabelHealthFormat> LABEL_HEALTH_FORMAT;
    public static final ForgeConfigSpec.DoubleValue LABEL_SCALE;
    public static final ForgeConfigSpec.DoubleValue LABEL_HEIGHT;
    public static final ForgeConfigSpec.BooleanValue LABEL_SEE_THROUGH;

    // --- zoom ---
    public static final ForgeConfigSpec.BooleanValue ZOOM_ENABLED;
    public static final ForgeConfigSpec.DoubleValue ZOOM_MAX;
    public static final ForgeConfigSpec.DoubleValue ZOOM_SPEED;

    // --- floating damage numbers ---
    public static final ForgeConfigSpec.BooleanValue DAMAGE_ENABLED;
    public static final ForgeConfigSpec.BooleanValue DAMAGE_ONLY_TARGET;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_MIN;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_RANGE;
    public static final ForgeConfigSpec.IntValue DAMAGE_DURATION;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_SCALE;
    public static final ForgeConfigSpec.ConfigValue<String> DAMAGE_HEX;
    public static final ForgeConfigSpec.IntValue DAMAGE_BIG_PERCENT;
    public static final ForgeConfigSpec.ConfigValue<String> DAMAGE_BIG_HEX;
    public static final ForgeConfigSpec.BooleanValue DAMAGE_SHOW_HEALING;

    // --- ground ring ---
    public static final ForgeConfigSpec.BooleanValue RING_ENABLED;
    public static final ForgeConfigSpec.DoubleValue RING_PADDING;
    public static final ForgeConfigSpec.DoubleValue RING_THICKNESS;
    public static final ForgeConfigSpec.IntValue RING_SEGMENTS;
    public static final ForgeConfigSpec.DoubleValue RING_Y_OFFSET;
    public static final ForgeConfigSpec.BooleanValue RING_HEALTH_TINT;
    public static final ForgeConfigSpec.EnumValue<RingStyle> RING_STYLE;
    public static final ForgeConfigSpec.DoubleValue RING_ROTATION_SPEED;

    // --- head marker ---
    public static final ForgeConfigSpec.BooleanValue MARKER_ENABLED;
    public static final ForgeConfigSpec.DoubleValue MARKER_HEIGHT;
    public static final ForgeConfigSpec.DoubleValue MARKER_SIZE;
    public static final ForgeConfigSpec.EnumValue<ThroughWalls> MARKER_THROUGH_WALLS;

    // --- colours (normal tier keeps the original key names) ---
    public static final ForgeConfigSpec.ConfigValue<String> COLOR_HEX;
    public static final ForgeConfigSpec.IntValue COLOR_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<String> CUSTOM_COLOR_HEX;
    public static final ForgeConfigSpec.IntValue CUSTOM_COLOR_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<String> BOSS_COLOR_HEX;
    public static final ForgeConfigSpec.IntValue BOSS_COLOR_ALPHA;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("general");
        ENABLED = b.comment("Master switch. The keybind toggles this.")
                .define("enabled", true);
        b.pop();

        b.push("acquisition");
        HOSTILE_ONLY = b.comment("true = only hostile mobs, false = any living entity except players and armor stands. "
                        + "Entities listed under [tiers] (and bosses from the boss tag) are targetable either way.")
                .define("hostileOnly", true);
        REACH_BONUS = b.comment("Blocks added on top of the player's entity reach")
                .defineInRange("reachBonus", 1.0D, 0.0D, 16.0D);
        CONE_HALF_ANGLE = b.comment("Half-angle of the acquisition cone around the crosshair, in degrees")
                .defineInRange("coneHalfAngle", 35.0D, 1.0D, 90.0D);
        ACQUIRE_INTERVAL_TICKS = b.comment("Re-scan every N ticks (2 = every 0.1 s)")
                .defineInRange("acquireIntervalTicks", 2, 1, 20);
        LOSE_GRACE_TICKS = b.comment("Ticks to keep a target after it leaves the cone (10 = 0.5 s)")
                .defineInRange("loseGraceTicks", 10, 0, 100);
        STICKY_BONUS = b.comment("Score advantage of the current target, in degree-equivalents. Higher = less flicker between nearby enemies")
                .defineInRange("stickyBonus", 8.0D, 0.0D, 90.0D);
        DISTANCE_WEIGHT = b.comment("Score added per block of distance (lower score wins)")
                .defineInRange("distanceWeight", 0.5D, 0.0D, 10.0D);
        REQUIRE_LINE_OF_SIGHT = b.comment("Ignore enemies hidden behind blocks (checks head, body and feet, so fences and thin cover don't block it)")
                .define("requireLineOfSight", true);
        BLACKLIST = b.comment("Entities that are never highlighted. Entity ids, or \"modid:*\" for every entity of a mod.",
                        "Example: [\"minecraft:villager\", \"guardvillagers:*\"]")
                .<String>defineListAllowEmpty("blacklist", List.of(), THConfig::isString);
        b.pop();

        b.push("tiers");
        BOSS_FROM_TAG = b.comment("Treat entities in the forge:bosses entity tag as bosses (many mods tag their bosses)")
                .define("bossFromTag", true);
        BOSS_IDS = b.comment("Extra entity ids to treat as bosses (boss colour). Entity ids, or \"modid:*\".",
                        "Example: [\"cataclysm:ignis\", \"mowziesmobs:*\"]")
                .<String>defineListAllowEmpty("bossIds", List.of("minecraft:ender_dragon", "minecraft:wither"), THConfig::isString);
        CUSTOM_IDS = b.comment("Entity ids that get the custom colour (e.g. minibosses or elites). Entity ids, or \"modid:*\".",
                        "A boss entry wins over a custom entry.")
                .<String>defineListAllowEmpty("customIds", List.of("minecraft:elder_guardian", "minecraft:warden"), THConfig::isString);
        b.pop();

        b.push("ring");
        RING_ENABLED = b.comment("Draw a ring on the ground under the target")
                .define("enabled", true);
        RING_PADDING = b.comment("Blocks added to the target's half-width to get the ring radius")
                .defineInRange("padding", 0.25D, 0.0D, 4.0D);
        RING_THICKNESS = b.comment("Ring thickness in blocks")
                .defineInRange("thickness", 0.08D, 0.01D, 1.0D);
        RING_SEGMENTS = b.comment("Number of segments (more = rounder)")
                .defineInRange("segments", 40, 8, 128);
        RING_Y_OFFSET = b.comment("Blocks above the target's feet")
                .defineInRange("yOffset", 0.03D, 0.0D, 1.0D);
        RING_HEALTH_TINT = b.comment("Tint the ring from green (full health) through yellow to red (low health) instead of using the tier colour.",
                        "While the target is in reach, inReach mode COLOR still overrides this colour.")
                .define("healthTint", true);
        RING_STYLE = b.comment("Ring look:",
                        "SOLID = plain ring, SEGMENTED_ARC = counter-rotating arcs, RUNIC = subtle glyph pattern,",
                        "ROTATING_RUNES = orbiting enchanting-table glyphs, MOTES = orbiting lights (no outline)")
                .defineEnum("style", RingStyle.SOLID);
        RING_ROTATION_SPEED = b.comment("Rotation speed of the animated styles in degrees per second (0 = static)")
                .defineInRange("rotationSpeed", 20.0D, 0.0D, 360.0D);
        b.pop();

        b.push("marker");
        MARKER_ENABLED = b.comment("Draw a small diamond above the target's head")
                .define("enabled", true);
        MARKER_HEIGHT = b.comment("Blocks above the target's head")
                .defineInRange("height", 0.45D, 0.0D, 4.0D);
        MARKER_SIZE = b.comment("Marker half-width in blocks")
                .defineInRange("size", 0.12D, 0.02D, 1.0D);
        MARKER_THROUGH_WALLS = b.comment("Which markers stay visible behind obstacles: OFF, BOSS (bosses only) or ALL")
                .defineEnum("throughWalls", ThroughWalls.BOSS);
        b.pop();

        b.push("color");
        COLOR_HEX = b.comment("Colour of normal enemies (ring and marker) as RRGGBB hex")
                .define("hex", "FFD75A", THConfig::isHex);
        COLOR_ALPHA = b.comment("Opacity of normal enemies, 0-255")
                .defineInRange("alpha", 100, 0, 255);
        CUSTOM_COLOR_HEX = b.comment("Colour of enemies listed under customIds, RRGGBB hex")
                .define("customHex", "5AC8FF", THConfig::isHex);
        CUSTOM_COLOR_ALPHA = b.comment("Opacity of custom-tier enemies, 0-255")
                .defineInRange("customAlpha", 100, 0, 255);
        BOSS_COLOR_HEX = b.comment("Colour of bosses, RRGGBB hex")
                .define("bossHex", "FF5050", THConfig::isHex);
        BOSS_COLOR_ALPHA = b.comment("Opacity of bosses, 0-255")
                .defineInRange("bossAlpha", 140, 0, 255);
        b.pop();

        b.push("inReach");
        IN_REACH_MODE = b.comment("How the ring shows that the target is inside your real weapon reach (not just the assist range).",
                        "OFF = no change, BRIGHTEN = the ring gets more opaque, COLOR = the ring uses the colour below.")
                .defineEnum("mode", ReachMode.BRIGHTEN);
        IN_REACH_ALPHA_BOOST = b.comment("BRIGHTEN: opacity added to the ring while in reach (0-255, result capped at 255)")
                .defineInRange("alphaBoost", 90, 0, 255);
        IN_REACH_HEX = b.comment("COLOR: ring colour while in reach, RRGGBB hex")
                .define("hex", "7CFF7C", THConfig::isHex);
        IN_REACH_EXTRA = b.comment("Blocks added to the reach used for this check (vanilla reach, or the Better Combat attack range). Negative values allowed.")
                .defineInRange("rangeExtra", 0.0D, -4.0D, 4.0D);
        IN_REACH_USE_BETTER_COMBAT = b.comment("If Better Combat is installed, use the held weapon's own attack range instead of the vanilla entity reach, "
                        + "and measure from shoulder height like Better Combat does. Has no effect when Better Combat is missing.")
                .define("useBetterCombat", true);
        b.pop();

        b.push("weaponOnly");
        WEAPON_ONLY = b.comment("Only show the highlight while the main hand holds a weapon, so it stays quiet when mining or building")
                .define("enabled", false);
        WEAPON_INCLUDE_RANGED = b.comment("Count bows, crossbows and tridents as weapons")
                .define("includeRanged", true);
        WEAPON_INCLUDE_AXES = b.comment("Count axes as weapons")
                .define("includeAxes", true);
        WEAPON_EXTRA_ITEMS = b.comment("Extra item ids that count as weapons (modded weapons are not always recognised). Item ids, or \"modid:*\".",
                        "Example: [\"simplyswords:*\", \"ars_nouveau:novice_spell_book\"]")
                .<String>defineListAllowEmpty("extraItems", List.of(), THConfig::isString);
        b.pop();

        b.push("ranged");
        RANGED_ENABLED = b.comment("Ranged mode: while aiming a bow, crossbow, trident or listed item, use a longer range and a narrower cone, and show the draw progress on the ring")
                .define("enabled", true);
        RANGED_TRIGGER = b.comment("AIMING = only while drawing, charging, or holding a loaded crossbow. HOLDING = whenever a ranged weapon is held.")
                .defineEnum("trigger", RangedTrigger.AIMING);
        RANGED_LINGER_TICKS = b.comment("Ticks to keep ranged mode after you stop aiming, so the highlight doesn't flicker (10 = 0.5 s)")
                .defineInRange("lingerTicks", 10, 0, 100);
        RANGED_RANGE = b.comment("Search range in blocks while in ranged mode")
                .defineInRange("range", 40.0D, 5.0D, 128.0D);
        RANGED_CONE_HALF_ANGLE = b.comment("Half-angle of the acquisition cone in ranged mode, in degrees. Narrower than melee because small aim errors matter at distance")
                .defineInRange("coneHalfAngle", 7.0D, 1.0D, 45.0D);
        RANGED_SCALE_WITH_DISTANCE = b.comment("Enlarge the ring and marker with distance in ranged mode so they stay readable")
                .define("scaleWithDistance", true);
        RANGED_MAX_SCALE = b.comment("Largest size multiplier from distance scaling (reached at about 6 blocks times this value)")
                .defineInRange("maxScale", 4.0D, 1.0D, 8.0D);
        RANGED_CHARGE_BRIGHTNESS = b.comment("The ring follows the draw progress: BRIGHTEN mode fades it up while drawing, COLOR mode switches colour at full draw (see [inReach])")
                .define("chargeBrightness", true);
        RANGED_EXTRA_ITEMS = b.comment("Extra item ids treated as ranged weapons, such as spell casters or modded launchers. Item ids, or \"modid:*\". They count as aiming while the item is in use.",
                        "Example: [\"ars_nouveau:novice_spell_book\", \"somemod:*\"]")
                .<String>defineListAllowEmpty("extraItems", List.of(), THConfig::isString);
        b.pop();

        b.push("label");
        LABEL_ENABLED = b.comment("Show a small text label above the marker with the target's name and health")
                .define("enabled", false);
        LABEL_SHOW_NAME = b.comment("Include the target's name")
                .define("showName", true);
        LABEL_SHOW_HEALTH = b.comment("Include the target's health")
                .define("showHealth", true);
        LABEL_HEALTH_FORMAT = b.comment("VALUE = 14/20, PERCENT = 70%")
                .defineEnum("healthFormat", LabelHealthFormat.VALUE);
        LABEL_SCALE = b.comment("Text size multiplier (it also grows with distance in ranged mode)")
                .defineInRange("scale", 1.0D, 0.25D, 4.0D);
        LABEL_HEIGHT = b.comment("Blocks between the top of the marker (or the head, if the marker is off) and the label")
                .defineInRange("height", 0.15D, 0.0D, 3.0D);
        LABEL_SEE_THROUGH = b.comment("Draw the label through walls")
                .define("seeThrough", false);
        b.pop();

        b.push("zoom");
        ZOOM_ENABLED = b.comment("Zoom the view while drawing a bow, charging a crossbow or winding up a trident")
                .define("enabled", true);
        ZOOM_MAX = b.comment("Zoom factor at full draw (1.5 = field of view divided by 1.5). Stacks with vanilla's small bow zoom. "
                        + "Mouse sensitivity is not changed, so high values make aiming touchy.")
                .defineInRange("maxZoom", 2.0D, 1.0D, 4.0D);
        ZOOM_SPEED = b.comment("How fast the zoom follows the draw, as a fraction of the remaining distance per tick (higher = snappier)")
                .defineInRange("speed", 0.3D, 0.05D, 1.0D);
        b.pop();

        b.push("damageNumbers");
        DAMAGE_ENABLED = b.comment("Show floating damage numbers when a mob is struck. Damage is read from the mob's health, so it works without any server mod.")
                .define("enabled", false);
        DAMAGE_ONLY_TARGET = b.comment("Only show numbers for the currently highlighted target")
                .define("onlyTarget", false);
        DAMAGE_MIN = b.comment("Smallest health change that gets a number")
                .defineInRange("minDamage", 0.5D, 0.1D, 100.0D);
        DAMAGE_RANGE = b.comment("Only watch mobs within this many blocks of you")
                .defineInRange("range", 32.0D, 4.0D, 96.0D);
        DAMAGE_DURATION = b.comment("How long a number stays, in ticks (24 = 1.2 s)")
                .defineInRange("durationTicks", 24, 10, 80);
        DAMAGE_SCALE = b.comment("Text size multiplier")
                .defineInRange("scale", 1.0D, 0.25D, 4.0D);
        DAMAGE_HEX = b.comment("Colour of normal hits, RRGGBB hex")
                .define("hex", "FF6B5A", THConfig::isHex);
        DAMAGE_BIG_PERCENT = b.comment("A hit of at least this percentage of the mob's maximum health counts as big and is drawn larger in the big-hit colour (0 = off)")
                .defineInRange("bigHitPercent", 20, 0, 100);
        DAMAGE_BIG_HEX = b.comment("Colour of big hits, RRGGBB hex")
                .define("bigHex", "FFD040", THConfig::isHex);
        DAMAGE_SHOW_HEALING = b.comment("Also show green numbers when a mob regains health")
                .define("showHealing", false);
        b.pop();

        SPEC = b.build();
    }

    private static boolean isString(Object o) {
        return o instanceof String;
    }

    private static boolean isHex(Object o) {
        return o instanceof String && ((String) o).matches("[0-9a-fA-F]{6}");
    }

    // index = Tier.ordinal(): NORMAL, CUSTOM, BOSS
    private static final int[] FALLBACK_RGB = {0xFFD75A, 0x5AC8FF, 0xFF5050};
    private static final String[] cachedHex = {"", "", ""};
    private static final int[] cachedRgb = {0xFFD75A, 0x5AC8FF, 0xFF5050};

    /** Colour of a tier as 0xRRGGBB, cached so we don't re-parse every frame. */
    public static int colorRgb(Tier tier) {
        int i = tier.ordinal();
        String hex;
        if (tier == Tier.BOSS) {
            hex = BOSS_COLOR_HEX.get();
        } else if (tier == Tier.CUSTOM) {
            hex = CUSTOM_COLOR_HEX.get();
        } else {
            hex = COLOR_HEX.get();
        }

        if (!hex.equals(cachedHex[i])) {
            try {
                cachedRgb[i] = Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                cachedRgb[i] = FALLBACK_RGB[i];
            }
            cachedHex[i] = hex;
        }
        return cachedRgb[i];
    }

    /** Opacity of a tier, 0-255. */
    public static int colorAlpha(Tier tier) {
        if (tier == Tier.BOSS) {
            return BOSS_COLOR_ALPHA.get();
        }
        if (tier == Tier.CUSTOM) {
            return CUSTOM_COLOR_ALPHA.get();
        }
        return COLOR_ALPHA.get();
    }

    private static String cachedReachHex = "";
    private static int cachedReachRgb = 0x7CFF7C;

    /** In-reach ring colour as 0xRRGGBB (COLOR mode), cached. */
    public static int inReachRgb() {
        String hex = IN_REACH_HEX.get();
        if (!hex.equals(cachedReachHex)) {
            try {
                cachedReachRgb = Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                cachedReachRgb = 0x7CFF7C;
            }
            cachedReachHex = hex;
        }
        return cachedReachRgb;
    }

    /** Parses an RRGGBB hex string, or returns the fallback if it is not valid. */
    public static int hexToRgb(String hex, int fallback) {
        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private THConfig() {
    }
}
