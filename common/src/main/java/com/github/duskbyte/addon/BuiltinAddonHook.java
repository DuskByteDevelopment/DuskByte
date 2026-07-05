package com.github.duskbyte.addon;

import com.github.duskbyte.DuskByte;
import com.github.duskbyte.FabricStuff;

/**
 * Registers Epsilon's built-in Fabric addon through Fabric custom entrypoint.
 */
public class BuiltinAddonHook implements FabricAddonHook {

    @Override
    public void registerAddon(DuskAddonSetup event) {
        event.registerAddon(new FabricStuff());
    }

}

