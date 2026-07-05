package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.AfterRender3DEvent;
import com.github.duskbyte.graphics.shaders.FXAAShader;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;

public class AntiAlias extends Module {

    public static final AntiAlias INSTANCE = new AntiAlias();

    private AntiAlias() {
        super("Anti Alias", Category.RENDER);
    }

    @EventHandler
    public void onAfterRender3D(AfterRender3DEvent event) {
        if (nullCheck()) {
            return;
        }

        FXAAShader.INSTANCE.renderMainTarget();
    }

}
