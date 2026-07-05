package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;

public class AspectRatio extends Module {

    public static final AspectRatio INSTANCE = new AspectRatio();

    private AspectRatio() {
        super("Aspect Ratio", Category.RENDER);
    }

    public final DoubleSetting ratio = doubleSetting("Ratio", 1.78, 0.1, 8.0, 0.1);

}
