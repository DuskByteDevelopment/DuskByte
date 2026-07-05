package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.bus.EventPriority;
import com.github.duskbyte.events.impl.AfterRender3DEvent;
import com.github.duskbyte.graphics.shaders.FilterShader;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.ColorSetting;

import java.awt.*;

public class Filter extends Module {

    public static final Filter INSTANCE = new Filter();

    private final ColorSetting baseColor = colorSetting("Base Color", new Color(70, 70, 150, 50), true);

    private Filter() {
        super("Filter", Category.RENDER);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onAfterRender3D(AfterRender3DEvent event) {
        if (nullCheck()) {
            return;
        }

        Color color = this.baseColor.getValue();
        if (color.getAlpha() <= 0) {
            return;
        }

        FilterShader.INSTANCE.renderMainTarget(color);
    }

}
