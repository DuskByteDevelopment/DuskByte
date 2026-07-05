package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.SendPositionEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.mojang.blaze3d.platform.InputConstants;

public class SafeWalk extends Module {

    public static final SafeWalk INSTANCE = new SafeWalk();

    private SafeWalk() {
        super("Safe Walk", Category.MOVEMENT);
    }

    public boolean isOnBlockEdge(float sensitivity) {
        return !mc.level
                .getCollisions(mc.player, mc.player.getBoundingBox().move(0.0, -0.5, 0.0).inflate(-sensitivity, 0.0, -sensitivity))
                .iterator()
                .hasNext();
    }

    @EventHandler
    public void onMotion(SendPositionEvent e) {
        mc.options.keyShift.setDown(mc.player.onGround() && isOnBlockEdge(0.3F));
    }

    @Override
    public void onDisable() {
        boolean isHoldingShift = InputConstants.isKeyDown(mc.getWindow(), mc.options.keyShift.getDefaultKey().getValue());
        mc.options.keyShift.setDown(isHoldingShift);
    }

}