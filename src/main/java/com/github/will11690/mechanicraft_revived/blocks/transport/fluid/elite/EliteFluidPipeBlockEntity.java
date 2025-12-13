package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.elite;

import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.FluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteFluidPipeBlockEntity extends FluidPipeBlockEntity {

    public static final int ELITE_MAX_TRANSFER = 64000; // mB/t

    public EliteFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.EliteFluidPipeBE.get(), pos, state, ELITE_MAX_TRANSFER, 4);
    }
}
