package com.dreams01.targethighlight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;

/** Ranged weapon detection, aiming state and draw progress for ranged mode. */
public final class RangedCheck {
    private static final IdListMatcher EXTRA = new IdListMatcher(THConfig.RANGED_EXTRA_ITEMS::get);

    private RangedCheck() {
    }

    /** Bows, crossbows (including modded ones), tridents, and anything in the extraItems list. */
    public static boolean isRangedWeapon(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ProjectileWeaponItem || item instanceof TridentItem) {
            return true;
        }
        return EXTRA.matches(BuiltInRegistries.ITEM.getKey(item));
    }

    /** Drawing, charging, or holding a loaded crossbow. Only meaningful for the main hand. */
    public static boolean isAiming(Player player, ItemStack stack) {
        if (stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack)) {
            return true;
        }
        return isUsingMainHand(player);
    }

    /** How far the weapon is drawn: 0 = not at all, 1 = ready to fire at full power. */
    public static float drawProgress(Player player, ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof CrossbowItem) {
            if (CrossbowItem.isCharged(stack)) {
                return 1.0F;
            }
            if (!isUsingMainHand(player)) {
                return 0.0F;
            }
            float duration = CrossbowItem.getChargeDuration(stack);
            return duration <= 0.0F ? 1.0F : Mth.clamp(player.getTicksUsingItem() / duration, 0.0F, 1.0F);
        }

        if (!isUsingMainHand(player)) {
            return 0.0F;
        }
        if (item instanceof BowItem) {
            return BowItem.getPowerForTime(player.getTicksUsingItem());
        }
        if (item instanceof TridentItem) {
            // a trident needs about 10 ticks of use before it can be thrown
            return Mth.clamp(player.getTicksUsingItem() / 10.0F, 0.0F, 1.0F);
        }
        // other listed items have no draw information: ready while in use
        return 1.0F;
    }

    /** True while the main-hand item is being used (drawing, charging, winding up). */
    public static boolean isUsingMainHand(Player player) {
        return player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND;
    }
}
