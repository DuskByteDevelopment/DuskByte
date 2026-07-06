package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class AutoCity extends Module {

    public static final AutoCity INSTANCE = new AutoCity();

    private final DoubleSetting range = doubleSetting("Range", 4.0, 1.0, 6.0, 0.5);
    private final IntSetting delay = intSetting("Delay", 0, 0, 10, 1);
    private final BoolSetting rotate = boolSetting("Rotate", true);
    private final BoolSetting packet = boolSetting("Packet", false);
    private final BoolSetting citySelf = boolSetting("City Self", false);

    private int breakDelay = 0;
    private BlockPos targetPos = null;

    private AutoCity() {
        super("Auto City", Category.COMBAT);
    }

    @Override
    protected void onDisable() {
        breakDelay = 0;
        targetPos = null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (breakDelay > 0) {
            breakDelay--;
            return;
        }

        if (mc.player.horizontalCollision || !mc.player.onGround()) {
            findAndBreak();
        }
    }

    private void findAndBreak() {
        BlockPos playerPos = mc.player.blockPosition();
        Set<BlockPos> targets = new HashSet<>();

        if (citySelf.getValue()) {
            addTargetBlocks(playerPos, targets);
        }

        for (Player player : mc.level.players()) {
            if (player == mc.player) continue;
            if (player.distanceTo(mc.player) > range.getValue()) continue;

            BlockPos targetPlayerPos = player.blockPosition();
            addTargetBlocks(targetPlayerPos, targets);
        }

        for (BlockPos pos : targets) {
            BlockState state = mc.level.getBlockState(pos);
            if (state.is(Blocks.OBSIDIAN) || state.is(Blocks.BEDROCK)
                    || state.is(Blocks.ENDER_CHEST) || state.is(Blocks.CRYING_OBSIDIAN)
                    || state.is(Blocks.RESPAWN_ANCHOR)) {
                breakBlock(pos);
                return;
            }
        }
    }

    private void addTargetBlocks(BlockPos basePos, Set<BlockPos> targets) {
        int[][] offsets = {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
        for (int[] offset : offsets) {
            targets.add(basePos.offset(offset[0], offset[1], offset[2]));
            targets.add(basePos.offset(offset[0], 1, offset[2]));
        }
    }

    private void breakBlock(BlockPos pos) {
        if (packet.getValue()) {
            mc.gameMode.continueDestroyBlock(pos, net.minecraft.core.Direction.UP);
            mc.gameMode.stopDestroyBlock();
            mc.gameMode.startDestroyBlock(pos, net.minecraft.core.Direction.UP);
        } else {
            mc.gameMode.startDestroyBlock(pos, net.minecraft.core.Direction.UP);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
        breakDelay = delay.getValue();
    }
}
