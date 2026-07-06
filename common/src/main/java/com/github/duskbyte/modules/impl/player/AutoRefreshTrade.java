package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AutoRefreshTrade extends Module {

    public static final AutoRefreshTrade INSTANCE = new AutoRefreshTrade();

    private final IntSetting range = intSetting("Range", 4, 1, 6, 1);
    private final IntSetting delay = intSetting("Delay", 5, 0, 20, 1);

    private int refreshDelay = 0;

    private AutoRefreshTrade() {
        super("Auto Refresh Trade", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        refreshDelay = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (refreshDelay > 0) {
            refreshDelay--;
            return;
        }

        if (!hasEmerald()) return;

        for (Villager villager : mc.level.getEntitiesOfClass(Villager.class,
                mc.player.getBoundingBox().inflate(range.getValue()))) {

            if (!villager.isAlive()) continue;

            BlockPos workstationPos = findWorkstation(villager.blockPosition());
            if (workstationPos == null) continue;

            mc.gameMode.startDestroyBlock(workstationPos, net.minecraft.core.Direction.UP);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);

            refreshDelay = delay.getValue();
            return;
        }
    }

    private boolean hasEmerald() {
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getItem(i).is(Items.EMERALD)) return true;
        }
        return false;
    }

    private BlockPos findWorkstation(BlockPos villagerPos) {
        int searchRadius = 3;
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = villagerPos.offset(x, y, z);
                    BlockState state = mc.level.getBlockState(checkPos);
                    if (state.is(Blocks.LECTERN)
                            || state.is(Blocks.CARTOGRAPHY_TABLE)
                            || state.is(Blocks.FLETCHING_TABLE)
                            || state.is(Blocks.SMITHING_TABLE)
                            || state.is(Blocks.STONECUTTER)
                            || state.is(Blocks.GRINDSTONE)
                            || state.is(Blocks.LOOM)
                            || state.is(Blocks.BARREL)
                            || state.is(Blocks.SMOKER)
                            || state.is(Blocks.BLAST_FURNACE)
                            || state.is(Blocks.BREWING_STAND)
                            || state.is(Blocks.CAULDRON)
                            || state.is(Blocks.COMPOSTER)) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }
}
