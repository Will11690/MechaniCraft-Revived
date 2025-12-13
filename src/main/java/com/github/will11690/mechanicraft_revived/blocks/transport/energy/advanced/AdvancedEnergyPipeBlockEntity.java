package com.github.will11690.mechanicraft_revived.blocks.transport.energy.advanced;

import com.github.will11690.mechanicraft_revived.blocks.transport.energy.EnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedEnergyPipeBlockEntity extends EnergyPipeBlockEntity {

    public static final int ADVANCED_MAX_TRANSFER = 16_384; // FE/t

    public AdvancedEnergyPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.AdvancedEnergyPipeBE.get(), pos, state, ADVANCED_MAX_TRANSFER, 3);
    }
}
