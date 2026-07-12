package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.events.impl.TravelEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.modules.impl.movement.Speed;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.ColorSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import com.github.duskbyte.utils.player.MoveUtils;

import java.awt.Color;

public class TargetStrafe extends Module {

    public static final TargetStrafe INSTANCE = new TargetStrafe();

    private final DoubleSetting radius = doubleSetting("Radius", 2.5, 0.5, 6.0, 0.5);
    private final IntSetting points = intSetting("Points", 12, 3, 16, 1);
    private final BoolSetting space = boolSetting("Require Space", true);
    private final BoolSetting render = boolSetting("Render", true);
    private final ColorSetting color = colorSetting("Color", new Color(0, 180, 255, 120));

    private int strafeDir = 1;
    private int currentPoint = 0;

    private TargetStrafe() {
        super("Target Strafe", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        strafeDir = 1;
        currentPoint = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (KillAura.INSTANCE.target == null) return;

        if (mc.player.horizontalCollision) {
            strafeDir = -strafeDir;
        }
    }

    @EventHandler
    private void onTravel(TravelEvent event) {
        if (nullCheck()) return;
        if (!canStrafe()) return;

        var target = KillAura.INSTANCE.target;
        if (target == null) return;

        double speed = Math.sqrt(
                mc.player.getDeltaMovement().x * mc.player.getDeltaMovement().x +
                        mc.player.getDeltaMovement().z * mc.player.getDeltaMovement().z
        );
        if (speed < 0.01) speed = 0.28;

        double rad = radius.getValue();
        int count = points.getValue();

        double angleStep = (Math.PI * 2.0) / count;
        double pointX = Math.sin(angleStep * currentPoint) * rad * strafeDir;
        double pointZ = Math.cos(angleStep * currentPoint) * rad;

        double targetYaw = Math.toRadians(getRotations(target.getX() + pointX, target.getZ() + pointZ));

        double motionX = speed * -Math.sin(targetYaw);
        double motionZ = speed * Math.cos(targetYaw);

        mc.player.setDeltaMovement(motionX, mc.player.getDeltaMovement().y, motionZ);

        double dx = Math.abs(target.getX() + pointX - mc.player.getX());
        double dz = Math.abs(target.getZ() + pointZ - mc.player.getZ());
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist <= 0.7) {
            currentPoint = (currentPoint + strafeDir) % count;
            if (currentPoint < 0) currentPoint += count;
        }
    }

    private boolean canStrafe() {
        if (!KillAura.INSTANCE.isEnabled() || KillAura.INSTANCE.target == null) return false;
        if (!MoveUtils.isMoving()) return false;
        if (space.getValue() && !mc.options.keyJump.isDown()) return false;
        return Speed.INSTANCE.isEnabled();
    }

    private double getRotations(double x, double z) {
        double dx = x - mc.player.getX();
        double dz = z - mc.player.getZ();
        return Math.atan2(dz, dx);
    }

}