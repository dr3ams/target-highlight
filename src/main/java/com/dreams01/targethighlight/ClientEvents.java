package com.dreams01.targethighlight;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/** Keybind registration and the client tick. Only loaded on the physical client. */
public final class ClientEvents {

    /** Unbound by default so it can't clash with the many other keybinds in a big pack. Bind it under Controls > Target Highlight. */
    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.targethighlight.toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.targethighlight");

    private ClientEvents() {
    }

    @Mod.EventBusSubscriber(modid = TargetHighlightMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = TargetHighlightMod.MODID, value = Dist.CLIENT)
    public static final class ForgeBus {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();

            while (TOGGLE_KEY.consumeClick()) {
                boolean now = !THConfig.ENABLED.get();
                THConfig.ENABLED.set(now);
                THConfig.ENABLED.save();
                if (mc.player != null) {
                    mc.player.displayClientMessage(
                            Component.translatable(now ? "message.targethighlight.on" : "message.targethighlight.off"), true);
                }
            }

            TargetSelector.tick(mc);
        }

        @SubscribeEvent
        public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            TargetSelector.clear();
        }
    }
}
