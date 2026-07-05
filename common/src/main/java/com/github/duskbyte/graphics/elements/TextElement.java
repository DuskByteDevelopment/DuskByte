package com.github.duskbyte.graphics.elements;

import com.github.duskbyte.graphics.text.StaticFontLoader;
import com.github.duskbyte.graphics.text.ttf.TtfFontLoader;

import java.awt.*;

public record TextElement(
        String text,
        float x,
        float y,
        float scale,
        Color color,
        TtfFontLoader fontLoader
) {

    public TextElement {
        if (text == null) {
            text = "";
        }
        if (fontLoader == null) {
            fontLoader = StaticFontLoader.DEFAULT;
        }
    }

    public static TextElement of(String text, float x, float y, float scale, Color color) {
        return new TextElement(text, x, y, scale, color, StaticFontLoader.DEFAULT);
    }

    public static TextElement of(String text, float x, float y, Color color) {
        return new TextElement(text, x, y, 1.0f, color, StaticFontLoader.DEFAULT);
    }
}

