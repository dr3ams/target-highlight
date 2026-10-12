package com.dreams01.targethighlight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Draws the highlight for the current target: a flat ring under its feet and a small
 * diamond above its head that always faces the camera.
 *
 * Uses RenderType.lightning() (untextured, additive-ish, position+colour). Every quad is
 * emitted twice with reversed winding because that render type culls back faces.
 * If you'd rather have normal alpha blending, try RenderType.debugQuads() instead.
 */
@Mod.EventBusSubscriber(modid = TargetHighlightMod.MODID, value = Dist.CLIENT)
public final class TargetRenderer {

    private static final float[] QUAD = new float[12];

    private TargetRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        if (!THConfig.ENABLED.get()) {
            return;
        }
        LivingEntity target = TargetSelector.getTarget();
        if (target == null) {
            return;
        }
        boolean ring = THConfig.RING_ENABLED.get();
        boolean marker = THConfig.MARKER_ENABLED.get();
        if (!ring && !marker) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        float partial = event.getPartialTick();
        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();

        // interpolated position so the highlight doesn't stutter at tick rate
        double x = Mth.lerp(partial, target.xo, target.getX()) - cam.x;
        double y = Mth.lerp(partial, target.yo, target.getY()) - cam.y;
        double z = Mth.lerp(partial, target.zo, target.getZ()) - cam.z;

        int rgb = THConfig.colorRgb();
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int a = THConfig.COLOR_ALPHA.get();

        PoseStack stack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());

        stack.pushPose();
        stack.translate(x, y, z);
        Matrix4f m = stack.last().pose();

        if (ring) {
            double radius = target.getBbWidth() * 0.5D + THConfig.RING_PADDING.get();
            drawRing(vc, m, radius, THConfig.RING_THICKNESS.get(), THConfig.RING_SEGMENTS.get(),
                    THConfig.RING_Y_OFFSET.get().floatValue(), r, g, b, a);
        }

        if (marker) {
            float time = (mc.level != null ? mc.level.getGameTime() : 0L) + partial;
            float bob = (float) Math.sin(time * 0.15F) * 0.05F;
            float cy = target.getBbHeight() + THConfig.MARKER_HEIGHT.get().floatValue() + bob;
            drawMarker(vc, m, camera, cy, THConfig.MARKER_SIZE.get().floatValue(), r, g, b, a);
        }

        stack.popPose();
        buffers.endBatch(RenderType.lightning());
    }

    private static void drawRing(VertexConsumer vc, Matrix4f m, double radius, double thickness,
                                 int segments, float y, int r, int g, int b, int a) {
        double inner = Math.max(0.02D, radius - thickness);

        for (int i = 0; i < segments; i++) {
            double a0 = (Math.PI * 2.0D) * i / segments;
            double a1 = (Math.PI * 2.0D) * (i + 1) / segments;
            float c0 = (float) Math.cos(a0);
            float s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1);
            float s1 = (float) Math.sin(a1);

            set(0, (float) (c0 * radius), y, (float) (s0 * radius));
            set(1, (float) (c1 * radius), y, (float) (s1 * radius));
            set(2, (float) (c1 * inner), y, (float) (s1 * inner));
            set(3, (float) (c0 * inner), y, (float) (s0 * inner));
            quad(vc, m, r, g, b, a);
        }
    }

    /** Diamond centred above the head, built from the camera's own up/left vectors so it always faces the viewer. */
    private static void drawMarker(VertexConsumer vc, Matrix4f m, Camera camera,
                                   float centerY, float size, int r, int g, int b, int a) {
        Vector3f left = camera.getLeftVector();
        Vector3f up = camera.getUpVector();
        float tall = size * 1.5F;

        set(0, up.x() * tall, centerY + up.y() * tall, up.z() * tall);               // top
        set(1, -left.x() * size, centerY - left.y() * size, -left.z() * size);       // right
        set(2, -up.x() * tall, centerY - up.y() * tall, -up.z() * tall);             // bottom
        set(3, left.x() * size, centerY + left.y() * size, left.z() * size);         // left
        quad(vc, m, r, g, b, a);
    }

    private static void set(int index, float x, float y, float z) {
        QUAD[index * 3] = x;
        QUAD[index * 3 + 1] = y;
        QUAD[index * 3 + 2] = z;
    }

    /** Emits the quad in QUAD[] front-facing and back-facing. */
    private static void quad(VertexConsumer vc, Matrix4f m, int r, int g, int b, int a) {
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
}
