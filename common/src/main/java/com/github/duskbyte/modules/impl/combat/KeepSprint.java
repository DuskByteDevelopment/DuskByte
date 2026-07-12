package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.SlowdownEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;

public class KeepSprint extends Module {

    public static final KeepSprint INSTANCE = new KeepSprint();

    private KeepSprint() {
        super("Keep Sprint", Category.COMBAT);
    }

    @EventHandler
    private void onSlowdown(SlowdownEvent event) {
        if (nullCheck()) return;
        event.setSlowdown(false);
    }

}