package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.events.impl.TravelEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;
import com.github.duskbyte.utils.player.MoveUtils;

public class Speed extends Module {

    public static final Speed INSTANCE = new Speed();

    private enum Mode {
        Strafe,
        Hop,
        Ground
    }

    private final EnumSetting<Mode> mode = enumSetting("Mode", Mode.Strafe);
    private final DoubleSetting speed = doubleSetting("Speed", 0.5, 0.1, 2.0, 0.1);
    private final BoolSetting autoJump = boolSetting("Auto Jump", true);

    private int ticks = 0;

    private Speed() {
        super("Speed", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;
        ticks++;
    }

    @EventHandler
    private void onTravel(TravelEvent event) {
        if (nullCheck() || !MoveUtils.isMoving()) return;

        if (!mc.player.onGround()) {
            if (mode.getValue() == Mode.Strafe) {
                // Air strafe - maintain speed
                double[] motion = MoveUtils.forward(speed.getValue());
                double currentSpeed = Math.sqrt(
                        mc.player.getDeltaMovement().x * mc.player.getDeltaMovement().x +
                                mc.player.getDeltaMovement().z * mc.player.getDeltaMovement().z
                );
                double maxSpeed = speed.getValue();
                if (currentSpeed < maxSpeed) {
                    mc.player.setDeltaMovement(
                            motion[0] * 0.9,
                            mc.player.getDeltaMovement().y,
                            motion[1] * 0.9
                    );
                }
            }
            return;
        }

        switch (mode.getValue()) {
            case Strafe -> {
                double[] motion = MoveUtils.forward(speed.getValue());
                mc.player.setDeltaMovement(motion[0], mc.player.getDeltaMovement().y, motion[1]);
                if (autoJump.getValue()) {
                    mc.player.jumpFromGround();
                }
            }
            case Hop -> {
                if (ticks % 4 == 0) {
                    mc.player.jumpFromGround();
                }
                double[] motion = MoveUtils.forward(speed.getValue());
                mc.player.setDeltaMovement(motion[0], mc.player.getDeltaMovement().y, motion[1]);
            }
            case Ground -> {
                double[] motion = MoveUtils.forward(speed.getValue());
                mc.player.setDeltaMovement(motion[0], mc.player.getDeltaMovement().y, motion[1]);
            }
        }
    }

}