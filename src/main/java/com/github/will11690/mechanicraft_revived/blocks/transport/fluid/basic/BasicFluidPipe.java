package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.basic;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipe;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BasicFluidPipe extends BasePipe {

    public BasicFluidPipe(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return MechaniCraftBlockEntities.BasicFluidPipeBE.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                  BlockState state,
                                                                  BlockEntityType<T> type) {

        if (level.isClientSide()) {
            return null;
        }

        if (type == MechaniCraftBlockEntities.BasicFluidPipeBE.get()) {

            return (lvl, pos, st, be) -> {
                if (be instanceof com.github.will11690.mechanicraft_revived.blocks.transport.fluid.basic.BasicFluidPipeBlockEntity pipe) {
                    pipe.serverTick();
                }
            };
        }

        return null;
    }
}