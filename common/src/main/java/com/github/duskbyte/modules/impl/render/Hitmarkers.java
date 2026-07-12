package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.AttackEntityEvent;
import com.github.duskbyte.events.impl.Render2DEvent;
import com.github.duskbyte.graphics.renderers.TextRenderer;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.ColorSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import com.google.common.base.Suppliers;
import net.minecraft.client.DeltaTracker;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class Hitmarkers extends Module {

    public static final Hitmarkers INSTANCE = new Hitmarkers();

    private final DoubleSetting scale = doubleSetting("Scale", 1.0, 0.5, 2.0, 0.1);
    private final ColorSetting color = colorSetting("Color", Color.RED);
    private final IntSetting duration = intSetting("Duration", 500, 100, 2000, 100);

    private final Supplier<TextRenderer> textRendererSupplier = Suppliers.memoize(TextRenderer::new);

    private final List<Hitmarker> activeMarkers = new ArrayList<>();

    private Hitmarkers() {
        super("Hitmarkers", Category.RENDER);
    }

    @EventHandler
    private void onAttack(AttackEntityEvent event) {
        if (nullCheck()) return;

        float centerX = mc.getWindow().getGuiScaledWidth() / 2.0f;
        float centerY = mc.getWindow().getGuiScaledHeight() / 2.0f;

        activeMarkers.add(new Hitmarker(centerX, centerY, System.currentTimeMillis()));
    }

    @EventHandler
    private void onRender2D(Render2DEvent.HUD event) {
        if (nullCheck() || activeMarkers.isEmpty()) return;

        TextRenderer textRenderer = textRendererSupplier.get();
        float s = scale.getValue().floatValue();
        long now = System.currentTimeMillis();

        Iterator<Hitmarker> iterator = activeMarkers.iterator();
        while (iterator.hasNext()) {
            Hitmarker marker = iterator.next();
            long elapsed = now - marker.timestamp;

            if (elapsed > duration.getValue()) {
                iterator.remove();
                continue;
            }

            float alpha = 1.0f - (float) elapsed / duration.getValue();
            Color c = new Color(
                    color.getValue().getRed(),
                    color.getValue().getGreen(),
                    color.getValue().getBlue(),
                    (int) (255 * alpha)
            );

            // Draw 4 cross lines (X shape)
            float size = 5 * s;
            // We can only draw text, so use a simple character
            textRenderer.addText("+", marker.x - 4 * s, marker.y - 4 * s, 0.5f * s, c);
        }

        textRenderer.drawAndClear();
    }

    private record Hitmarker(float x, float y, long timestamp) {}

}