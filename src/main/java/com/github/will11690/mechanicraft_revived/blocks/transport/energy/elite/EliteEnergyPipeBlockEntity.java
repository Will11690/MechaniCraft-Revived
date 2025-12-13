package com.github.will11690.mechanicraft_revived.blocks.transport.energy.elite;

import com.github.will11690.mechanicraft_revived.blocks.transport.energy.EnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteEnergyPipeBlockEntity extends EnergyPipeBlockEntity {

    public static final int ELITE_MAX_TRANSFER = 65_536; // FE/t

    public EliteEnergyPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.EliteEnergyPipeBE.get(), pos, state, ELITE_MAX_TRANSFER, 4);
    }
}
