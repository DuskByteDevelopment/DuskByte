package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.Render3DEvent;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.LinkedList;

public class Breadcrumbs extends Module {

    public static final Breadcrumbs INSTANCE = new Breadcrumbs();

    private final IntSetting maxPoints = intSetting("Max Points", 200, 50, 1000, 50);

    private final LinkedList<Vec3> trail = new LinkedList<>();

    private Breadcrumbs() {
        super("Breadcrumbs", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        trail.clear();
    }

    @Override
    protected void onDisable() {
        trail.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        Vec3 pos = mc.player.getPosition(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        if (trail.isEmpty() || trail.getLast().distanceTo(pos) > 0.5) {
            trail.add(pos);
            if (trail.size() > maxPoints.getValue()) {
                trail.removeFirst();
            }
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (trail.size() < 2) return;

        // Render trail as lines
        // Implementation depends on the rendering API used by the project
    }
}
