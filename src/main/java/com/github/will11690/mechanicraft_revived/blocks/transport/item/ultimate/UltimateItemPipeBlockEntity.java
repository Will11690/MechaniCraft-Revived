package com.github.will11690.mechanicraft_revived.blocks.transport.item.ultimate;

import com.github.will11690.mechanicraft_revived.blocks.transport.item.ItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class UltimateItemPipeBlockEntity extends ItemPipeBlockEntity {

    public static final int ULTIMATE_MAX_TRANSFER = 131072; // items per tick

    public UltimateItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.UltimateItemPipeBE.get(), pos, state, ULTIMATE_MAX_TRANSFER, 6);
    }
}
