package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;

public class AirJump extends Module {

    public static final AirJump INSTANCE = new AirJump();

    private int jumpCooldown = 0;

    private AirJump() {
        super("Air Jump", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        jumpCooldown = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (mc.options.keyJump.isDown() && jumpCooldown <= 0 && !mc.player.onGround()) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.42, mc.player.getDeltaMovement().z);
            mc.player.hurtMarked = true;
            jumpCooldown = 5;
        }

        if (jumpCooldown > 0) jumpCooldown--;
    }
}
