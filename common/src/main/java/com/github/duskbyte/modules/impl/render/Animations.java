package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;

public class Animations extends Module {

    public static final Animations INSTANCE = new Animations();

    private final DoubleSetting speed = doubleSetting("Speed", 1.0, 0.5, 2.0, 0.1);
    private final BoolSetting oldAnimations = boolSetting("Old Animations", false);

    private Animations() {
        super("Animations", Category.RENDER);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (oldAnimations.getValue()) {
            // Adjust swing speed
            if (mc.player.swingTime > 0) {
                mc.player.swingTime = Math.max(0, mc.player.swingTime - 2);
            }
        }
    }

}