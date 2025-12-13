package com.github.will11690.mechanicraft_revived.blocks.transport.item.superior;

import com.github.will11690.mechanicraft_revived.blocks.transport.item.ItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SuperiorItemPipeBlockEntity extends ItemPipeBlockEntity {

    public static final int SUPERIOR_MAX_TRANSFER = 32768; // items per tick

    public SuperiorItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.SuperiorItemPipeBE.get(), pos, state, SUPERIOR_MAX_TRANSFER, 5);
    }
}
