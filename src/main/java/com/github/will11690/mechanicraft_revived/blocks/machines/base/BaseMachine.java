package com.github.will11690.mechanicraft_revived.blocks.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/**
 * Generic machine block:
 * - horizontal facing
 * - lit property
 * - opens BE screen on use
 */
public abstract class BaseMachine extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected BaseMachine(Properties props) {

        super(props);
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.FALSE));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {

        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {

        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {

        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(LIT, Boolean.FALSE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {

        b.add(FACING, LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.MODEL;
    }

    /** Drop contents if block is broken/replaced. */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if (state.getBlock() != newState.getBlock()) {

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BaseMachineBlockEntity machineBE) {

                machineBE.dropInventory(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /** Open screen. */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BaseMachineBlockEntity machineBE) {

                NetworkHooks.openScreen((ServerPlayer) player, machineBE, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Standard server ticker. */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        if (level.isClientSide) return null;

        return (lvl, p, st, be) -> {

            if (be instanceof BaseMachineBlockEntity machineBE) {
                machineBE.tickServer();
            }
        };
    }
}