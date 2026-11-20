package com.dreams01.targethighlight;

import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/** The different ring looks. Geometry styles draw into ctx.vc, the rune style draws text. */
final class RingStyles {
    /** Vanilla's "alt" font is the enchanting-table (Standard Galactic) alphabet. */
    private static final Style GALACTIC = Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt"));

    // Built-in look of each style (no longer configurable)
    private static final int ARC_COUNT = 3;
    private static final double ARC_COVERAGE = 0.6D;
    private static final int RUNE_COUNT = 16;
    private static final double RUNE_SIZE = 0.2D;
    private static final int MOTE_COUNT = 8;
    private static final double MOTE_SIZE = 0.05D;

    private RingStyles() {
    }

    /** All styles except ROTATING_RUNES, which needs text rendering. */
    static void drawGeometry(RingContext c, RingStyle style) {
        switch (style) {
            case SEGMENTED_ARC:
                segmentedArcs(c);
                break;
            case RUNIC:
                runic(c);
                break;
            case MOTES:
                motes(c);
                break;
            default:
                QuadWriter.arcBand(c, c.radius, c.radius - c.thickness, 0.0D, Math.PI * 2.0D);
                break;
        }
    }

    // ------------------------------------------------------------------ segmented arcs

    /** Outer arcs turn one way, inner arcs the other way. */
    private static void segmentedArcs(RingContext c) {
        int count = ARC_COUNT;
        double coverage = ARC_COVERAGE;
        double sector = Math.PI * 2.0D / count;
        double arcLength = sector * coverage;
        double band = Math.max(0.03D, c.thickness * 0.7D);
        double gap = band * 0.5D;

        for (int k = 0; k < count; k++) {
            double outerStart = k * sector + c.spin;
            QuadWriter.arcBand(c, c.radius, c.radius - band, outerStart, outerStart + arcLength);

            double innerStart = k * sector + sector * 0.5D - c.spin;
            QuadWriter.arcBand(c, c.radius - band - gap, c.radius - 2.0D * band - gap, innerStart, innerStart + arcLength);
        }
    }

    // ------------------------------------------------------------------ runic pattern

    /** Two thin circles with a subtle, fixed pattern of glyph strokes between them. */
    private static void runic(RingContext c) {
        int cells = 12;
        double thin = Math.max(0.02D, c.thickness * 0.5D);
        double bandWidth = 0.22D * Math.sqrt(c.scale);
        double innerRadius = Math.max(0.1D, c.radius - bandWidth);
        bandWidth = c.radius - innerRadius;

        QuadWriter.arcBand(c, c.radius, c.radius - thin, 0.0D, Math.PI * 2.0D);
        QuadWriter.arcBand(c, innerRadius + thin, innerRadius, 0.0D, Math.PI * 2.0D);

        double mid = (c.radius + innerRadius) * 0.5D;
        double h = Math.max(0.01D, (bandWidth - 2.0D * thin) * 0.5D * 0.8D);       // half radial extent of a glyph
        double q = Math.max(0.01D, Math.min(h * 0.7D, mid * Math.PI / cells * 0.4D)); // half tangential extent
        double w = Math.max(0.012D, c.thickness * 0.3D);

        for (int i = 0; i < cells; i++) {
            double theta = c.spin * 0.5D + i * Math.PI * 2.0D / cells;
            double cs = Math.cos(theta);
            double sn = Math.sin(theta);
            int bits = glyphBits(c.target.getId(), i);

            if ((bits & 1) != 0) {
                stroke(c, cs, sn, mid, 0.0D, -h, 0.0D, h, w);      // radial line
            }
            if ((bits & 2) != 0) {
                stroke(c, cs, sn, mid, -q, 0.0D, q, 0.0D, w);      // tangential line
            }
            if ((bits & 4) != 0) {
                stroke(c, cs, sn, mid, -q, -h, q, h, w);           // diagonal
            }
            if ((bits & 8) != 0) {
                stroke(c, cs, sn, mid, -q, h, q, -h, w);           // other diagonal
            }
            if ((bits & 16) != 0) {
                double px = (mid + h * 0.55D) * cs;
                double pz = (mid + h * 0.55D) * sn;
                QuadWriter.flatDiamond(c, px, pz, Math.max(0.025D, w * 1.6D));   // dot
            }
        }
    }

