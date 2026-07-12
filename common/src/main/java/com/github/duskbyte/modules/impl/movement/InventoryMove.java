package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.EnumSetting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.Arrays;
import java.util.List;

public class InventoryMove extends Module {

    public static final InventoryMove INSTANCE = new InventoryMove();

    private enum Mode {
        Vanilla,
        Spoof
    }

    private final EnumSetting<Mode> mode = enumSetting("Mode", Mode.Vanilla);

    private InventoryMove() {
        super("Inventory Move", Category.MOVEMENT);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (mc.screen != null && !(mc.screen instanceof AbstractContainerScreen)) return;

        if (mc.screen != null) {
            updateMovementKeys();
        }
    }

    private void updateMovementKeys() {
        Options options = mc.options;
        List<KeyMapping> movementKeys = Arrays.asList(
                options.keyUp, options.keyDown,
                options.keyLeft, options.keyRight,
                options.keyJump
        );

        for (KeyMapping key : movementKeys) {
            key.setDown(key.isDown());
        }
    }

}