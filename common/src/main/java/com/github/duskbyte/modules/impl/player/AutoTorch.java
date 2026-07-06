package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AutoTorch extends Module {

    public static final AutoTorch INSTANCE = new AutoTorch();

    private final IntSetting lightLevel = intSetting("Light Level", 8, 0, 15, 1);
    private final DoubleSetting range = doubleSetting("Range", 4.0, 1.0, 6.0, 0.5);
    private final IntSetting delay = intSetting("Delay", 2, 0, 10, 1);
    private final BoolSetting onlyDark = boolSetting("Only Dark", true);
    private final BoolSetting placeOnSides = boolSetting("Place On Sides", true);

    private int placeDelay = 0;

    private AutoTorch() {
        super("Auto Torch", Category.PLAYER);
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

        if (!hasTorch()) return;

        BlockPos playerPos = mc.player.blockPosition();
        int light = mc.level.getMaxLocalRawBrightness(playerPos);

        if (onlyDark.getValue() && light > lightLevel.getValue()) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = playerPos.offset(x, 0, z);
                    if (mc.level.getMaxLocalRawBrightness(checkPos) < lightLevel.getValue()) {
                        tryPlaceTorch(checkPos);
                        return;
                    }
                }
            }
            return;
        }

        if (light > lightLevel.getValue()) return;
        tryPlaceTorch(playerPos);
    }

    private boolean hasTorch() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).is(Items.TORCH)) return true;
        }
        return false;
    }

    private void tryPlaceTorch(BlockPos pos) {
        for (int i = 0; i < 9; i++) {
            if (!mc.player.getInventory().getItem(i).is(Items.TORCH)) continue;

            BlockPos targetPos = pos;
            BlockState targetState = mc.level.getBlockState(targetPos);

            if (!targetState.isAir() && !targetState.canBeReplaced()) return;

            if (targetState.isAir() || targetState.canBeReplaced()) {
                BlockPos belowPos = pos.below();
                BlockState belowState = mc.level.getBlockState(belowPos);
                if (belowState.isAir() || belowState.canBeReplaced()) {
                    if (placeOnSides.getValue()) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dz == 0) continue;
                                BlockPos sidePos = pos.offset(dx, 0, dz);
                                BlockState sideState = mc.level.getBlockState(sidePos);
                                if (!sideState.isAir() && !sideState.canBeReplaced()) {
                                    tryPlace(i, pos);
                                    return;
                                }
                            }
                        }
                    }
                    return;
                }
                tryPlace(i, pos);
                return;
            }
        }
    }

    private void tryPlace(int slot, BlockPos pos) {
        int prevSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(slot);

        Vec3 eyePos = mc.player.getEyePosition();
        Vec3 targetVec = Vec3.atCenterOf(pos);

        BlockHitResult hitResult = (BlockHitResult) mc.player.pick(range.getValue(), 1.0f, false);

        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult);
        mc.player.getInventory().setSelectedSlot(prevSlot);
        placeDelay = delay.getValue();
    }
}
