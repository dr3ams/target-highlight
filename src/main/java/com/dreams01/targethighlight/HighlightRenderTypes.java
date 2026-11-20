package com.dreams01.targethighlight;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

/**
 * Custom render types. This class extends RenderType only to get access to the protected
 * RenderStateShard constants (depth test, transparency, shader); it is never instantiated.
 */
public abstract class HighlightRenderTypes extends RenderType {

    /** Flat coloured quads that ignore the depth buffer, so they stay visible behind blocks. */
    private static final RenderType MARKER_THROUGH_WALLS = create(
            "targethighlight_marker_through_walls",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    private HighlightRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                 boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    public static RenderType markerThroughWalls() {
        return MARKER_THROUGH_WALLS;
    }
}
