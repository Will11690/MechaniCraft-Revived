package com.github.will11690.mechanicraft_revived.blocks.machines.superior.infuser;

import com.github.will11690.mechanicraft_revived.blocks.machines.base.BaseMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SuperiorInfuser extends BaseMachine {

    public SuperiorInfuser(Properties props) {

        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new SuperiorInfuserBlockEntity(pos, state);
    }
}