package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AutoPlaceBlock extends Module {

    public static final AutoPlaceBlock INSTANCE = new AutoPlaceBlock();

    private enum PlaceMode {
        LookingAt,
        Feet,
        Above
    }

    private final EnumSetting<PlaceMode> placeMode = enumSetting("Place Mode", PlaceMode.LookingAt);
    private final DoubleSetting placeRange = doubleSetting("Place Range", 4.5, 1.0, 6.0, 0.1);
    private final IntSetting delay = intSetting("Delay", 1, 0, 10, 1);
    private final BoolSetting autoSwitch = boolSetting("Auto Switch", true);
    private final BoolSetting rotate = boolSetting("Rotate", true);
    private final BoolSetting swingHand = boolSetting("Swing Hand", true);

    private int placeDelay = 0;

    private AutoPlaceBlock() {
        super("Auto Place Block", Category.PLAYER);
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

        if (!hasBlock()) return;

        BlockPos targetPos = findTargetPosition();
        if (targetPos == null) return;

        BlockState state = mc.level.getBlockState(targetPos);
        if (!state.isAir() && !state.canBeReplaced()) return;

        placeBlock(targetPos);
    }

    private boolean hasBlock() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).getItem() instanceof BlockItem) return true;
        }
        return false;
    }

    private BlockPos findTargetPosition() {
        return switch (placeMode.getValue()) {
            case LookingAt -> {
                if (mc.hitResult instanceof BlockHitResult hitResult) {
                    BlockPos pos = hitResult.getBlockPos();
                    BlockState state = mc.level.getBlockState(pos);
                    if (!state.isAir() && !state.canBeReplaced()) {
                        yield pos.relative(hitResult.getDirection());
                    }
                }
                yield null;
            }
            case Feet -> mc.player.blockPosition();
            case Above -> mc.player.blockPosition().above();
        };
    }

    private void placeBlock(BlockPos pos) {
        int blockSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem) {
                blockSlot = i;
                break;
            }
        }

        if (blockSlot == -1) return;

        int prevSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(blockSlot);

        Direction placeDir = Direction.UP;
        BlockPos placeAgainst = pos.below();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos neighbor = pos.offset(x, y, z);
                    BlockState neighborState = mc.level.getBlockState(neighbor);
                    if (!neighborState.isAir() && !neighborState.canBeReplaced()) {
                        placeAgainst = neighbor;
                        int dx = neighbor.getX() - pos.getX();
                        int dy = neighbor.getY() - pos.getY();
                        int dz = neighbor.getZ() - pos.getZ();
                        if (dx > 0) placeDir = Direction.EAST;
                        else if (dx < 0) placeDir = Direction.WEST;
                        else if (dz > 0) placeDir = Direction.SOUTH;
                        else if (dz < 0) placeDir = Direction.NORTH;
                        else if (dy > 0) placeDir = Direction.UP;
                        else placeDir = Direction.DOWN;
                        break;
                    }
                }
            }
        }

        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(pos),
                placeDir.getOpposite(),
                placeAgainst,
                false
        );

        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult);

        if (swingHand.getValue()) {
            mc.player.swing(InteractionHand.MAIN_HAND);
        }

        if (!autoSwitch.getValue()) {
            mc.player.getInventory().setSelectedSlot(prevSlot);
        }

        placeDelay = delay.getValue();
    }
}
