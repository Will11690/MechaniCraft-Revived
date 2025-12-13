package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.enhanced;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipe;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnhancedFluidPipe extends BasePipe {

    public EnhancedFluidPipe(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return MechaniCraftBlockEntities.EnhancedFluidPipeBE.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                  BlockState state,
                                                                  BlockEntityType<T> type) {

        if (level.isClientSide()) {
            return null;
        }

        if (type == MechaniCraftBlockEntities.EnhancedFluidPipeBE.get()) {

            return (lvl, pos, st, be) -> {
                if (be instanceof EnhancedFluidPipeBlockEntity pipe) {
                    pipe.serverTick();
                }
            };
        }

        return null;
    }
}
