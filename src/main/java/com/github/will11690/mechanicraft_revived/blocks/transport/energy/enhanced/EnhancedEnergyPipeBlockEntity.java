package com.github.will11690.mechanicraft_revived.blocks.transport.energy.enhanced;

import com.github.will11690.mechanicraft_revived.blocks.transport.energy.EnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EnhancedEnergyPipeBlockEntity extends EnergyPipeBlockEntity {

    public static final int ENHANCED_MAX_TRANSFER = 4_096; // FE/t

    public EnhancedEnergyPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.EnhancedEnergyPipeBE.get(), pos, state, ENHANCED_MAX_TRANSFER, 2);
    }
}
