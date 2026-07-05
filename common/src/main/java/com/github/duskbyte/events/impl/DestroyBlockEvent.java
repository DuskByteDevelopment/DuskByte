package com.github.duskbyte.events.impl;

import com.github.duskbyte.events.Cancellable;
import net.minecraft.core.BlockPos;

public class DestroyBlockEvent extends Cancellable {

    private final BlockPos pos;

    public DestroyBlockEvent(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

}
