package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.basic;

import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.FluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BasicFluidPipeBlockEntity extends FluidPipeBlockEntity {

    public static final int BASIC_MAX_TRANSFER = 1_000; // mB/t

    public BasicFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.BasicFluidPipeBE.get(), pos, state, BASIC_MAX_TRANSFER, 1);
    }
}
