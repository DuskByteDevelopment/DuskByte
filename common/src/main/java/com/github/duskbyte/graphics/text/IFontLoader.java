package com.github.duskbyte.graphics.text;

public interface IFontLoader {

    void checkAndLoadChar(char ch);

    void checkAndLoadChars(String chars);

    void destroy();

    GlyphDescriptor getGlyph(char ch);

}
