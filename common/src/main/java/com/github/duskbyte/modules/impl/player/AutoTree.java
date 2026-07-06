package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AutoTree extends Module {

    public static final AutoTree INSTANCE = new AutoTree();

    private final IntSetting range = intSetting("Range", 4, 1, 6, 1);
    private final IntSetting delay = intSetting("Delay", 5, 0, 20, 1);
    private final BoolSetting checkLight = boolSetting("Check Light", true);
    private final BoolSetting autoSwitch = boolSetting("Auto Switch", true);

    private int placeDelay = 0;

    private AutoTree() {
        super("Auto Tree", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        placeDelay = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (placeDelay > 0) {
            placeDelay--;
            return;
        }

        if (!hasSapling()) return;

        BlockPos playerPos = mc.player.blockPosition();

        for (int x = -range.getValue(); x <= range.getValue(); x++) {
            for (int z = -range.getValue(); z <= range.getValue(); z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockPos abovePos = checkPos.above();
                    BlockState groundState = mc.level.getBlockState(checkPos);
                    BlockState aboveState = mc.level.getBlockState(abovePos);

                    if (!aboveState.isAir() && !aboveState.canBeReplaced()) continue;

                    if (groundState.is(Blocks.GRASS_BLOCK) || groundState.is(Blocks.DIRT) || groundState.is(Blocks.FARMLAND)) {
                        if (checkLight.getValue()) {
                            int light = mc.level.getMaxLocalRawBrightness(abovePos);
                            if (light < 8) continue;
                        }

                        tryPlant(abovePos);
                        return;
                    }
                }
            }
        }
    }

    private boolean hasSapling() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                    && blockItem.getBlock() instanceof SaplingBlock) {
                return true;
            }
        }
        return false;
    }

    private void tryPlant(BlockPos pos) {
        int saplingSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                    && blockItem.getBlock() instanceof SaplingBlock) {
                saplingSlot = i;
                break;
            }
        }

        if (saplingSlot == -1) return;

        int prevSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(saplingSlot);

        Vec3 eyePos = mc.player.getEyePosition();
        Vec3 targetVec = Vec3.atCenterOf(pos);

        BlockHitResult hitResult = new BlockHitResult(
                targetVec,
                net.minecraft.core.Direction.UP,
                pos.below(),
                false
        );

        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult);

        if (!autoSwitch.getValue()) {
            mc.player.getInventory().setSelectedSlot(prevSlot);
        }

        placeDelay = delay.getValue();
    }
}
