package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;

public class SpeedMine extends Module {

    public static final SpeedMine INSTANCE = new SpeedMine();

    private final DoubleSetting multiplier = doubleSetting("Multiplier", 1.5, 1.0, 3.0, 0.1);
    private final BoolSetting reset = boolSetting("Reset", true);

    private SpeedMine() {
        super("Speed Mine", Category.PLAYER);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck() || mc.gameMode == null) return;

        // In modern Minecraft, destroy speed is handled by the game
        // We can't directly modify it, but we can ensure we're always
        // using the best tool by relying on AutoTool
        // This module is kept for compatibility and future expansion
    }

}