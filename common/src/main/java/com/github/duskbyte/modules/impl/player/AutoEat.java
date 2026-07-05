package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoEat extends Module {

    public static final AutoEat INSTANCE = new AutoEat();

    private final IntSetting hunger = intSetting("Hunger", 18, 1, 20, 1);
    private final DoubleSetting saturation = doubleSetting("Saturation", 6.0, 1.0, 20.0, 0.5);
    private final IntSetting delay = intSetting("Delay", 5, 0, 20, 1);

    private int eatDelay = 0;
    private boolean eating = false;

    private AutoEat() {
        super("Auto Eat", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        if (eating) stopEating();
        eatDelay = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;
        FoodData food = mc.player.getFoodData();
        if (food == null) return;
        if (eating) {
            InteractionHand hand = mc.player.getUsedItemHand();
            ItemStack stack = mc.player.getItemInHand(hand);
            if (mc.player.isUsingItem() && stack.has(DataComponents.FOOD)) return;
            stopEating();
            return;
        }
        if (eatDelay > 0) { eatDelay--; return; }
        if (food.getFoodLevel() < hunger.getValue() || food.getSaturationLevel() < saturation.getValue().floatValue()) {
            findAndEat();
        }
    }

    private void findAndEat() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.has(DataComponents.FOOD)) {
                mc.player.getInventory().setSelectedSlot(i);
                mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                eating = true;
                eatDelay = delay.getValue();
                return;
            }
        }
    }

    private void stopEating() {
        if (eating && mc.player.isUsingItem()) mc.player.stopUsingItem();
        eating = false;
    }
}
