package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;

public class FastBow extends Module {

    public static final FastBow INSTANCE = new FastBow();

    private final IntSetting ticks = intSetting("Ticks", 3, 1, 20, 1);

    private FastBow() {
        super("Fast Bow", Category.COMBAT);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (!mc.player.isUsingItem()) return;
        if (!mc.player.getUseItem().is(Items.BOW)) return;

        int usingTicks = mc.player.getTicksUsingItem();
        if (usingTicks >= ticks.getValue()) {
            mc.player.stopUsingItem();
            mc.player.releaseUsingItem();
        }
    }

}