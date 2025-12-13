package com.github.will11690.mechanicraft_revived.blocks.transport.energy.superior;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipe;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SuperiorEnergyPipe extends BasePipe {

    public SuperiorEnergyPipe(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return MechaniCraftBlockEntities.SuperiorEnergyPipeBE.get().create(pos, state);
    }
}