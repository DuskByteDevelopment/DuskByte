package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;

public class NoHurtCam extends Module {

    public static final NoHurtCam INSTANCE = new NoHurtCam();

    private NoHurtCam() {
        super("No Hurt Cam", Category.RENDER);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;
        // Reset hurt time to prevent camera wobble
        if (mc.player.hurtTime > 0) {
            mc.player.hurtTime = 0;
        }
    }

}