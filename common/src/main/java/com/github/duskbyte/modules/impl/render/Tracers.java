package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.Render3DEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;
import com.github.duskbyte.settings.impl.ColorSetting;
import com.github.duskbyte.utils.render.Render3DUtils;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;

public class Tracers extends Module {

    public static final Tracers INSTANCE = new Tracers();

    public enum Target {
        PLAYERS,
        ALL
    }

    private final EnumSetting<Target> target = enumSetting("Target", Target.PLAYERS);
    private final ColorSetting color = colorSetting("Color", new Color(100, 200, 255));
    private final DoubleSetting lineWidth = doubleSetting("Line Width", 1.5, 0.5, 5.0, 0.5);

    private Tracers() {
        super("Tracers", Category.RENDER);
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (nullCheck()) return;

        Vec3 camPos = mc.gameRenderer.getMainCamera().position();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
        Matrix4f matrix = mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.viewRotationMatrix;

        float thickness = lineWidth.getValue().floatValue();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!isValidTarget(entity)) continue;

            Vec3 targetPos = entity.getPosition(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));
            Vec3 relTarget = targetPos.subtract(camPos);
            Vec3 relStart = new Vec3(0, 0, 0); // camera at origin

            Color c = color.getValue();
            int rgba = new Color(c.getRed(), c.getGreen(), c.getBlue(), 180).getRGB();
            Vec3 normal = relTarget.normalize();

            buffer.addVertex(matrix, 0, 0, 0)
                    .setColor(rgba)
                    .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                    .setLineWidth(thickness);
            buffer.addVertex(matrix, (float) relTarget.x, (float) relTarget.y, (float) relTarget.z)
                    .setColor(rgba)
                    .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                    .setLineWidth(thickness);
        }

        Render3DUtils.LINES.draw(buffer.buildOrThrow());
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player) return false;
        if (!entity.isAlive()) return false;

        return switch (target.getValue()) {
            case PLAYERS -> entity instanceof Player;
            case ALL -> true;
        };
    }
}
