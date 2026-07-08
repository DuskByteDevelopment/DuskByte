package com.github.duskbyte.utils.render.esp;

import com.github.duskbyte.assets.resources.ResourceLocationUtils;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.function.Function;

public class CircleESP {

    private static final Minecraft mc = Minecraft.getInstance();

    private static final RenderPipeline TRIANGLE_STRIP_NO_DEPTH_PIPELINE = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(ResourceLocationUtils.getIdentifier("pipeline/triangle_strip"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .build();

    private static final RenderPipeline TRIANGLE_STRIP_PIPELINE = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(ResourceLocationUtils.getIdentifier("pipeline/triangle_strip"))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .build();

    private static final Function<RenderPipeline, RenderType> TRIANGLE_STRIP = Util.memoize(
            renderPipeline -> RenderType.create("duskbyte_triangle_strip", RenderSetup.builder(renderPipeline)
                    .createRenderSetup())
    );

    private static final RenderPipeline CIRCLE_LINES_NO_DEPTH_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(ResourceLocationUtils.getIdentifier("pipeline/circle_lines"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .build();

    private static final RenderPipeline CIRCLE_LINES_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(ResourceLocationUtils.getIdentifier("pipeline/circle_lines"))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withCull(false)
            .build();

    private static final Function<RenderPipeline, RenderType> CIRCLE_LINES = Util.memoize(
            renderPipeline -> RenderType.create("duskbyte_circle_lines", RenderSetup.builder(renderPipeline)
                    .createRenderSetup())
    );

    public static void render(PoseStack poseStack, LivingEntity target, float radius, Color sideColor, Color lineColor, float alphaFactor) {
        boolean canSee = mc.player.hasLineOfSight(target);

        float ticks = (float) (System.currentTimeMillis() % 1000000) * 0.004f;
        float alpha = 0.35f + 0.65f * ((Mth.sin(ticks * 1.8f) + 1.0f) * 0.5f) * alphaFactor;

        float tickDelta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        double x = Mth.lerp(tickDelta, target.xo, target.getX()) - mc.getEntityRenderDispatcher().camera.position().x;
        double y = Mth.lerp(tickDelta, target.yo, target.getY()) - mc.getEntityRenderDispatcher().camera.position().y + Math.sin(ticks) + 1;
        double z = Mth.lerp(tickDelta, target.zo, target.getZ()) - mc.getEntityRenderDispatcher().camera.position().z;

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        BufferBuilder triBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = poseStack.last().pose();

        float sinTicks = (float) -Math.sin(ticks + 1) / 2.7f;
        float sr = sideColor.getAlpha() / 255.0f;
        float sg = sideColor.getGreen() / 255.0f;
        float sb = sideColor.getBlue() / 255.0f;
        float sideAlpha = 0.52f * alpha;

        for (float i = 0; i <= (Math.PI * 2); i += ((float) Math.PI * 2) / 64.F) {
            float vecX = (float) (radius * Math.cos(i));
            float vecZ = (float) (radius * Math.sin(i));

            triBuffer.addVertex(matrix, vecX, sinTicks, vecZ).setColor(sr, sg, sb, 0.0f);
            triBuffer.addVertex(matrix, vecX, 0, vecZ).setColor(sr, sg, sb, sideAlpha);
        }

        TRIANGLE_STRIP.apply(canSee ? TRIANGLE_STRIP_PIPELINE : TRIANGLE_STRIP_NO_DEPTH_PIPELINE).draw(triBuffer.buildOrThrow());

        BufferBuilder lineBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
        PoseStack.Pose entry = poseStack.last();

        float lr = lineColor.getRed() / 255.0f;
        float lg = lineColor.getGreen() / 255.0f;
        float lb = lineColor.getBlue() / 255.0f;
        float la = Math.round(lineColor.getAlpha() * alpha) / 255.0f;

        for (int i = 0; i <= 180; i++) {
            float radAngle = (float) (i * Math.PI * 2 / 90);
            float nextAngle = (float) ((i + 1) * Math.PI * 2 / 90);

            float x1 = (float) (-Math.sin(radAngle) * radius);
            float z1 = (float) (Math.cos(radAngle) * radius);
            float x2 = (float) (-Math.sin(nextAngle) * radius);
            float z2 = (float) (Math.cos(nextAngle) * radius);
            float nx = (float) -Math.cos(radAngle);
            float nz = (float) -Math.sin(radAngle);

            lineBuffer.addVertex(entry, x1, 0f, z1).setColor(lr, lg, lb, la).setNormal(entry, nx, 0f, nz).setLineWidth(2f);
            lineBuffer.addVertex(entry, x2, 0f, z2).setColor(lr, lg, lb, la).setNormal(entry, nx, 0f, nz).setLineWidth(2f);
        }

        CIRCLE_LINES.apply(canSee ? CIRCLE_LINES_PIPELINE : CIRCLE_LINES_NO_DEPTH_PIPELINE).draw(lineBuffer.buildOrThrow());

        poseStack.popPose();
    }

}
