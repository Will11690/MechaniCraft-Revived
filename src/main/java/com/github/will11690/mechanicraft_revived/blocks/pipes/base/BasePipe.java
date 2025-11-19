package com.github.will11690.mechanicraft_revived.blocks.pipes.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
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

    // BASE SHAPE (your CORE_SHAPE)
    private static final VoxelShape CORE_SHAPE =
            Block.box(6, 6, 6, 10, 10, 10);

    // PIPE CONNECTION SHAPES (old CHUTE_*_SHAPE)
    private static final VoxelShape DOWN_SHAPE_PIPE  = Block.box(6, 0, 6, 10, 6, 10);
    private static final VoxelShape UP_SHAPE_PIPE    = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape NORTH_SHAPE_PIPE = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SOUTH_SHAPE_PIPE = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape EAST_SHAPE_PIPE  = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape WEST_SHAPE_PIPE  = Block.box(0, 6, 6, 6, 10, 10);

    // BLOCK CONNECTION SHAPES (old BLOCK_*_SHAPE)
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
        builder.add(
                BLOCK_DOWN, BLOCK_UP, BLOCK_NORTH, BLOCK_SOUTH, BLOCK_WEST, BLOCK_EAST,
                PIPE_DOWN,  PIPE_UP,  PIPE_NORTH,  PIPE_SOUTH,  PIPE_WEST,  PIPE_EAST
        );
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
        if (!(selfBE instanceof BasePipeBlockEntity selfPipe)) {
            return false;
        }

        // This pipe’s side must not be disabled
        if (!selfPipe.isSideVisuallyEnabled(dir)) {
            return false;
        }

        BlockPos neighborPos = pos.relative(dir);
        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
        if (!(neighborBE instanceof BasePipeBlockEntity otherPipe)) {
            return false;
        }

        // Neighbor must be the same pipe type (ENERGY ↔ ENERGY, ITEM ↔ ITEM, etc.)
        if (selfPipe.getPipeType() != otherPipe.getPipeType()) {
            return false;
        }

        // Neighbor’s opposite side must also be visually enabled
        if (!otherPipe.isSideVisuallyEnabled(dir.getOpposite())) {
            return false;
        }

        // And it must actually be a pipe block
        BlockState neighborState = level.getBlockState(neighborPos);
        return neighborState.getBlock() instanceof BasePipe;
    }


    public static boolean canPipeConnectBlock(BlockGetter level, BlockPos pos, Direction dir) {
        BlockEntity selfBE = level.getBlockEntity(pos);
        if (!(selfBE instanceof BasePipeBlockEntity pipeBE)) {
            return false;
        }

        // Hide arm if side is disabled
        if (!pipeBE.isSideVisuallyEnabled(dir)) {
            return false;
        }

        return pipeBE.canConnectToBlock(dir);
    }

    /* --------------------------------------------------------------------- */
    /* Shape (collision/selection)                                           */
    /* --------------------------------------------------------------------- */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos,
                               CollisionContext context) {
        VoxelShape shape = CORE_SHAPE;

        // DOWN
        if (canPipeConnectPipe(world, pos, Direction.DOWN)) {
            shape = Shapes.join(shape, DOWN_SHAPE_PIPE, BooleanOp.OR);
        }
        if (canPipeConnectBlock(world, pos, Direction.DOWN)) {
            shape = Shapes.join(shape, DOWN_SHAPE_BLOCK, BooleanOp.OR);
        }

        // UP
        if (canPipeConnectPipe(world, pos, Direction.UP)) {
            shape = Shapes.join(shape, UP_SHAPE_PIPE, BooleanOp.OR);
        }
        if (canPipeConnectBlock(world, pos, Direction.UP)) {
            shape = Shapes.join(shape, UP_SHAPE_BLOCK, BooleanOp.OR);
        }

        // NORTH
        if (canPipeConnectPipe(world, pos, Direction.NORTH)) {
            shape = Shapes.join(shape, NORTH_SHAPE_PIPE, BooleanOp.OR);
        }
        if (canPipeConnectBlock(world, pos, Direction.NORTH)) {
            shape = Shapes.join(shape, NORTH_SHAPE_BLOCK, BooleanOp.OR);
        }

        // SOUTH
        if (canPipeConnectPipe(world, pos, Direction.SOUTH)) {
            shape = Shapes.join(shape, SOUTH_SHAPE_PIPE, BooleanOp.OR);
        }
        if (canPipeConnectBlock(world, pos, Direction.SOUTH)) {
            shape = Shapes.join(shape, SOUTH_SHAPE_BLOCK, BooleanOp.OR);
        }

        // WEST
        if (canPipeConnectPipe(world, pos, Direction.WEST)) {
            shape = Shapes.join(shape, WEST_SHAPE_PIPE, BooleanOp.OR);
        }
        if (canPipeConnectBlock(world, pos, Direction.WEST)) {
            shape = Shapes.join(shape, WEST_SHAPE_BLOCK, BooleanOp.OR);
        }

        // EAST
        if (canPipeConnectPipe(world, pos, Direction.EAST)) {
            shape = Shapes.join(shape, EAST_SHAPE_PIPE, BooleanOp.OR);
        }
        if (canPipeConnectBlock(world, pos, Direction.EAST)) {
            shape = Shapes.join(shape, EAST_SHAPE_BLOCK, BooleanOp.OR);
        }

        return shape;
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
                .setValue(BLOCK_UP,    blockUp)
                .setValue(BLOCK_DOWN,  blockDown)
                .setValue(BLOCK_NORTH, blockNorth)
                .setValue(BLOCK_SOUTH, blockSouth)
                .setValue(BLOCK_WEST,  blockWest)
                .setValue(BLOCK_EAST,  blockEast)
                .setValue(PIPE_UP,     pipeUp)
                .setValue(PIPE_DOWN,   pipeDown)
                .setValue(PIPE_NORTH,  pipeNorth)
                .setValue(PIPE_SOUTH,  pipeSouth)
                .setValue(PIPE_WEST,   pipeWest)
                .setValue(PIPE_EAST,   pipeEast);
    }

    public BlockState updateConnections(BlockState state, Level level, BlockPos pos) {
        BlockState newState = setConnections(level, pos, state);
        if (newState != state) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }
        return newState;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos,
                        BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        BlockState newState = setConnections(level, pos, state);
        if (newState != state) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (world instanceof Level level) {
            state = setConnections(level, currentPos, state);
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);
        BlockState newState = setConnections(level, pos, state);
        if (newState != state) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
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
    /* Interaction: Shift-right-click on face toggles DISABLED ↔ BOTH       */
    /* --------------------------------------------------------------------- */

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand,
                                          BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BasePipeBlockEntity pipe) {
                Direction face = hit.getDirection();
                pipe.toggleDisabled(face);
            }
            return InteractionResult.CONSUME;
        }

        // Regular right-click will open type-specific GUI later
        return InteractionResult.PASS;
    }
}