package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.enhanced;

import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.FluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EnhancedFluidPipeBlockEntity extends FluidPipeBlockEntity {

    public static final int ENHANCED_MAX_TRANSFER = 4_000; // mB/t

    public EnhancedFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.EnhancedFluidPipeBE.get(), pos, state, ENHANCED_MAX_TRANSFER, 2);
    }
}
