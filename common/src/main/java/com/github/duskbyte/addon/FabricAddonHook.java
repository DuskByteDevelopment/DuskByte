package com.github.duskbyte.addon;

import com.github.duskbyte.addon.DuskAddonSetup;

/**
 * Custom Fabric entrypoint contract for Epsilon addons.
 */
public interface FabricAddonHook {

    void registerAddon(DuskAddonSetup event);

}

