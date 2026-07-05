package com.github.duskbyte.graphics.text;

import com.github.duskbyte.assets.resources.ResourceLocationUtils;
import com.github.duskbyte.graphics.text.ttf.TtfFontLoader;

public class StaticFontLoader {

    public static final TtfFontLoader DEFAULT = new TtfFontLoader(ResourceLocationUtils.getIdentifier("fonts/font.ttf"));

    public static final TtfFontLoader ICONS = new TtfFontLoader(ResourceLocationUtils.getIdentifier("fonts/icon.ttf"));

    public static final TtfFontLoader JURA = new TtfFontLoader(ResourceLocationUtils.getIdentifier("fonts/jura-light.ttf"));

    public static final TtfFontLoader OSAKA_CHIPS = new TtfFontLoader(ResourceLocationUtils.getIdentifier("fonts/osakachips.ttf"));

}
