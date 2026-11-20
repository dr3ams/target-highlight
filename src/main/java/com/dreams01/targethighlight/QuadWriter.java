package com.dreams01.targethighlight;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Small helpers that emit flat coloured quads. Every quad is written twice with reversed winding
 * because RenderType.lightning() culls back faces. Render thread only.
 */
final class QuadWriter {
    private static final float[] QUAD = new float[12];

    private QuadWriter() {
    }

    static void set(int index, float x, float y, float z) {
        QUAD[index * 3] = x;
        QUAD[index * 3 + 1] = y;
        QUAD[index * 3 + 2] = z;
    }

    /** Emits the quad in QUAD[] front-facing and back-facing. */
    static void quad(VertexConsumer vc, Matrix4f m, int r, int g, int b, int a) {
        for (int i = 0; i < 4; i++) {
            vertex(vc, m, QUAD[i * 3], QUAD[i * 3 + 1], QUAD[i * 3 + 2], r, g, b, a);
        }
        for (int i = 3; i >= 0; i--) {
            vertex(vc, m, QUAD[i * 3], QUAD[i * 3 + 1], QUAD[i * 3 + 2], r, g, b, a);
        }
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, float x, float y, float z,
                               int r, int g, int b, int a) {
        vc.vertex(m, x, y, z).color(r, g, b, a).endVertex();
    }

    /** Flat band between two radii, from angle a0 to a1 (radians). */
    static void arcBand(RingContext c, double outer, double inner, double a0, double a1) {
        double span = a1 - a0;
        int steps = Math.max(1, (int) Math.ceil(Math.abs(span) / (Math.PI * 2.0D) * c.segments));
        double innerR = Math.max(0.02D, inner);

        for (int i = 0; i < steps; i++) {
            double s0 = a0 + span * i / steps;
            double s1 = a0 + span * (i + 1) / steps;
            float c0 = (float) Math.cos(s0);
            float n0 = (float) Math.sin(s0);
            float c1 = (float) Math.cos(s1);
            float n1 = (float) Math.sin(s1);

            set(0, (float) (c0 * outer), c.y, (float) (n0 * outer));
            set(1, (float) (c1 * outer), c.y, (float) (n1 * outer));
            set(2, (float) (c1 * innerR), c.y, (float) (n1 * innerR));
            set(3, (float) (c0 * innerR), c.y, (float) (n0 * innerR));
            quad(c.vc, c.m, c.r, c.g, c.b, c.a);
        }
    }

    /** Flat rectangular bar between two points on the ground plane. */
    static void bar(RingContext c, double x0, double z0, double x1, double z1, double halfWidth) {
        double dx = x1 - x0;
        double dz = z1 - z0;
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 1.0E-5D) {
            return;
        }
        double px = -dz / len * halfWidth;
        double pz = dx / len * halfWidth;

        set(0, (float) (x0 - px), c.y, (float) (z0 - pz));
        set(1, (float) (x1 - px), c.y, (float) (z1 - pz));
        set(2, (float) (x1 + px), c.y, (float) (z1 + pz));
        set(3, (float) (x0 + px), c.y, (float) (z0 + pz));
        quad(c.vc, c.m, c.r, c.g, c.b, c.a);
    }

    /** Small flat diamond on the ground plane. */
    static void flatDiamond(RingContext c, double cx, double cz, double half) {
        set(0, (float) (cx + half), c.y, (float) cz);
        set(1, (float) cx, c.y, (float) (cz + half));
        set(2, (float) (cx - half), c.y, (float) cz);
        set(3, (float) cx, c.y, (float) (cz - half));
        quad(c.vc, c.m, c.r, c.g, c.b, c.a);
    }

    /**
     * Diamond that always faces the camera, built from the camera's own up/left vectors.
     * tall = 1 gives a square diamond, bigger values stretch it vertically.
     */
    static void billboardDiamond(VertexConsumer vc, Matrix4f m, Camera camera,
                                 float cx, float cy, float cz, float size, float tall,
                                 int r, int g, int b, int a) {
        Vector3f left = camera.getLeftVector();
        Vector3f up = camera.getUpVector();
        float h = size * tall;

        set(0, cx + up.x() * h, cy + up.y() * h, cz + up.z() * h);               // top
        set(1, cx - left.x() * size, cy - left.y() * size, cz - left.z() * size); // right
        set(2, cx - up.x() * h, cy - up.y() * h, cz - up.z() * h);               // bottom
        set(3, cx + left.x() * size, cy + left.y() * size, cz + left.z() * size); // left
        quad(vc, m, r, g, b, a);
    }
}
