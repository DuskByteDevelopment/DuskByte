package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import com.github.duskbyte.utils.player.MoveUtils;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChestStealer extends Module {

    public static final ChestStealer INSTANCE = new ChestStealer();

    private final IntSetting delay = intSetting("Delay", 80, 0, 300, 10);
    private final BoolSetting smart = boolSetting("Smart", false);
    private final BoolSetting reverse = boolSetting("Reverse", false);

    private long lastClickTime = 0;

    private ChestStealer() {
        super("Chest Stealer", Category.PLAYER);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (!(mc.player.containerMenu instanceof ChestMenu chestMenu)) return;

        if (System.currentTimeMillis() - lastClickTime < delay.getValue()) return;

        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < chestMenu.getItems().size(); i++) {
            ItemStack stack = chestMenu.getItems().get(i);
            if (!stack.isEmpty()) {
                slots.add(i);
            }
        }

        if (reverse.getValue()) {
            Collections.reverse(slots);
        }

        for (int slot : slots) {
            ItemStack stack = chestMenu.getItems().get(slot);
            if (smart.getValue() && !isUsefulItem(stack)) continue;

            mc.gameMode.handleContainerInput(
                    chestMenu.containerId, slot, 0,
                    ContainerInput.QUICK_MOVE, mc.player
            );
            lastClickTime = System.currentTimeMillis();
            return;
        }
    }

    private boolean isUsefulItem(ItemStack stack) {
        return !stack.isEmpty();
    }

}