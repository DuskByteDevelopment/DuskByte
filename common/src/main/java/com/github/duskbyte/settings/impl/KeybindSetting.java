package com.github.duskbyte.settings.impl;

import com.github.duskbyte.settings.Setting;

public class KeybindSetting extends Setting<Integer> {

    public KeybindSetting(String name, int defaultValue, Dependency dependency) {
        super(name, dependency, null);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

}


