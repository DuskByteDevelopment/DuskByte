package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.Render3DEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Tracers extends Module {

    public static final Tracers INSTANCE = new Tracers();

    public enum Target {
        PLAYERS,
        ALL
    }

    private final EnumSetting<Target> target = enumSetting("Target", Target.PLAYERS);
    private final DoubleSetting lineWidth = doubleSetting("Line Width", 1.0, 0.5, 5.0, 0.5);

    private Tracers() {
        super("Tracers", Category.RENDER);
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (nullCheck()) return;

        Vec3 camPos = mc.gameRenderer.getMainCamera().position();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!isValidTarget(entity)) continue;

            Vec3 targetPos = entity.getPosition(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        }
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
