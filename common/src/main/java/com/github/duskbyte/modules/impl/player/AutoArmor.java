package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import com.github.duskbyte.utils.player.InvHelper;
import com.github.duskbyte.utils.player.MoveUtils;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class AutoArmor extends Module {

    public static final AutoArmor INSTANCE = new AutoArmor();

    private final IntSetting delay = intSetting("Delay", 150, 0, 500, 10);
    private final BoolSetting onlyWhileNotMoving = boolSetting("Stop when moving", false);
    private final BoolSetting invOnly = boolSetting("Inventory only", false);

    private long lastEquipTime = 0;

    private AutoArmor() {
        super("Auto Armor", Category.PLAYER);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (invOnly.getValue() && !(mc.screen instanceof InventoryScreen)) return;
        if (onlyWhileNotMoving.getValue() && MoveUtils.isMoving()) return;

        if (System.currentTimeMillis() - lastEquipTime < delay.getValue()) return;

        EquipmentSlot[] armorSlots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
        for (int i = 0; i < 4; i++) {
            int containerSlot = 4 + (4 - i);
            ItemStack current = mc.player.inventoryMenu.getSlot(containerSlot).getItem();
            if (current.isEmpty() || !current.is(ItemTags.ARMOR_ENCHANTABLE)) continue;

            Equippable equipment = current.get(DataComponents.EQUIPPABLE);
            if (equipment == null) continue;

            // Find best piece for this slot
            int bestSlot = -1;
            float bestScore = -1;

            for (int j = 9; j < 45; j++) {
                ItemStack stack = mc.player.inventoryMenu.getSlot(j).getItem();
                if (stack.isEmpty() || !stack.is(ItemTags.ARMOR_ENCHANTABLE)) continue;
                Equippable eq = stack.get(DataComponents.EQUIPPABLE);
                if (eq == null || eq.slot() != armorSlots[i]) continue;

                float score = InvHelper.getProtection(stack);
                if (score > bestScore) {
                    bestScore = score;
                    bestSlot = j;
                }
            }

            if (bestSlot != -1) {
                if (bestSlot < 9) bestSlot += 36;
                mc.gameMode.handleContainerInput(
                        mc.player.inventoryMenu.containerId, bestSlot, 0,
                        net.minecraft.world.inventory.ContainerInput.QUICK_MOVE, mc.player
                );
                lastEquipTime = System.currentTimeMillis();
                return;
            }
        }
    }

}