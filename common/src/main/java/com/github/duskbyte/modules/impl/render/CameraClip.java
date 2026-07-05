package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;

public class CameraClip extends Module {

    public static final CameraClip INSTANCE = new CameraClip();

    private CameraClip() {
        super("Camera Clip", Category.RENDER);
    }

    public final DoubleSetting distance = doubleSetting("Distance", 3.5, 1.0, 20.0, 0.5);

    public final BoolSetting action = boolSetting("Action", true);
    private final DoubleSetting interpolation = doubleSetting("Interpolation", 0.05, 0.01, 1.0, 0.01, action::getValue);

}
