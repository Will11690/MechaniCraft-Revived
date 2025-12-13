package com.github.will11690.mechanicraft_revived.blocks.transport.energy.ultimate;

import com.github.will11690.mechanicraft_revived.blocks.transport.energy.EnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class UltimateEnergyPipeBlockEntity extends EnergyPipeBlockEntity {

    public static final int ULTIMATE_MAX_TRANSFER = 1_048_576; // FE/t

    public UltimateEnergyPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.UltimateEnergyPipeBE.get(), pos, state, ULTIMATE_MAX_TRANSFER, 6);
    }
}
