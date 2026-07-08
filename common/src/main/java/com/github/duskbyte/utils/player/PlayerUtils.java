package com.github.duskbyte.utils.player;

import com.github.duskbyte.utils.world.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class PlayerUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    public static boolean isEating() {
        return (mc.player.getMainHandItem().getComponents().has(DataComponents.FOOD) || mc.player.getOffhandItem().getComponents().has(DataComponents.FOOD)) && mc.player.isUsingItem();
    }

    public static boolean isInWeb() {
        return anyBlockInAABB(mc.player.getBoundingBox().deflate(1.0E-6),
                pos -> mc.level.getBlockState(pos).getBlock() instanceof WebBlock);
    }

    public static boolean isInBlock() {
        return anyBlockInAABB(mc.player.getBoundingBox().deflate(1.0E-6),
                pos -> BlockUtils.isSolidBlock(pos));
    }

    private static boolean anyBlockInAABB(AABB box, Predicate<BlockPos> predicate) {
        int minX = Mth.floor(box.minX);
        int minY = Mth.floor(box.minY);
        int minZ = Mth.floor(box.minZ);
        int maxX = Mth.floor(box.maxX);
        int maxY = Mth.floor(box.maxY);
        int maxZ = Mth.floor(box.maxZ);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mutablePos.set(x, y, z);
                    if (predicate.test(mutablePos)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