    /** A stroke given in glyph-local coordinates: u along the ring, v across it. */
    private static void stroke(RingContext c, double cs, double sn, double mid,
                               double u0, double v0, double u1, double v1, double w) {
        double x0 = (mid + v0) * cs - u0 * sn;
        double z0 = (mid + v0) * sn + u0 * cs;
        double x1 = (mid + v1) * cs - u1 * sn;
        double z1 = (mid + v1) * sn + u1 * cs;
        QuadWriter.bar(c, x0, z0, x1, z1, w);
    }

    /** Five bits choosing which strokes a glyph has; always at least two. */
    private static int glyphBits(int entityId, int cell) {
        int h = entityId * 73856093 ^ (cell + 1) * 19349663;
        h ^= h >>> 13;
        h *= 0x5BD1E995;
        h ^= h >>> 15;
        int bits = h & 0x1F;
        if (Integer.bitCount(bits) < 2) {
            bits |= 3;
        }
        return bits;
    }

    // ------------------------------------------------------------------ motes

    /** No outline at all: small glowing lights orbit the footprint. */
    private static void motes(RingContext c) {
        int count = MOTE_COUNT;
        double path = c.radius - c.thickness * 0.5D;
        float size = (float) (MOTE_SIZE * c.scale);
        int base = Math.min(255, c.a * 2);

        for (int i = 0; i < count; i++) {
            double theta = c.spin + i * Math.PI * 2.0D / count;
            float cx = (float) (Math.cos(theta) * path);
            float cz = (float) (Math.sin(theta) * path);
            float cy = c.y + 0.1F + (float) Math.sin(c.seconds * 2.5D + i * 1.7D) * 0.04F;

            double pulse = 0.65D + 0.35D * Math.sin(c.seconds * 3.0D + i);
            int alpha = Mth.clamp((int) (base * pulse), 1, 255);

            // faint halo behind a brighter core
            QuadWriter.billboardDiamond(c.vc, c.m, c.camera, cx, cy, cz, size * 2.2F, 1.0F, c.r, c.g, c.b, Math.max(1, alpha / 4));
            QuadWriter.billboardDiamond(c.vc, c.m, c.camera, cx, cy, cz, size, 1.0F, c.r, c.g, c.b, alpha);
        }
    }

    // ------------------------------------------------------------------ rotating runes (text)

    /**
     * Enchanting-table glyphs lying flat on the ring path and slowly orbiting. Each glyph is rotated so its
     * baseline follows the circle and its top points outward, and it faces upward.
     * Must be called after all geometry has been written, because text uses other render types.
     */
    static void drawRunes(RingContext c) {
        Font font = c.mc.font;
        int count = RUNE_COUNT;
        float s = (float) (RUNE_SIZE * c.scale / 8.0D);
        double path = c.radius - c.thickness * 0.5D;
        int color = (Math.max(1, c.a) << 24) | (c.r << 16) | (c.g << 8) | c.b;

        for (int i = 0; i < count; i++) {
            double theta = c.spin + i * Math.PI * 2.0D / count;
            double px = Math.cos(theta) * path;
            double pz = Math.sin(theta) * path;
            double py = c.y + 0.06D + Math.sin(c.seconds * 2.0D + i) * 0.02D;

            char letter = (char) ('a' + Math.floorMod(c.target.getId() * 31 + i * 7, 26));
            Component glyph = Component.literal(String.valueOf(letter)).withStyle(GALACTIC);

            c.stack.pushPose();
            c.stack.translate(px, py, pz);
            c.stack.mulPose(Axis.YP.rotation((float) (-theta - Math.PI / 2.0D)));
            c.stack.mulPose(Axis.XP.rotation((float) (Math.PI / 2.0D)));
            c.stack.scale(s, s, s);
            Matrix4f mat = c.stack.last().pose();

            float width = font.width(glyph);
            font.drawInBatch(glyph, -width / 2.0F, -4.0F, color, false, mat, c.buffers,
                    Font.DisplayMode.POLYGON_OFFSET, 0, LightTexture.FULL_BRIGHT);
            c.stack.popPose();
        }
    }
}
