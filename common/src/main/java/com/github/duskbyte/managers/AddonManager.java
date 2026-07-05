package com.github.duskbyte.managers;

import com.github.duskbyte.DuskByte;
import com.github.duskbyte.addon.DuskAddon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddonManager {

    public static final AddonManager INSTANCE = new AddonManager();

    private final List<DuskAddon> addons = new ArrayList<>();
    private final Set<String> addonIds = new HashSet<>();

    private boolean setupComplete;

    private AddonManager() {
    }

    public synchronized void registerAddon(DuskAddon addon) {
        if (addon == null) {
            return;
        }

        String addonId = addon.getAddonId();
        if (addonId == null || addonId.isBlank()) {
            DuskByte.LOGGER.warn("Ignoring Epsilon addon with blank addonId: {}", addon.getClass().getName());
            return;
        }

        if (!addonIds.add(addonId)) {
            DuskByte.LOGGER.warn("Duplicate Epsilon addon id ignored: {}", addonId);
            return;
        }

        addons.add(addon);
    }

    public synchronized void registerAddons(Iterable<DuskAddon> addonIterable) {
        if (addonIterable == null) {
            return;
        }
        for (DuskAddon addon : addonIterable) {
            registerAddon(addon);
        }
    }

    public synchronized void setupAddons() {
        if (setupComplete) {
            return;
        }
        setupComplete = true;

        for (DuskAddon addon : addons) {
            try {
                addon.initAddonI18n();
                addon.onSetup();
                DuskByte.LOGGER.info("Loaded Epsilon addon: {}", addon.getAddonId());
            } catch (Throwable throwable) {
                DuskByte.LOGGER.error("Failed to setup Epsilon addon: {}", addon.getAddonId(), throwable);
            }
        }
    }

    public synchronized List<DuskAddon> getAddons() {
        return List.copyOf(addons);
    }

}
