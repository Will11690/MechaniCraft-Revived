package com.github.will11690.mechanicraft_revived.blocks.transport.item.elite;

import com.github.will11690.mechanicraft_revived.blocks.transport.item.ItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteItemPipeBlockEntity extends ItemPipeBlockEntity {

    public static final int ELITE_MAX_TRANSFER = 8192; // items per tick

    public EliteItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.EliteItemPipeBE.get(), pos, state, ELITE_MAX_TRANSFER, 4);
    }
}
