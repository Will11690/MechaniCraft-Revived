package com.github.will11690.mechanicraft_revived.blocks.storages.basic.energycube;

import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.BaseEnergyCube;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicEnergyCube extends BaseEnergyCube {

    public BasicEnergyCube(Properties props) {
        super(props);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BasicEnergyCubeBlockEntity(pos, state);
    }
}
