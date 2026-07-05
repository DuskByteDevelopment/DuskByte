package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;

public class BreakCooldown extends Module {

    public static final BreakCooldown INSTANCE = new BreakCooldown();

    private BreakCooldown() {
        super("Break Cooldown", Category.PLAYER);
    }

    public final IntSetting cooldown = intSetting("Cooldown", 0, 0, 5, 1);

}
