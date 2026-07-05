package com.github.duskbyte.assets.i18n;

/**
 * Static factory that creates {@link TranslateComponent} instances
 * with the "duskbyte" prefix. Used for DuskByte's own i18n keys.
 */
public class DuskTranslate {

    private static final String PREFIX = "duskbyte";

    public static TranslateComponent create(String prefix, String suffix) {
        return DefaultTranslateComponent.create(PREFIX + "." + prefix + "." + suffix);
    }

}

