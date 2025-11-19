package com.github.will11690.mechanicraft_revived.blocks.pipes.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;

public abstract class BasePipeBlockEntity extends BlockEntity {

    public enum PipeType {
        ENERGY,
        FLUID,
        ITEM,
        GAS
    }

    public enum IOMode {
        DISABLED,   // no connection / no transfer
        INPUT,      // accept from neighbor into network
        OUTPUT,     // send from network to neighbor
        BOTH        // both directions
    }

    public enum RedstoneMode {
        IGNORED,    // always active
        HIGH        // active only when powered (we can extend later)
    }

    public static class SideConfig {
        public IOMode ioMode = IOMode.BOTH;
        public RedstoneMode redstoneMode = RedstoneMode.IGNORED;
        public int priority = 0;         // 0 = lowest
        public int transferLimit = 0;    // 1..tierMaxTransfer, 0=>use tierMaxTransfer
    }

    protected final PipeType pipeType;
    public final int tierMaxTransfer;  // "units per tick" cap for this tier
    protected final EnumMap<Direction, SideConfig> sideConfigs =
            new EnumMap<>(Direction.class);

    protected BasePipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                         PipeType pipeType, int tierMaxTransfer) {
        super(type, pos, state);
        this.pipeType = pipeType;
        this.tierMaxTransfer = tierMaxTransfer;

        for (Direction dir : Direction.values()) {
            SideConfig cfg = new SideConfig();
            cfg.transferLimit = tierMaxTransfer;
            sideConfigs.put(dir, cfg);
        }
    }

    public PipeType getPipeType() {
        return pipeType;
    }

    public int getTierMaxTransfer() {
        return tierMaxTransfer;
    }

    public SideConfig getSideConfig(Direction side) {
        return sideConfigs.get(side);
    }

    public void setSideConfig(Direction side, SideConfig cfg) {
        sideConfigs.put(side, cfg);
        setChanged();
        refreshConnections();
    }

    protected int clampTransferLimit(int limit) {
        int cap = tierMaxTransfer > 0 ? tierMaxTransfer : Integer.MAX_VALUE;
        if (limit <= 0) return cap;      // 0 or negative = "use tier cap"
        return Math.min(limit, cap);
    }

    /* --------------------------------------------------------------------- */
    /* IO / redstone helpers (type-agnostic)                                 */
    /* --------------------------------------------------------------------- */

    protected boolean isSideActiveForInput(Direction side) {
        SideConfig cfg = sideConfigs.get(side);
        if (cfg == null) return false;

        if (cfg.ioMode == IOMode.DISABLED || cfg.ioMode == IOMode.OUTPUT) {
            return false;
        }

        if (cfg.redstoneMode == RedstoneMode.IGNORED) {
            return true;
        }

        return level != null && level.hasNeighborSignal(worldPosition);
    }

    public boolean isSideActiveForOutput(Direction side) {
        SideConfig cfg = sideConfigs.get(side);
        if (cfg == null) return false;

        if (cfg.ioMode == IOMode.DISABLED || cfg.ioMode == IOMode.INPUT) {
            return false;
        }

        if (cfg.redstoneMode == RedstoneMode.IGNORED) {
            return true;
        }

        return level != null && level.hasNeighborSignal(worldPosition);
    }

    /* --------------------------------------------------------------------- */
    /* Hooks for transport-specific logic                                    */
    /* --------------------------------------------------------------------- */

    /**
     * Called by the block shape logic to check if this pipe should visually
     * connect to the neighbor block on this side (non-pipe).
     * Implementation decides which capability to test (energy/fluid/item/gas).
     */
    public abstract boolean canConnectToBlock(Direction side);

    /**
     * Called when a neighbor pushes units (FE, mB, items, gas) into this pipe
     * from a given side.
     *
     * Implementation is responsible for enforcing transfer limits, scanning
     * the network, and distributing units to endpoints.
     *
     * @return amount actually accepted.
     */
    public abstract int receiveFromSide(Direction fromSide, int maxAmount, boolean simulate);

    /* --------------------------------------------------------------------- */
    /* Connection + config helpers                                           */
    /* --------------------------------------------------------------------- */

    public void toggleDisabled(Direction side) {
        SideConfig cfg = sideConfigs.get(side);
        if (cfg == null) {
            cfg = new SideConfig();
        }

        if (cfg.ioMode == IOMode.DISABLED) {
            cfg.ioMode = IOMode.BOTH;
        } else {
            cfg.ioMode = IOMode.DISABLED;
        }

        sideConfigs.put(side, cfg);
        setChanged();
        refreshConnections();
    }

    /**
     * Called by the BE whenever side configs change, to recompute blockstate
     * connection flags for rendering.
     */
    public void refreshConnections() {
        if (level == null) return;

        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof BasePipe pipeBlock)) {
            return;
        }

        BlockState newState = pipeBlock.updateConnections(state, level, worldPosition);
        if (newState != state) {
            level.setBlock(worldPosition, newState, Block.UPDATE_ALL);
        }
    }

    /**
     * Use this in block rendering logic to decide if the pipe may
     * visually connect on a side at all (not DISABLED).
     */
    public boolean isSideVisuallyEnabled(Direction side) {
        SideConfig cfg = sideConfigs.get(side);
        return cfg != null && cfg.ioMode != IOMode.DISABLED;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            refreshConnections();
        }
    }

    /* --------------------------------------------------------------------- */
    /* SAVE / LOAD                                                           */
    /* --------------------------------------------------------------------- */

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag sidesTag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            SideConfig cfg = sideConfigs.get(dir);
            if (cfg == null) continue;

            CompoundTag sideTag = new CompoundTag();
            sideTag.putInt("IOMode", cfg.ioMode.ordinal());
            sideTag.putInt("RedstoneMode", cfg.redstoneMode.ordinal());
            sideTag.putInt("Priority", cfg.priority);
            sideTag.putInt("TransferLimit", cfg.transferLimit);

            sidesTag.put(dir.getName(), sideTag);
        }
        tag.put("SideConfigs", sidesTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("SideConfigs", Tag.TAG_COMPOUND)) {
            CompoundTag sidesTag = tag.getCompound("SideConfigs");
            for (Direction dir : Direction.values()) {
                SideConfig cfg = new SideConfig();
                String key = dir.getName();

                if (sidesTag.contains(key, Tag.TAG_COMPOUND)) {
                    CompoundTag sideTag = sidesTag.getCompound(key);
                    int ioOrdinal = sideTag.getInt("IOMode");
                    int rsOrdinal = sideTag.getInt("RedstoneMode");
                    int pr = sideTag.getInt("Priority");
                    int lim = sideTag.getInt("TransferLimit");

                    cfg.ioMode = IOMode.values()[Math.max(0, Math.min(ioOrdinal, IOMode.values().length - 1))];
                    cfg.redstoneMode = RedstoneMode.values()[Math.max(0, Math.min(rsOrdinal, RedstoneMode.values().length - 1))];
                    cfg.priority = pr;
                    cfg.transferLimit = clampTransferLimit(lim);
                } else {
                    cfg.transferLimit = tierMaxTransfer;
                }

                sideConfigs.put(dir, cfg);
            }
        } else {
            for (Direction dir : Direction.values()) {
                SideConfig cfg = new SideConfig();
                cfg.transferLimit = tierMaxTransfer;
                sideConfigs.put(dir, cfg);
            }
        }
    }
}