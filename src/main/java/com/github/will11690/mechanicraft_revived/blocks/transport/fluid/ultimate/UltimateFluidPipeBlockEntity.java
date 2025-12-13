package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.ultimate;

import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.FluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class UltimateFluidPipeBlockEntity extends FluidPipeBlockEntity {

    public static final int ULTIMATE_MAX_TRANSFER = 1024000; // mB/t

    public UltimateFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.UltimateFluidPipeBE.get(), pos, state, ULTIMATE_MAX_TRANSFER, 6);
    }
}
