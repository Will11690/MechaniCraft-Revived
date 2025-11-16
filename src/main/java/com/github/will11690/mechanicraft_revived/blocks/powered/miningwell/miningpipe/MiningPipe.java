package com.github.will11690.mechanicraft_revived.blocks.powered.miningwell.miningpipe;

import com.github.will11690.mechanicraft_revived.blocks.powered.miningwell.MiningWell;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MiningPipe extends Block {

    public MiningPipe(Properties properties) {

        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos fromPos, boolean movedByPiston) {

        super.neighborChanged(state, level, pos, neighborBlock, fromPos, movedByPiston);

        if(level.isClientSide()) {
            return;
        }

        if(!isSupported(level, pos)) {

            // No valid well/pipe above -> remove this pipe
            level.destroyBlock(pos, false);
        }
    }

    private boolean isSupported(Level level, BlockPos pos) {

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        Block aboveBlock = aboveState.getBlock();

        if(aboveBlock == MechaniCraftBlocks.MiningPipe.get()) {

            // Supported by another pipe
            return true;
        }

        if(aboveBlock == MechaniCraftBlocks.MiningWell.get()) {

            // Supported by a mining well only if it is still working
            if(aboveState.hasProperty(MiningWell.STATE)) {
                return aboveState.getValue(MiningWell.STATE) == MiningWell.WellState.WORKING;
            }
        }

        return false;
    }
}
