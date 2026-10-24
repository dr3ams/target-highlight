package com.dreams01.targethighlight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

/** Decides whether the main hand holds a weapon (used by weapon-only mode). */
public final class WeaponCheck {
    private static final IdListMatcher EXTRA = new IdListMatcher(THConfig.WEAPON_EXTRA_ITEMS::get);

    private WeaponCheck() {
    }

    public static boolean isHoldingWeapon(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();

        if (item instanceof SwordItem) {
            return true;
        }
        if (THConfig.WEAPON_INCLUDE_AXES.get() && item instanceof AxeItem) {
            return true;
        }
        if (THConfig.WEAPON_INCLUDE_RANGED.get() && (item instanceof ProjectileWeaponItem || item instanceof TridentItem)) {
            return true;
        }
        return EXTRA.matches(BuiltInRegistries.ITEM.getKey(item));
    }
}
