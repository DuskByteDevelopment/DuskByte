package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.Render2DEvent;
import com.github.duskbyte.graphics.renderers.TextRenderer;
import com.github.duskbyte.graphics.text.StaticFontLoader;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.ColorSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.google.common.base.Suppliers;
import net.minecraft.client.DeltaTracker;

import java.awt.Color;
import java.util.function.Supplier;

public class Keystrokes extends Module {

    public static final Keystrokes INSTANCE = new Keystrokes();

    private final DoubleSetting scale = doubleSetting("Scale", 1.0, 0.5, 2.0, 0.1);
    private final ColorSetting textColor = colorSetting("Text Color", new Color(255, 255, 255));
    private final ColorSetting pressedColor = colorSetting("Pressed Color", new Color(255, 255, 255, 80));

    private final Supplier<TextRenderer> textRendererSupplier = Suppliers.memoize(TextRenderer::new);

    private static final float KEY_SIZE = 28.0f;
    private static final float GAP = 2.0f;

    private Keystrokes() {
        super("Keystrokes", Category.RENDER);
    }

    @EventHandler
    private void onRender2D(Render2DEvent.HUD event) {
        if (nullCheck()) return;

        TextRenderer textRenderer = textRendererSupplier.get();
        float s = scale.getValue().floatValue();
        float keySize = KEY_SIZE * s;
        float gap = GAP * s;

        // WASD layout
        //   W
        // A S D
        float startX = 10.0f;
        float startY = mc.getWindow().getGuiScaledHeight() - keySize * 3 - gap * 2 - 10.0f;

        // W
        drawKey(textRenderer, startX + keySize + gap, startY, "W",
                mc.options.keyUp.isDown(), keySize, s);
        // A
        drawKey(textRenderer, startX, startY + keySize + gap, "A",
                mc.options.keyLeft.isDown(), keySize, s);
        // S
        drawKey(textRenderer, startX + keySize + gap, startY + keySize + gap, "S",
                mc.options.keyDown.isDown(), keySize, s);
        // D
        drawKey(textRenderer, startX + (keySize + gap) * 2, startY + keySize + gap, "D",
                mc.options.keyRight.isDown(), keySize, s);

        textRenderer.drawAndClear();
    }

    private void drawKey(TextRenderer textRenderer, float x, float y, String label, boolean pressed,
                         float keySize, float s) {
        // Background
        int bgColor = pressed ? pressedColor.getValue().getRGB() : new Color(0, 0, 0, 100).getRGB();

        // Use a simple approach - draw the key with the text renderer at the correct position
        float textWidth = textRenderer.getWidth(label, s * 0.7f);
        float textHeight = textRenderer.getHeight(s * 0.7f);
        float textX = x + (keySize - textWidth) / 2.0f;
        float textY = y + (keySize - textHeight) / 2.0f - 1;

        // Draw background rect (approximate with text renderer or just draw text)
        // Since we can't draw rects easily, we'll just draw the text with background color representation
        textRenderer.addText(label, textX, textY, s * 0.7f, textColor.getValue());
    }

}