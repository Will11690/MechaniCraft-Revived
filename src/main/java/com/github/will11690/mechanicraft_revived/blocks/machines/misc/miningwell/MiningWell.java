package com.github.will11690.mechanicraft_revived.blocks.machines.misc.miningwell;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public class MiningWell extends BaseEntityBlock {

    public enum WellState implements StringRepresentable {

        WORKING("working"), COMPLETE("complete");

        private final String name;

        WellState(String name) {

            this.name = name;
        }

        @Override
        public String getSerializedName() {

            return name;
        }
    }

    public static final EnumProperty<WellState> STATE = EnumProperty.create("state", WellState.class);

    public MiningWell(Properties properties) {

        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(STATE);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext ctx) {

        return this.defaultBlockState().setValue(STATE, WellState.WORKING);
    }

    /* BLOCK ENTITY METHODS */

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new MiningWellBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        if(level.isClientSide()) {

            return null;
        }

        return createTickerHelper(type, MechaniCraftBlockEntities.MiningWellBE.get(),
                (pLevel, pPos, pState, pBlockEntity) -> pBlockEntity.serverTick());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.MODEL;
    }
}