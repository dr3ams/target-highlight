package com.dreams01.targethighlight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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
 * Draws the highlight for the current target: a flat ring under its feet, a small diamond above
 * its head that always faces the camera, and an optional text label above the diamond.
 *
 * The ring and marker use RenderType.lightning() (untextured, additive-ish, position+colour).
 * Every quad is emitted twice with reversed winding because that render type culls back faces.
 * If you'd rather have normal alpha blending, try RenderType.debugQuads() instead.
 */
@Mod.EventBusSubscriber(modid = TargetHighlightMod.MODID, value = Dist.CLIENT)
public final class TargetRenderer {

    private static final float[] QUAD = new float[12];

    private static final int HEALTH_LOW = 0xFF4040;
    private static final int HEALTH_MID = 0xFFD040;
    private static final int HEALTH_HIGH = 0x50E050;

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
        boolean label = THConfig.LABEL_ENABLED.get();
        if (!ring && !marker && !label) {
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

        Tier tier = TargetSelector.getTier();
        int rgb = THConfig.colorRgb(tier);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int a = THConfig.colorAlpha(tier);

        PoseStack stack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());

        stack.pushPose();
        stack.translate(x, y, z);
        Matrix4f m = stack.last().pose();

        // In ranged mode the highlight grows with distance so it stays readable at bow range
        double dist = Math.sqrt(x * x + y * y + z * z);
        double scale = 1.0D;
        if (TargetSelector.isRangedActive() && THConfig.RANGED_SCALE_WITH_DISTANCE.get()) {
            scale = Mth.clamp(dist / 6.0D, 1.0D, THConfig.RANGED_MAX_SCALE.get());
        }

        // Ring colour: tier colour, or the health gradient. Then the in-reach / draw-progress signal on top.
        int ringRgb = THConfig.RING_HEALTH_TINT.get() ? healthRgb(target) : rgb;
        int ringAlpha = a;
        ReachMode mode = THConfig.IN_REACH_MODE.get();
        if (TargetSelector.isRangedActive() && THConfig.RANGED_CHARGE_BRIGHTNESS.get()) {
            float charge = TargetSelector.getCharge();
            if (mode == ReachMode.BRIGHTEN) {
                ringAlpha = Math.min(255, a + Math.round(THConfig.IN_REACH_ALPHA_BOOST.get() * charge));
            } else if (mode == ReachMode.COLOR && charge >= 0.999F) {
                ringRgb = THConfig.inReachRgb();
            }
        } else if (TargetSelector.isInReach()) {
            if (mode == ReachMode.BRIGHTEN) {
                ringAlpha = Math.min(255, a + THConfig.IN_REACH_ALPHA_BOOST.get());
            } else if (mode == ReachMode.COLOR) {
                ringRgb = THConfig.inReachRgb();
            }
        }

        if (ring) {
            double radius = target.getBbWidth() * 0.5D + THConfig.RING_PADDING.get() * scale;
            drawRing(vc, m, radius, THConfig.RING_THICKNESS.get() * scale, THConfig.RING_SEGMENTS.get(),
                    THConfig.RING_Y_OFFSET.get().floatValue(),
                    (ringRgb >> 16) & 0xFF, (ringRgb >> 8) & 0xFF, ringRgb & 0xFF, ringAlpha);
        }

        // The marker; its top edge is also where the label sits
        float markerTop = target.getBbHeight();
        boolean wallPass = false;
        if (marker) {
            float s = (float) scale;
            float time = (mc.level != null ? mc.level.getGameTime() : 0L) + partial;
            float bob = (float) Math.sin(time * 0.15F) * 0.05F * s;
            float cy = target.getBbHeight() + THConfig.MARKER_HEIGHT.get().floatValue() * s + bob;
            float size = THConfig.MARKER_SIZE.get().floatValue() * s;

            VertexConsumer markerConsumer = vc;
            if (showsThroughWalls(tier)) {
                // A separate render type without a depth test, so the marker stays visible behind blocks
                markerConsumer = buffers.getBuffer(HighlightRenderTypes.markerThroughWalls());
                wallPass = true;
            }
            drawMarker(markerConsumer, m, camera, cy, size, r, g, b, a);
            markerTop = cy + size * 1.5F;
        }

