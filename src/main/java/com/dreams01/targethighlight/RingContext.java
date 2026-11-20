package com.dreams01.targethighlight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

/** Everything a ring style needs to draw one frame. Filled by TargetRenderer. */
final class RingContext {
    VertexConsumer vc;          // for the geometry styles
    Matrix4f m;
    PoseStack stack;            // positioned at the target's feet
    Camera camera;
    MultiBufferSource buffers;  // for the text style
    Minecraft mc;
    LivingEntity target;

    double radius;              // outer radius of the ring, already distance-scaled
    double thickness;           // band thickness, already distance-scaled
    double scale;               // distance scale factor (1 in melee)
    double spin;                // accumulated rotation in radians
    double seconds;             // time in seconds, for small animations
    float partial;              // partial tick
    float y;                    // height above the feet
    int segments;               // segments in a full circle

    int r;
    int g;
    int b;
    int a;
}
