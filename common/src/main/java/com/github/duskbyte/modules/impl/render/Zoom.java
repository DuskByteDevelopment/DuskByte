package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.client.Minecraft;

public class Zoom extends Module {

    public static final Zoom INSTANCE = new Zoom();

    private final DoubleSetting zoomFactor = doubleSetting("Zoom Factor", 4.0, 2.0, 20.0, 1.0);
    private final IntSetting smoothSpeed = intSetting("Smooth Speed", 5, 1, 10, 1);

    private double currentFov = 0;

    private Zoom() {
        super("Zoom", Category.RENDER);
    }

    @Override
    protected void onDisable() {
        currentFov = 0;
    }

    public double getFov(double originalFov) {
        if (!isEnabled()) return originalFov;

        double target = originalFov / zoomFactor.getValue();
        if (currentFov == 0) currentFov = originalFov;

        currentFov += (target - currentFov) * (smoothSpeed.getValue() / 10.0);
        return currentFov;
    }

    public static double applyZoom(double originalFov) {
        return INSTANCE.getFov(originalFov);
    }
}
