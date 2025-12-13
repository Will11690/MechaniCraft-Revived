package com.github.will11690.mechanicraft_revived.blocks.transport.item.advanced;

import com.github.will11690.mechanicraft_revived.blocks.transport.item.ItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedItemPipeBlockEntity extends ItemPipeBlockEntity {

    public static final int ADVANCED_MAX_TRANSFER = 2048; // items per tick

    public AdvancedItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.AdvancedItemPipeBE.get(), pos, state, ADVANCED_MAX_TRANSFER, 3);
    }
}
