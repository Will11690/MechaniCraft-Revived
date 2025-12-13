package com.github.will11690.mechanicraft_revived.blocks.transport.energy.basic;

import com.github.will11690.mechanicraft_revived.blocks.transport.energy.EnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BasicEnergyPipeBlockEntity extends EnergyPipeBlockEntity {

    public static final int BASIC_MAX_TRANSFER = 1_024; // FE/t

    public BasicEnergyPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.BasicEnergyPipeBE.get(), pos, state, BASIC_MAX_TRANSFER, 1);
    }
}
