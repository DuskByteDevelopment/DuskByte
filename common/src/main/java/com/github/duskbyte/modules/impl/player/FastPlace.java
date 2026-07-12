package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.StartUseItemEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.world.item.BlockItem;

public class FastPlace extends Module {

    public static final FastPlace INSTANCE = new FastPlace();

    private final IntSetting delay = intSetting("Delay", 0, 0, 4, 1);

    private FastPlace() {
        super("Fast Place", Category.PLAYER);
    }

    @EventHandler
    private void onStartUseItem(StartUseItemEvent event) {
        if (nullCheck()) return;

        if (!(mc.player.getMainHandItem().getItem() instanceof BlockItem) &&
                !(mc.player.getOffhandItem().getItem() instanceof BlockItem)) {
            return;
        }

        // Reset the item use timer to allow fast placement
        mc.player.resetAttackStrengthTicker();
    }

}