        boolean labelDrawn = false;
        if (label) {
            labelDrawn = drawLabel(mc, stack, camera, buffers, target, rgb, markerTop, scale);
        }

        stack.popPose();
        buffers.endBatch(RenderType.lightning());
        if (wallPass) {
            buffers.endBatch(HighlightRenderTypes.markerThroughWalls());
        }
        if (labelDrawn) {
            buffers.endBatch();
        }
    }

    private static boolean showsThroughWalls(Tier tier) {
        ThroughWalls walls = THConfig.MARKER_THROUGH_WALLS.get();
        return walls == ThroughWalls.ALL || (walls == ThroughWalls.BOSS && tier == Tier.BOSS);
    }

    /** Green at full health, yellow at half, red when low. */
    private static int healthRgb(LivingEntity e) {
        float max = e.getMaxHealth();
        float f = max <= 0.0F ? 1.0F : Mth.clamp(e.getHealth() / max, 0.0F, 1.0F);
        if (f < 0.5F) {
            return lerpRgb(HEALTH_LOW, HEALTH_MID, f * 2.0F);
        }
        return lerpRgb(HEALTH_MID, HEALTH_HIGH, (f - 0.5F) * 2.0F);
    }

    private static int lerpRgb(int from, int to, float t) {
        int r = Math.round(Mth.lerp(t, (from >> 16) & 0xFF, (to >> 16) & 0xFF));
        int g = Math.round(Mth.lerp(t, (from >> 8) & 0xFF, (to >> 8) & 0xFF));
        int b = Math.round(Mth.lerp(t, from & 0xFF, to & 0xFF));
        return (r << 16) | (g << 8) | b;
    }

    private static String formatHealth(LivingEntity e) {
        float hp = Math.max(0.0F, e.getHealth());
        float max = Math.max(1.0F, e.getMaxHealth());
        if (THConfig.LABEL_HEALTH_FORMAT.get() == LabelHealthFormat.PERCENT) {
            return Math.round(hp / max * 100.0F) + "%";
        }
        return (int) Math.ceil(hp) + "/" + (int) Math.ceil(max);
    }

    /** Name (in the tier colour) and health (white), facing the camera, above the marker. */
    private static boolean drawLabel(Minecraft mc, PoseStack stack, Camera camera, MultiBufferSource buffers,
                                     LivingEntity target, int tierRgb, float markerTop, double scale) {
        boolean showName = THConfig.LABEL_SHOW_NAME.get();
        boolean showHealth = THConfig.LABEL_SHOW_HEALTH.get();
        if (!showName && !showHealth) {
            return false;
        }

        MutableComponent text = Component.empty();
        if (showName) {
            text.append(Component.literal(target.getName().getString()).withStyle(Style.EMPTY.withColor(tierRgb)));
        }
        if (showHealth) {
            String health = formatHealth(target);
            text.append(Component.literal(showName ? "  " + health : health).withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        }

        Font font = mc.font;
        float s = 0.025F * THConfig.LABEL_SCALE.get().floatValue() * (float) scale;
        // The text grows downward from its anchor once flipped, so lift the anchor by the text height
        float y = markerTop + THConfig.LABEL_HEIGHT.get().floatValue() * (float) scale + 8.0F * s;

        stack.pushPose();
        stack.translate(0.0D, y, 0.0D);
        stack.mulPose(camera.rotation());
        stack.scale(-s, -s, s);
        Matrix4f mat = stack.last().pose();

        Font.DisplayMode displayMode = THConfig.LABEL_SEE_THROUGH.get() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
        float textX = -font.width(text) / 2.0F;
        font.drawInBatch(text, textX, 0.0F, 0xFFFFFFFF, true, mat, buffers, displayMode, 0, LightTexture.FULL_BRIGHT);

        stack.popPose();
        return true;
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
