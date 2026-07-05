package com.github.duskbyte;

import com.github.duskbyte.addon.DuskAddon;

import java.util.List;

/**
 * Built-in Fabric addon for Fabric-only features.
 */
public class FabricStuff extends DuskAddon {

    public FabricStuff() {
        super("duskbyte_fabric");
    }

    @Override
    public void onSetup() {
        DuskByte.LOGGER.info("Fabric platform addon initialized.");
    }

    @Override
    public String getDisplayName() {
        return "Fabric Platform";
    }

    @Override
    public String getDescription() {
        return "Built-in addon for Fabric-specific integrations.";
    }

    @Override
    public String getVersion() {
        return DuskByte.VERSION;
    }

    @Override
    public List<String> getAuthors() {
        return List.of("DuskByte");
    }

}