package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Items;

public class AutoFish extends Module {

    public static final AutoFish INSTANCE = new AutoFish();

    private final BoolSetting autoCast = boolSetting("Auto Cast", true);
    private final IntSetting castDelay = intSetting("Cast Delay", 5, 0, 20, 1);

    private int delay;

    private AutoFish() {
        super("Auto Fish", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        delay = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (delay > 0) {
            delay--;
            return;
        }

        if (mc.player.getMainHandItem().getItem() != Items.FISHING_ROD
                && mc.player.getOffhandItem().getItem() != Items.FISHING_ROD) return;

        FishingHook hook = mc.player.fishing;
        if (hook == null) {
            if (autoCast.getValue()) {
                mc.player.connection.sendChat("/cast");
                delay = castDelay.getValue();
            }
            return;
        }

        // Check if the bobber has a bite by checking if it's moving
        if (hook.getDeltaMovement().lengthSqr() < 0.001 && !hook.isUnderWater()) {
            // Potential bite detected
        }
    }
}
