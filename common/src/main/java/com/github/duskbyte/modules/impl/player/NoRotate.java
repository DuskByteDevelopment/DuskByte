package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;

public class NoRotate extends Module {

    public static final NoRotate INSTANCE = new NoRotate();

    private NoRotate() {
        super("No Rotate", Category.PLAYER);
    }

}
