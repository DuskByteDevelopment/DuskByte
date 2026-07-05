package com.github.duskbyte.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuskAddonSetup {

    private final ArrayList<DuskAddon> addons = new ArrayList<>();

    public void registerAddon(DuskAddon addon) {
        if (addon != null) {
            addons.add(addon);
        }
    }

    public List<DuskAddon> getAddons() {
        return Collections.unmodifiableList(addons);
    }

}
