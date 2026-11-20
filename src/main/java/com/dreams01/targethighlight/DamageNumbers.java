package com.dreams01.targethighlight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Floating damage numbers. This mod is client-only, so damage is detected by watching each nearby
 * mob's synced health drop between ticks. A number rises from the mob and fades out.
 * Off by default.
 */
@Mod.EventBusSubscriber(modid = TargetHighlightMod.MODID, value = Dist.CLIENT)
public final class DamageNumbers {
    private static final Random RANDOM = new Random();
    private static final int MAX_ACTIVE = 32;
    private static final int SETTLE_TICKS = 10;      // ignore health changes right after a mob appears
    private static final float RISE_HEIGHT = 0.9F;   // blocks the number climbs
    private static final int HEAL_RGB = 0x55FF55;
    private static final int FALLBACK_RGB = 0xFF6B5A;

    private static final Map<Integer, Tracked> TRACKED = new HashMap<>();
    private static final List<Popup> POPUPS = new ArrayList<>();
    private static int tickCounter;

    private static final class Tracked {
        float health;
        int seen;
    }

    private static final class Popup {
        double x;
        double y;
        double z;
        int spawnTick;
        String text;
        int rgb;
        float sizeFactor;
    }

    private DamageNumbers() {
    }

    public static void clear() {
        TRACKED.clear();
        POPUPS.clear();
    }

    /** Called once per client tick. */
    public static void tick(Minecraft mc) {
        if (!THConfig.ENABLED.get() || !THConfig.DAMAGE_ENABLED.get() || mc.player == null || mc.level == null) {
            if (!TRACKED.isEmpty() || !POPUPS.isEmpty()) {
                clear();
            }
            return;
        }
        tickCounter++;

        final int duration = THConfig.DAMAGE_DURATION.get();
        POPUPS.removeIf(p -> tickCounter - p.spawnTick >= duration);

        Player player = mc.player;
        double range = THConfig.DAMAGE_RANGE.get();
        double min = THConfig.DAMAGE_MIN.get();
        boolean showHealing = THConfig.DAMAGE_SHOW_HEALING.get();
        boolean onlyTarget = THConfig.DAMAGE_ONLY_TARGET.get();
        LivingEntity target = TargetSelector.getTarget();

        List<LivingEntity> nearby = mc.level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(range),
                e -> e != player && !(e instanceof Player) && !(e instanceof ArmorStand) && !e.isRemoved());

        Set<Integer> seen = new HashSet<>();
        for (LivingEntity e : nearby) {
            int id = e.getId();
            seen.add(id);

            float health = e.getHealth();
            if (Float.isNaN(health)) {
                continue;
            }
            Tracked t = TRACKED.get(id);
            if (t == null) {
                t = new Tracked();
                t.health = health;
                TRACKED.put(id, t);
                continue;
            }

            float change = t.health - health;
            t.health = health;
            t.seen++;
            if (t.seen < SETTLE_TICKS) {
                continue;
            }
            if (onlyTarget && e != target) {
                continue;
            }

            if (change >= min) {
                spawn(e, change, false);
            } else if (showHealing && -change >= min) {
                spawn(e, -change, true);
            }
        }
        TRACKED.keySet().retainAll(seen);
    }

    private static void spawn(LivingEntity e, float amount, boolean heal) {
        if (POPUPS.size() >= MAX_ACTIVE) {
            POPUPS.remove(0);
        }
        double spread = Math.max(0.4D, e.getBbWidth());

        Popup p = new Popup();
        p.x = e.getX() + (RANDOM.nextDouble() - 0.5D) * spread;
        p.y = e.getY() + e.getBbHeight() * 0.8D + RANDOM.nextDouble() * 0.2D;
        p.z = e.getZ() + (RANDOM.nextDouble() - 0.5D) * spread;
        p.spawnTick = tickCounter;
        p.text = (heal ? "+" : "") + format(amount);

        float max = Math.max(1.0F, e.getMaxHealth());
        int bigPercent = THConfig.DAMAGE_BIG_PERCENT.get();
        boolean big = !heal && bigPercent > 0 && amount / max * 100.0F >= bigPercent;
        if (heal) {
            p.rgb = HEAL_RGB;
        } else {
            p.rgb = THConfig.hexToRgb(big ? THConfig.DAMAGE_BIG_HEX.get() : THConfig.DAMAGE_HEX.get(), FALLBACK_RGB);
        }
        p.sizeFactor = big ? 1.35F : 1.0F;
        POPUPS.add(p);
    }

    private static String format(float amount) {
        if (amount >= 10.0F) {
            return Integer.toString(Math.round(amount));
        }
        String s = String.format(Locale.ROOT, "%.1f", amount);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || POPUPS.isEmpty()) {
            return;
        }
        if (!THConfig.ENABLED.get() || !THConfig.DAMAGE_ENABLED.get()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();
        PoseStack stack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        float partial = event.getPartialTick();
        float duration = THConfig.DAMAGE_DURATION.get();
        float userScale = THConfig.DAMAGE_SCALE.get().floatValue();

        boolean drawn = false;
        for (Popup p : POPUPS) {
            float age = (tickCounter - p.spawnTick + partial) / duration;
            if (age < 0.0F || age >= 1.0F) {
                continue;
            }

            float rise = RISE_HEIGHT * (1.0F - (1.0F - age) * (1.0F - age));   // fast at first, then slows
            double x = p.x - cam.x;
            double y = p.y + rise - cam.y;
            double z = p.z - cam.z;
            double dist = Math.sqrt(x * x + y * y + z * z);

            float distanceScale = (float) Mth.clamp(dist / 10.0D, 1.0D, 3.0D);   // stay readable far away
            float pop = age < 0.15F ? 0.6F + 0.4F * (age / 0.15F) : 1.0F;        // quick pop-in
            float s = 0.03F * userScale * p.sizeFactor * distanceScale * pop;

            int alpha = age < 0.6F ? 255 : Math.max(8, (int) (255.0F * (1.0F - (age - 0.6F) / 0.4F)));
            int color = (alpha << 24) | p.rgb;

            stack.pushPose();
            stack.translate(x, y, z);
            stack.mulPose(camera.rotation());
            stack.scale(-s, -s, s);
            Matrix4f mat = stack.last().pose();
            float width = font.width(p.text);
            font.drawInBatch(p.text, -width / 2.0F, -4.0F, color, true, mat, buffers,
                    Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            stack.popPose();
            drawn = true;
        }
        if (drawn) {
            buffers.endBatch();
        }
    }
}
