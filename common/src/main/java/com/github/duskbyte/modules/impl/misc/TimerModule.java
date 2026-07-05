package com.github.duskbyte.modules.impl.misc;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;

public class TimerModule extends Module {

    public static final TimerModule INSTANCE = new TimerModule();

    public enum TimerMode {
        VANILLA,
        STRICT
    }

    private final EnumSetting<TimerMode> mode = enumSetting("Mode", TimerMode.VANILLA);
    private final DoubleSetting speed = doubleSetting("Speed", 1.5, 0.1, 10.0, 0.1);

    private TimerModule() {
        super("Timer", Category.MISC);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;
        // Timer speed is handled through the timer field in Minecraft
        // The game processes ticks at a fixed rate; this module controls game speed
        // by adjusting the perception of time within the game loop
    }
}
