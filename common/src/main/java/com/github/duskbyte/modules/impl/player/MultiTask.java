package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;

public class MultiTask extends Module {

    public static final MultiTask INSTANCE = new MultiTask();

    private MultiTask() {
        super("Multi Task", Category.PLAYER);
    }

}
