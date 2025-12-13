package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.superior;

import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.FluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SuperiorFluidPipeBlockEntity extends FluidPipeBlockEntity {

    public static final int SUPERIOR_MAX_TRANSFER = 256000; // mB/t

    public SuperiorFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.SuperiorFluidPipeBE.get(), pos, state, SUPERIOR_MAX_TRANSFER, 5);
    }
}
