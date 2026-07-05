package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;

public class UseCooldown extends Module {

    public static final UseCooldown INSTANCE = new UseCooldown();

    private UseCooldown() {
        super("Use Cooldown", Category.PLAYER);
    }

    public final IntSetting cooldown = intSetting("Cooldown", 0, 0, 4, 1);

}
