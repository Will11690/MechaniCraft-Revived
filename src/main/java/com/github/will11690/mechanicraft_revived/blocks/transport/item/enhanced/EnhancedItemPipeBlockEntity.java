package com.github.will11690.mechanicraft_revived.blocks.transport.item.enhanced;

import com.github.will11690.mechanicraft_revived.blocks.transport.item.ItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EnhancedItemPipeBlockEntity extends ItemPipeBlockEntity {

    public static final int ENHANCED_MAX_TRANSFER = 512; // items per tick

    public EnhancedItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.EnhancedItemPipeBE.get(), pos, state, ENHANCED_MAX_TRANSFER, 2);
    }
}
