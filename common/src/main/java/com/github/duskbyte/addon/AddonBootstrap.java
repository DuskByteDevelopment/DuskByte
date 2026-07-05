package com.github.duskbyte.addon;

import com.github.duskbyte.managers.AddonManager;

/**
 * Shared addon bootstrap utility used by multiple loaders.
 */
public class AddonBootstrap {

    private AddonBootstrap() {
    }

    public static void registerAddons(DuskAddonSetup addonEvent) {
        if (addonEvent != null) {
            registerAddons(addonEvent.getAddons());
        }
    }

    public static void registerAddons(Iterable<DuskAddon> addons) {
        AddonManager.INSTANCE.registerAddons(addons);
    }

    public static void setupAddons(DuskAddonSetup addonEvent) {
        registerAddons(addonEvent);
        AddonManager.INSTANCE.setupAddons();
    }

    public static void setupAddons(Iterable<DuskAddon> addons) {
        registerAddons(addons);
        AddonManager.INSTANCE.setupAddons();
    }

}
