package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.advanced;

import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.FluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedFluidPipeBlockEntity extends FluidPipeBlockEntity {

    public static final int ADVANCED_MAX_TRANSFER = 16000; // mB/t

    public AdvancedFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.AdvancedFluidPipeBE.get(), pos, state, ADVANCED_MAX_TRANSFER, 3);
    }
}
