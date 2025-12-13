package com.github.will11690.mechanicraft_revived.blocks.transport.base.block;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.PipeNetworkManager;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class BasePipe extends Block implements EntityBlock {

    // BLOCK CONNECTION BOOLS (pipe → block)
    public static final BooleanProperty BLOCK_UP    = BooleanProperty.create("block_up");
    public static final BooleanProperty BLOCK_DOWN  = BooleanProperty.create("block_down");
    public static final BooleanProperty BLOCK_NORTH = BooleanProperty.create("block_north");
    public static final BooleanProperty BLOCK_SOUTH = BooleanProperty.create("block_south");
    public static final BooleanProperty BLOCK_WEST  = BooleanProperty.create("block_west");
    public static final BooleanProperty BLOCK_EAST  = BooleanProperty.create("block_east");

    // PIPE CONNECTION BOOLS (pipe → pipe)
    public static final BooleanProperty PIPE_UP    = BooleanProperty.create("pipe_up");
    public static final BooleanProperty PIPE_DOWN  = BooleanProperty.create("pipe_down");
    public static final BooleanProperty PIPE_NORTH = BooleanProperty.create("pipe_north");
    public static final BooleanProperty PIPE_SOUTH = BooleanProperty.create("pipe_south");
    public static final BooleanProperty PIPE_WEST  = BooleanProperty.create("pipe_west");
    public static final BooleanProperty PIPE_EAST  = BooleanProperty.create("pipe_east");

    // VISUAL CORE SHAPE
    private static final VoxelShape CORE_SHAPE = Block.box(6, 6, 6, 10, 10, 10);

    // PIPE CONNECTION SHAPES
    private static final VoxelShape DOWN_SHAPE_PIPE  = Block.box(6, 0, 6, 10, 6, 10);
    private static final VoxelShape UP_SHAPE_PIPE    = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape NORTH_SHAPE_PIPE = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SOUTH_SHAPE_PIPE = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape EAST_SHAPE_PIPE  = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape WEST_SHAPE_PIPE  = Block.box(0, 6, 6, 6, 10, 10);

    // BLOCK CONNECTION SHAPES
    private static final VoxelShape DOWN_SHAPE_BLOCK = Shapes.join(
            Block.box(6, 3, 6, 10, 6, 10),
            Block.box(5, 0, 5, 11, 3, 11),
            BooleanOp.OR
    );
    private static final VoxelShape UP_SHAPE_BLOCK = Shapes.join(
            Block.box(6, 10, 6, 10, 13, 10),
            Block.box(5, 13, 5, 11, 16, 11),
            BooleanOp.OR
    );
    private static final VoxelShape NORTH_SHAPE_BLOCK = Shapes.join(
            Block.box(6, 6, 3, 10, 10, 6),
            Block.box(5, 5, 0, 11, 11, 3),
            BooleanOp.OR
    );
    private static final VoxelShape SOUTH_SHAPE_BLOCK = Shapes.join(
            Block.box(6, 6, 10, 10, 10, 13),
            Block.box(5, 5, 13, 11, 11, 16),
            BooleanOp.OR
    );
    private static final VoxelShape EAST_SHAPE_BLOCK = Shapes.join(
            Block.box(10, 6, 6, 13, 10, 10),
            Block.box(13, 5, 5, 16, 11, 11),
            BooleanOp.OR
    );
    private static final VoxelShape WEST_SHAPE_BLOCK = Shapes.join(
            Block.box(3, 6, 6, 6, 10, 10),
            Block.box(0, 5, 5, 3, 11, 11),
            BooleanOp.OR
    );

    protected BasePipe(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BLOCK_DOWN,  false).setValue(BLOCK_UP,    false)
                .setValue(BLOCK_NORTH, false).setValue(BLOCK_SOUTH, false)
                .setValue(BLOCK_WEST,  false).setValue(BLOCK_EAST,  false)
                .setValue(PIPE_DOWN,   false).setValue(PIPE_UP,     false)
                .setValue(PIPE_NORTH,  false).setValue(PIPE_SOUTH,  false)
                .setValue(PIPE_WEST,   false).setValue(PIPE_EAST,   false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(BLOCK_DOWN, BLOCK_UP, BLOCK_NORTH, BLOCK_SOUTH, BLOCK_WEST, BLOCK_EAST,
                    PIPE_DOWN,  PIPE_UP,  PIPE_NORTH,  PIPE_SOUTH,  PIPE_WEST,  PIPE_EAST);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    /* --------------------------------------------------------------------- */
    /* Connection checks                                                     */
    /* --------------------------------------------------------------------- */

    public static boolean canPipeConnectPipe(BlockGetter level, BlockPos pos, Direction dir) {

        BlockEntity selfBE = level.getBlockEntity(pos);
        if (!(selfBE instanceof BasePipeBlockEntity selfPipe)) return false;

        if (!selfPipe.isSideVisuallyEnabled(dir)) return false;

        BlockPos neighborPos = pos.relative(dir);
        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
        if (!(neighborBE instanceof BasePipeBlockEntity otherPipe)) return false;

        if (selfPipe.getPipeType() != otherPipe.getPipeType()) return false;

        if (selfPipe.getTierIndex() != otherPipe.getTierIndex()) return false;

        if (!otherPipe.isSideVisuallyEnabled(dir.getOpposite())) return false;

        return level.getBlockState(neighborPos).getBlock() instanceof BasePipe;
    }

    public static boolean canPipeConnectBlock(BlockGetter level, BlockPos pos, Direction dir) {

        BlockEntity selfBE = level.getBlockEntity(pos);
        if (!(selfBE instanceof BasePipeBlockEntity selfPipe)) return false;

        if (!selfPipe.isSideVisuallyEnabled(dir)) return false;

        BlockPos neighborPos = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighborPos);
        BlockEntity neighborBE = level.getBlockEntity(neighborPos);

        if (neighborBE instanceof BasePipeBlockEntity otherPipe &&
                neighborState.getBlock() instanceof BasePipe) {

            if (selfPipe.getPipeType() != otherPipe.getPipeType()) return false;

            if (selfPipe.getTierIndex() == otherPipe.getTierIndex()) return false;

            return true;
        }

        return selfPipe.canConnectToBlock(dir);
    }

    /* --------------------------------------------------------------------- */
    /* VISUAL Shape                                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

        VoxelShape shape = CORE_SHAPE;

        if (state.getValue(PIPE_DOWN))  shape = Shapes.join(shape, DOWN_SHAPE_PIPE, BooleanOp.OR);
        if (state.getValue(BLOCK_DOWN)) shape = Shapes.join(shape, DOWN_SHAPE_BLOCK, BooleanOp.OR);

        if (state.getValue(PIPE_UP))    shape = Shapes.join(shape, UP_SHAPE_PIPE, BooleanOp.OR);
        if (state.getValue(BLOCK_UP))   shape = Shapes.join(shape, UP_SHAPE_BLOCK, BooleanOp.OR);

        if (state.getValue(PIPE_NORTH))  shape = Shapes.join(shape, NORTH_SHAPE_PIPE, BooleanOp.OR);
        if (state.getValue(BLOCK_NORTH)) shape = Shapes.join(shape, NORTH_SHAPE_BLOCK, BooleanOp.OR);

        if (state.getValue(PIPE_SOUTH))  shape = Shapes.join(shape, SOUTH_SHAPE_PIPE, BooleanOp.OR);
        if (state.getValue(BLOCK_SOUTH)) shape = Shapes.join(shape, SOUTH_SHAPE_BLOCK, BooleanOp.OR);

        if (state.getValue(PIPE_WEST))  shape = Shapes.join(shape, WEST_SHAPE_PIPE, BooleanOp.OR);
        if (state.getValue(BLOCK_WEST)) shape = Shapes.join(shape, WEST_SHAPE_BLOCK, BooleanOp.OR);

        if (state.getValue(PIPE_EAST))  shape = Shapes.join(shape, EAST_SHAPE_PIPE, BooleanOp.OR);
        if (state.getValue(BLOCK_EAST)) shape = Shapes.join(shape, EAST_SHAPE_BLOCK, BooleanOp.OR);

        return shape;
    }

    /* --------------------------------------------------------------------- */
    /* CONDITIONAL Collision Shape                                           */
    /* --------------------------------------------------------------------- */

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {

        VoxelShape actual = getShape(state, level, pos, ctx);
        VoxelShape groundedPost = Block.box(6, 0, 6, 10, 6, 10);

        if (ctx == CollisionContext.empty()) {

            return Shapes.or(actual, groundedPost);
        }

        if (ctx instanceof EntityCollisionContext ecc) {

            Entity e = ecc.getEntity();

            if (e instanceof Player || !(e instanceof Mob)) {

                return actual;
            }

            return Shapes.or(actual, groundedPost);
        }

        return Shapes.or(actual, groundedPost);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {

        return Shapes.block();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {

        return type == PathComputationType.LAND;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {

        return BlockPathTypes.WALKABLE;
    }

    /* --------------------------------------------------------------------- */
    /* Blockstate connection flags                                           */
    /* --------------------------------------------------------------------- */

    private BlockState setConnections(BlockGetter world, BlockPos pos, BlockState state) {

        boolean blockUp    = canPipeConnectBlock(world, pos, Direction.UP);
        boolean blockDown  = canPipeConnectBlock(world, pos, Direction.DOWN);
        boolean blockNorth = canPipeConnectBlock(world, pos, Direction.NORTH);
        boolean blockSouth = canPipeConnectBlock(world, pos, Direction.SOUTH);
        boolean blockWest  = canPipeConnectBlock(world, pos, Direction.WEST);
        boolean blockEast  = canPipeConnectBlock(world, pos, Direction.EAST);

        boolean pipeUp    = canPipeConnectPipe(world, pos, Direction.UP);
        boolean pipeDown  = canPipeConnectPipe(world, pos, Direction.DOWN);
        boolean pipeNorth = canPipeConnectPipe(world, pos, Direction.NORTH);
        boolean pipeSouth = canPipeConnectPipe(world, pos, Direction.SOUTH);
        boolean pipeWest  = canPipeConnectPipe(world, pos, Direction.WEST);
        boolean pipeEast  = canPipeConnectPipe(world, pos, Direction.EAST);

        return state
                .setValue(BLOCK_UP,    blockUp).setValue(BLOCK_DOWN,  blockDown)
                .setValue(BLOCK_NORTH, blockNorth).setValue(BLOCK_SOUTH, blockSouth)
                .setValue(BLOCK_WEST,  blockWest).setValue(BLOCK_EAST,  blockEast)
                .setValue(PIPE_UP,     pipeUp).setValue(PIPE_DOWN,   pipeDown)
                .setValue(PIPE_NORTH,  pipeNorth).setValue(PIPE_SOUTH,  pipeSouth)
                .setValue(PIPE_WEST,   pipeWest).setValue(PIPE_EAST,   pipeEast);
    }

    public BlockState updateConnections(BlockState state, Level level, BlockPos pos) {

        BlockState newState = setConnections(level, pos, state);

        if (!level.isClientSide && newState != state) {

            level.setBlock(pos, newState, Block.UPDATE_ALL);

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BasePipeBlockEntity pipeBE) {

                PipeNetworkManager.get(level).markDirty(pipeBE.getPipeType());
            }
        }

        return newState;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {

        super.onPlace(state, level, pos, oldState, moving);
        if (!level.isClientSide) {

            BlockState newState = setConnections(level, pos, state);
            if (newState != state) {

                level.setBlock(pos, newState, Block.UPDATE_ALL);
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BasePipeBlockEntity pipeBE) {

                PipeNetworkManager.get(level).markDirty(pipeBE.getPipeType());
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {

        if (world instanceof Level level && !level.isClientSide) {

            return setConnections(level, currentPos, state);
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);

        if (!level.isClientSide) {

            BlockState newState = setConnections(level, pos, state);
            if (newState != state) {

                level.setBlock(pos, newState, Block.UPDATE_ALL);

                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BasePipeBlockEntity pipeBE) {

                    PipeNetworkManager.get(level).markDirty(pipeBE.getPipeType());
                }
            }
        }
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {

        return 1.0F;
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {

        return false;
    }

    /* --------------------------------------------------------------------- */
    /* Interaction: Shift-right-click anywhere on arm toggles that side      */
    /* --------------------------------------------------------------------- */

    @Override
    public InteractionResult use(BlockState state,
                                 Level level,
                                 BlockPos pos,
                                 Player player,
                                 InteractionHand hand,
                                 BlockHitResult hit) {

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BasePipeBlockEntity pipe)) {
            return InteractionResult.PASS;
        }

        // Primary use: open config GUI
        if (!player.isSecondaryUseActive()) {

            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

                NetworkHooks.openScreen(serverPlayer, pipe, buf -> {
                    buf.writeBlockPos(pos);
                    // You can also write additional info here, e.g. selected side.
                });
            }

            // Tell MC the interaction was handled on this side
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Secondary use (sneak / "secondary use"): toggle side enable/disable
        Direction side = getClickedArmSide(pos, hit);
        if (side == null) {

            side = hit.getDirection();
        }

        if (!level.isClientSide()) {

            PipeSideConfig cfg = pipe.getSideConfig(side);
            if (cfg == null) cfg = new PipeSideConfig();

            cfg.ioMode = (cfg.ioMode == IOMode.DISABLED) ? IOMode.BOTH : IOMode.DISABLED;

            pipe.setSideConfig(side, cfg);
            pipe.setChanged();
            pipe.refreshConnections();

            PipeNetworkManager.get(level).markDirty(pipe.getPipeType());

            level.sendBlockUpdated(pos, state, level.getBlockState(pos), Block.UPDATE_ALL);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }


    @Nullable
    private static Direction getClickedArmSide(BlockPos pos, BlockHitResult hit) {
        Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());

        if (isPointInShape(DOWN_SHAPE_PIPE, local)  || isPointInShape(DOWN_SHAPE_BLOCK, local))  return Direction.DOWN;
        if (isPointInShape(UP_SHAPE_PIPE, local)    || isPointInShape(UP_SHAPE_BLOCK, local))    return Direction.UP;
        if (isPointInShape(NORTH_SHAPE_PIPE, local) || isPointInShape(NORTH_SHAPE_BLOCK, local)) return Direction.NORTH;
        if (isPointInShape(SOUTH_SHAPE_PIPE, local) || isPointInShape(SOUTH_SHAPE_BLOCK, local)) return Direction.SOUTH;
        if (isPointInShape(WEST_SHAPE_PIPE, local)  || isPointInShape(WEST_SHAPE_BLOCK, local))  return Direction.WEST;
        if (isPointInShape(EAST_SHAPE_PIPE, local)  || isPointInShape(EAST_SHAPE_BLOCK, local))  return Direction.EAST;

        return null;
    }

    private static boolean isPointInShape(VoxelShape shape, Vec3 local) {

        final double eps = 1.0e-4;
        double px = local.x, py = local.y, pz = local.z;

        for (AABB aabb : shape.toAabbs()) {

            if (px >= aabb.minX - eps && px <= aabb.maxX + eps &&
                py >= aabb.minY - eps && py <= aabb.maxY + eps &&
                pz >= aabb.minZ - eps && pz <= aabb.maxZ + eps) {

                return true;
            }
        }
        return false;
    }
}