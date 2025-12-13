package com.github.will11690.mechanicraft_revived.blocks.transport.energy.superior;

import com.github.will11690.mechanicraft_revived.blocks.transport.energy.EnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SuperiorEnergyPipeBlockEntity extends EnergyPipeBlockEntity {

    public static final int SUPERIOR_MAX_TRANSFER = 262_144; // FE/t

    public SuperiorEnergyPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.SuperiorEnergyPipeBE.get(), pos, state, SUPERIOR_MAX_TRANSFER, 5);
    }
}
