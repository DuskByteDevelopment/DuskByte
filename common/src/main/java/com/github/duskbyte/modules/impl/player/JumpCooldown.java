package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;

public class JumpCooldown extends Module {

    public static final JumpCooldown INSTANCE = new JumpCooldown();

    private JumpCooldown() {
        super("Jump Cooldown", Category.PLAYER);
    }

    public final IntSetting cooldown = intSetting("Cooldown", 0, 0, 9, 1);

}
