package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;

public class Spider extends Module {

    public static final Spider INSTANCE = new Spider();

    private final DoubleSetting speed = doubleSetting("Speed", 0.2, 0.1, 1.0, 0.05);

    private Spider() {
        super("Spider", Category.MOVEMENT);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (mc.player.horizontalCollision && !mc.player.isFallFlying()) {
            mc.player.setDeltaMovement(
                    mc.player.getDeltaMovement().x,
                    speed.getValue(),
                    mc.player.getDeltaMovement().z
            );
        }
    }
}
