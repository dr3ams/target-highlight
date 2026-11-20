package com.dreams01.targethighlight;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Optional zoom while drawing a bow, charging a crossbow or winding up a trident.
 * The zoom follows the draw progress and eases in and out. It only applies while the item is
 * actively being used, so a loaded crossbow does not keep you zoomed in.
 */
@Mod.EventBusSubscriber(modid = TargetHighlightMod.MODID, value = Dist.CLIENT)
public final class ZoomController {
    private static float current = 1.0F;

    private ZoomController() {
    }

    /** Called once per client tick. */
    public static void tick(Minecraft mc) {
        float target = 1.0F;
        Player player = mc.player;

        if (THConfig.ZOOM_ENABLED.get() && player != null && !player.isSpectator()) {
            ItemStack held = player.getMainHandItem();
            if (RangedCheck.isRangedWeapon(held) && RangedCheck.isUsingMainHand(player)) {
                float progress = RangedCheck.drawProgress(player, held);
                target = 1.0F + (THConfig.ZOOM_MAX.get().floatValue() - 1.0F) * progress;
            }
        }

        current += (target - current) * THConfig.ZOOM_SPEED.get().floatValue();
        if (Math.abs(target - current) < 0.001F) {
            current = target;
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        if (current > 1.0001F) {
            event.setNewFovModifier(event.getNewFovModifier() / current);
        }
    }
}
