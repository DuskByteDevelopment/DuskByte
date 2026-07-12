package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.utils.player.InvHelper;
import net.minecraft.world.item.Items;

public class AutoHead extends Module {

    public static final AutoHead INSTANCE = new AutoHead();

    private final DoubleSetting health = doubleSetting("Health", 10.0, 0.0, 36.0, 0.5);
    private final BoolSetting checkGapple = boolSetting("Check Gapple", true);

    private AutoHead() {
        super("Auto Head", Category.COMBAT);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > health.getValue().floatValue()) {
            return;
        }

        if (checkGapple.getValue()) {
            if (mc.player.getMainHandItem().is(Items.GOLDEN_APPLE) ||
                    mc.player.getMainHandItem().is(Items.ENCHANTED_GOLDEN_APPLE)) {
                return;
            }
        }

        if (mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;

        int headSlot = findHeadSlot();
        if (headSlot == -1) return;

        // Move head to main hand
        if (headSlot < 9) {
            headSlot += 36;
        }

        mc.gameMode.handleContainerInput(
                mc.player.inventoryMenu.containerId,
                headSlot, 0,
                net.minecraft.world.inventory.ContainerInput.PICKUP,
                mc.player
        );
        mc.gameMode.handleContainerInput(
                mc.player.inventoryMenu.containerId,
                mc.player.getInventory().getSelectedSlot() + 36, 0,
                net.minecraft.world.inventory.ContainerInput.PICKUP,
                mc.player
        );
        if (!mc.player.inventoryMenu.getCarried().isEmpty()) {
            mc.gameMode.handleContainerInput(
                    mc.player.inventoryMenu.containerId,
                    headSlot, 0,
                    net.minecraft.world.inventory.ContainerInput.PICKUP,
                    mc.player
            );
        }

        mc.options.keyUse.setDown(true);
    }

    private int findHeadSlot() {
        int slot;
        slot = InvHelper.getItemSlot(Items.PLAYER_HEAD); if (slot != -1) return slot;
        slot = InvHelper.getItemSlot(Items.CREEPER_HEAD); if (slot != -1) return slot;
        slot = InvHelper.getItemSlot(Items.ZOMBIE_HEAD); if (slot != -1) return slot;
        slot = InvHelper.getItemSlot(Items.SKELETON_SKULL); if (slot != -1) return slot;
        slot = InvHelper.getItemSlot(Items.WITHER_SKELETON_SKULL); if (slot != -1) return slot;
        slot = InvHelper.getItemSlot(Items.DRAGON_HEAD); if (slot != -1) return slot;
        slot = InvHelper.getItemSlot(Items.PIGLIN_HEAD); if (slot != -1) return slot;
        return -1;
    }

}