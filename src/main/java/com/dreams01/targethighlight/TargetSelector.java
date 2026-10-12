package com.dreams01.targethighlight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

/**
 * Picks the enemy nearest the crosshair inside a cone, within (reach + bonus).
 * The current target is kept sticky via a score advantage, so the highlight doesn't
 * flicker between two nearby enemies. Runs on the client tick only.
 */
public final class TargetSelector {
    private static LivingEntity target;
    private static int tickCounter;
    private static int lastSeenTick;

    private TargetSelector() {
    }

    /** Current target, or null. Safe to call every frame. */
    public static LivingEntity getTarget() {
        return target;
    }

    public static void clear() {
        target = null;
    }

    public static void tick(Minecraft mc) {
        if (!THConfig.ENABLED.get() || mc.player == null || mc.level == null || mc.player.isSpectator()) {
            target = null;
            return;
        }

        tickCounter++;

        if (target != null && (!target.isAlive() || target.isRemoved())) {
            target = null;
        }

        if (tickCounter % THConfig.ACQUIRE_INTERVAL_TICKS.get() != 0) {
            return;
        }

        LivingEntity found = acquire(mc.player, mc.level);
        if (found != null) {
            target = found;
            lastSeenTick = tickCounter;
        } else if (target != null && tickCounter - lastSeenTick > THConfig.LOSE_GRACE_TICKS.get()) {
            target = null;
        }
    }

    private static LivingEntity acquire(Player player, ClientLevel level) {
        double range = player.getAttributeValue(ForgeMod.ENTITY_REACH.get()) + THConfig.REACH_BONUS.get();
        double cosCone = Math.cos(Math.toRadians(THConfig.CONE_HALF_ANGLE.get()));
        double sticky = THConfig.STICKY_BONUS.get();
        double distWeight = THConfig.DISTANCE_WEIGHT.get();
        boolean hostileOnly = THConfig.HOSTILE_ONLY.get();
        boolean needSight = THConfig.REQUIRE_LINE_OF_SIGHT.get();

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();

        List<LivingEntity> nearby = level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(range + 2.0D),
                e -> isCandidate(player, e, hostileOnly));

        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;

        for (LivingEntity e : nearby) {
            Vec3 d = e.getBoundingBox().getCenter().subtract(eye);
            double dist = d.length();
            if (dist < 0.1D) {
                continue;
            }
            // reach is measured to the hitbox edge, not the centre
            if (dist - e.getBbWidth() * 0.5D > range) {
                continue;
            }

            double cos = d.dot(look) / dist;
            if (cos < cosCone) {
                continue;
            }

            if (needSight && !canSee(level, player, e)) {
                continue;
            }

            double angle = Math.toDegrees(Math.acos(Math.min(1.0D, cos)));
            double score = angle + dist * distWeight;
            if (e == target) {
                score -= sticky;
            }

            if (score < bestScore) {
                bestScore = score;
                best = e;
            }
        }
        return best;
    }

    private static boolean isCandidate(Player player, LivingEntity e, boolean hostileOnly) {
        if (e == player || e instanceof Player || e instanceof ArmorStand) {
            return false;
        }
        if (!e.isAlive() || e.isInvisible() || e == player.getVehicle()) {
            return false;
        }
        return !hostileOnly || e instanceof Enemy;
    }

    /**
     * Forgiving visibility test: the enemy counts as visible if a ray from our eyes reaches
     * its feet, body or head. Uses VISUAL block shapes, so fences and similar thin cover
     * don't block it the way the vanilla eye-to-eye check does.
     */
    private static boolean canSee(ClientLevel level, Player player, LivingEntity e) {
        Vec3 eye = player.getEyePosition();
        AABB bb = e.getBoundingBox();
        Vec3 c = bb.getCenter();
        double h = bb.getYsize();
        double[] ys = {bb.minY + h * 0.15D, c.y, bb.minY + h * 0.85D};

        for (double y : ys) {
            Vec3 point = new Vec3(c.x, y, c.z);
            BlockHitResult hit = level.clip(new ClipContext(
                    eye, point, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
            if (hit.getType() == HitResult.Type.MISS) {
                return true;
            }
        }
        return false;
    }
}
