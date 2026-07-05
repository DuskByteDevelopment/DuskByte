package com.github.duskbyte.settings.impl;

import com.github.duskbyte.settings.Setting;

public class ButtonSetting extends Setting<Runnable> {

    public ButtonSetting(String name, Runnable func, Dependency dependency) {
        super(name, dependency, null);
        this.value = func;
        this.defaultValue = func;
    }

}