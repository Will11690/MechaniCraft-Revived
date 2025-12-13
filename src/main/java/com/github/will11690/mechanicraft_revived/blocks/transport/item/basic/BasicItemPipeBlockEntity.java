package com.github.will11690.mechanicraft_revived.blocks.transport.item.basic;

import com.github.will11690.mechanicraft_revived.blocks.transport.item.ItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BasicItemPipeBlockEntity extends ItemPipeBlockEntity {

    public static final int BASIC_MAX_TRANSFER = 128; // items per tick

    public BasicItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.BasicItemPipeBE.get(), pos, state, BASIC_MAX_TRANSFER, 1);
    }
}
