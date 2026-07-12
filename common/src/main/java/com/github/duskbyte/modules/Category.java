package com.github.duskbyte.modules;

import com.github.duskbyte.assets.i18n.DuskTranslate;
import com.github.duskbyte.assets.i18n.TranslateComponent;

public enum Category {

    COMBAT("b", "combat"),
    PLAYER("5", "player"),
    MOVEMENT("@", "movement"),
    RENDER("a", "render"),
    HUD("E", "hud"),
    EXPLOIT("c", "exploit"),
    MISC("!", "misc");

    public final String icon;
    private final String name;
    private final TranslateComponent translateComponent;

    Category(String icon, String name) {
        this.icon = icon;
        this.name = name;
        translateComponent = DuskTranslate.create("categories", name);
    }

    public String getName() {
        return translateComponent.getTranslatedName();
    }

    @Override
    public String toString() {
        return name;
    }

}

