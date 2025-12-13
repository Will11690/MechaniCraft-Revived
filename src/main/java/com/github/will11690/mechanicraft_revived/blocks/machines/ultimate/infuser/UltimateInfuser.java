package com.github.will11690.mechanicraft_revived.blocks.machines.ultimate.infuser;

import com.github.will11690.mechanicraft_revived.blocks.machines.base.BaseMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class UltimateInfuser extends BaseMachine {

    public UltimateInfuser(Properties props) {

        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new UltimateInfuserBlockEntity(pos, state);
    }
}