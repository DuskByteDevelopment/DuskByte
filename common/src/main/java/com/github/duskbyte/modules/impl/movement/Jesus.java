package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.events.impl.TravelEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import net.minecraft.world.level.material.FluidState;

public class Jesus extends Module {

    public static final Jesus INSTANCE = new Jesus();

    private final DoubleSetting speed = doubleSetting("Speed", 0.3, 0.1, 0.8, 0.05);
    private final BoolSetting solid = boolSetting("Solid", true);

    private Jesus() {
        super("Jesus", Category.MOVEMENT);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (mc.player.isInWater() || mc.player.isInLava()) {
            mc.player.setDeltaMovement(
                    mc.player.getDeltaMovement().x,
                    speed.getValue() * 0.5,
                    mc.player.getDeltaMovement().z
            );
            mc.player.setOnGround(true);
        }
    }

    @EventHandler
    private void onTravel(TravelEvent event) {
        if (nullCheck() || !solid.getValue()) return;

        FluidState fluidState = mc.level.getFluidState(mc.player.blockPosition());
        if (!fluidState.isEmpty()) {
            mc.player.setDeltaMovement(
                    mc.player.getDeltaMovement().x,
                    0.1,
                    mc.player.getDeltaMovement().z
            );
        }
    }

}