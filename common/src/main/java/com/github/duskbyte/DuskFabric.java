package com.github.duskbyte;

import com.github.duskbyte.addon.AddonBootstrap;
import com.github.duskbyte.addon.DuskAddonSetup;
import com.github.duskbyte.addon.FabricAddonHook;
import com.github.duskbyte.assets.i18n.LanguageReloadListener;
import com.github.duskbyte.assets.resources.ResourceLocationUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.server.packs.PackType;

public class DuskFabric implements ClientModInitializer {

    public static final String ADDON_ENTRYPOINT_KEY = "duskbyte:addon";

    @Override
    public void onInitializeClient() {
        IntegrityChecker.check();
        Warning.show();

        DuskAddonSetup addonEvent = new DuskAddonSetup();
        for (EntrypointContainer<FabricAddonHook> container : FabricLoader.getInstance().getEntrypointContainers(ADDON_ENTRYPOINT_KEY, FabricAddonHook.class)) {
            String providerId = container.getProvider().getMetadata().getId();
            try {
                FabricAddonHook entrypoint = container.getEntrypoint();
                entrypoint.registerAddon(addonEvent);
            } catch (Throwable t) {
                DuskByte.LOGGER.error("Failed to register addon entrypoint from mod: {}", providerId, t);
            }
        }
        AddonBootstrap.registerAddons(addonEvent);

        DuskByte.init();

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                ResourceLocationUtils.getIdentifier("objects/reload_listener"),
                new LanguageReloadListener()
        );
    }

}
