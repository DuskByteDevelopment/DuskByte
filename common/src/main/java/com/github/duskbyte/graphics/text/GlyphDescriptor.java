package com.github.duskbyte.graphics.text;

import com.github.duskbyte.graphics.text.ttf.TtfGlyphAtlas;

public record GlyphDescriptor(
        TtfGlyphAtlas atlas,
        TtfGlyphAtlas.GlyphUV uv,
        int width,
        int height,
        int xOffset,
        int yOffset,
        int advance
) {
}