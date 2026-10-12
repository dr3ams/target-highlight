package com.dreams01.targethighlight;

import net.minecraftforge.common.ForgeConfigSpec;

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

    // --- ground ring ---
    public static final ForgeConfigSpec.BooleanValue RING_ENABLED;
    public static final ForgeConfigSpec.DoubleValue RING_PADDING;
    public static final ForgeConfigSpec.DoubleValue RING_THICKNESS;
    public static final ForgeConfigSpec.IntValue RING_SEGMENTS;
    public static final ForgeConfigSpec.DoubleValue RING_Y_OFFSET;

    // --- head marker ---
    public static final ForgeConfigSpec.BooleanValue MARKER_ENABLED;
    public static final ForgeConfigSpec.DoubleValue MARKER_HEIGHT;
    public static final ForgeConfigSpec.DoubleValue MARKER_SIZE;

    // --- colour ---
    public static final ForgeConfigSpec.ConfigValue<String> COLOR_HEX;
    public static final ForgeConfigSpec.IntValue COLOR_ALPHA;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("general");
        ENABLED = b.comment("Master switch. The keybind toggles this.")
                .define("enabled", true);
        b.pop();

        b.push("acquisition");
        HOSTILE_ONLY = b.comment("true = only hostile mobs, false = any living entity except players and armor stands")
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
        b.pop();

        b.push("marker");
        MARKER_ENABLED = b.comment("Draw a small diamond above the target's head")
                .define("enabled", true);
        MARKER_HEIGHT = b.comment("Blocks above the target's head")
                .defineInRange("height", 0.45D, 0.0D, 4.0D);
        MARKER_SIZE = b.comment("Marker half-width in blocks")
                .defineInRange("size", 0.12D, 0.02D, 1.0D);
        b.pop();

        b.push("color");
        COLOR_HEX = b.comment("Ring and marker colour as RRGGBB hex")
                .define("hex", "FFD75A", o -> o instanceof String s && s.matches("[0-9a-fA-F]{6}"));
        COLOR_ALPHA = b.comment("Opacity 0-255")
                .defineInRange("alpha", 100, 0, 255);
        b.pop();

        SPEC = b.build();
    }

    private static String cachedHex = "";
    private static int cachedRgb = 0xFFD75A;

    /** Parsed colour as 0xRRGGBB, cached so we don't re-parse every frame. */
    public static int colorRgb() {
        String hex = COLOR_HEX.get();
        if (!hex.equals(cachedHex)) {
            try {
                cachedRgb = Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                cachedRgb = 0xFFD75A;
            }
            cachedHex = hex;
        }
        return cachedRgb;
    }

    private THConfig() {
    }
}
