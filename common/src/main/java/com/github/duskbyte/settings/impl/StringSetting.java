package com.github.duskbyte.settings.impl;

import com.github.duskbyte.settings.Setting;

public class StringSetting extends Setting<String> {

    public StringSetting(String name, String defaultValue, Dependency dependency) {
        super(name, dependency, null);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

}