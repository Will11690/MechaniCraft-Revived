package com.github.will11690.mechanicraft_revived.blocks.transport.base.network;

import net.minecraft.core.Direction;
import net.minecraftforge.items.IItemHandler;

public interface IFilterablePipe {

    /**
     * Returns a view of the filter inventory for the given side.
     * The handler SHOULD expose 27 slots (3x9) for that side.
     */
    IItemHandler getFilterForSide(Direction side);
}