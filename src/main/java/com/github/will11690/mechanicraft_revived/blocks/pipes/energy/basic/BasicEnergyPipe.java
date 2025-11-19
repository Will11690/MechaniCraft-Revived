package com.github.will11690.mechanicraft_revived.blocks.pipes.energy.basic;

import com.github.will11690.mechanicraft_revived.blocks.pipes.base.BasePipe;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BasicEnergyPipe extends BasePipe {

    public BasicEnergyPipe(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return MechaniCraftBlockEntities.BasicEnergyPipeBE.get().create(pos, state);
    }
}