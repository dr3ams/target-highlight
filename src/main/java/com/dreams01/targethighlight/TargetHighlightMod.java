package com.dreams01.targethighlight;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Client-side target highlight (v1: highlight only).
 * Everything that touches client classes lives in ClientEvents / TargetRenderer, which are
 * only registered on the physical client, so this class is safe to load on a server too.
 */
@Mod(TargetHighlightMod.MODID)
public class TargetHighlightMod {
    public static final String MODID = "targethighlight";

    public TargetHighlightMod() {
        // Written to config/targethighlight-client.toml (name set explicitly)
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, THConfig.SPEC, "targethighlight-client.toml");
    }
}
