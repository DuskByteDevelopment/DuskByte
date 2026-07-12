package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;

public class FastLadder extends Module {

    public static final FastLadder INSTANCE = new FastLadder();

    private final DoubleSetting speed = doubleSetting("Speed", 2.0, 1.0, 5.0, 0.5);

    private FastLadder() {
        super("Fast Ladder", Category.MOVEMENT);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (mc.player.onClimbable() && mc.player.horizontalCollision) {
            mc.player.setDeltaMovement(
                    mc.player.getDeltaMovement().x,
                    speed.getValue() * 0.1,
                    mc.player.getDeltaMovement().z
            );
        }
    }

}