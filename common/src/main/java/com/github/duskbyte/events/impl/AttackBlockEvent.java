package com.github.duskbyte.events.impl;

import com.github.duskbyte.events.Cancellable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class AttackBlockEvent extends Cancellable {

    private BlockPos blockPos;
    private Direction direction;

    public AttackBlockEvent(BlockPos blockPos, Direction direction) {
        this.blockPos = blockPos;
        this.direction = direction;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

}
