package com.github.duskbyte.modules.impl.misc;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;

public class TimerModule extends Module {

    public static final TimerModule INSTANCE = new TimerModule();

    private final DoubleSetting speed = doubleSetting("Speed", 1.5, 0.1, 10.0, 0.1);

    private TimerModule() {
        super("Timer", Category.MISC);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        // Use TimerUtils for tick rate modification
        // Minecraft handles timing through its internal timer
    }
}
