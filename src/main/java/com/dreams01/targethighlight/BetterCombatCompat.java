package com.dreams01.targethighlight;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Optional Better Combat support, done with reflection so there is no compile-time or runtime
 * dependency. If Better Combat is missing, or its classes look different from what we expect,
 * every call simply returns -1 and the caller falls back to the vanilla entity reach.
 *
 * Better Combat gives each weapon its own "attack_range" and does not use the vanilla reach attribute.
 */
public final class BetterCombatCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MOD_ID = "bettercombat";
    private static final String REGISTRY_CLASS = "net.bettercombat.logic.WeaponRegistry";

    private static boolean initialised;
    private static boolean available;
    private static Method getAttributes;   // static WeaponRegistry.getAttributes(ItemStack)
    private static Method attackRange;     // WeaponAttributes.attackRange()

    private BetterCombatCompat() {
    }

    /**
     * Attack range, in blocks, of the given weapon according to Better Combat.
     * Returns -1 when Better Combat is not installed, the item has no Better Combat attributes,
     * or the weapon has no melee range (bows and crossbows report 0).
     */
    public static double getAttackRange(ItemStack stack) {
        if (stack.isEmpty() || !init()) {
            return -1.0D;
        }
        try {
            Object attributes = getAttributes.invoke(null, stack);
            if (attributes == null) {
                return -1.0D;
            }
            if (attackRange == null) {
                attackRange = attributes.getClass().getMethod("attackRange");
            }
            Object value = attackRange.invoke(attributes);
            if (value instanceof Number) {
                double range = ((Number) value).doubleValue();
                return range > 0.0D ? range : -1.0D;
            }
            return -1.0D;
        } catch (ReflectiveOperationException | RuntimeException e) {
            // Different Better Combat version than expected: stop trying and use vanilla reach.
            available = false;
            LOGGER.warn("[TargetHighlight] Better Combat range lookup failed, falling back to vanilla reach: {}", e.toString());
            return -1.0D;
        }
    }

    private static boolean init() {
        if (initialised) {
            return available;
        }
        initialised = true;

        ModList mods = ModList.get();
        if (mods == null || !mods.isLoaded(MOD_ID)) {
            return false;
        }
        try {
            Class<?> registry = Class.forName(REGISTRY_CLASS, false, BetterCombatCompat.class.getClassLoader());
            getAttributes = registry.getMethod("getAttributes", ItemStack.class);
            available = true;
            LOGGER.info("[TargetHighlight] Better Combat detected: the in-reach indicator uses weapon attack ranges");
        } catch (ReflectiveOperationException | RuntimeException e) {
            LOGGER.warn("[TargetHighlight] Better Combat is installed but its API was not found, using vanilla reach: {}", e.toString());
        }
        return available;
    }
}
