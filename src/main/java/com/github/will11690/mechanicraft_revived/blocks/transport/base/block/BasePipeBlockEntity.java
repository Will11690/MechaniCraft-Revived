package com.github.will11690.mechanicraft_revived.blocks.transport.base.block;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.gui.PipeConfigContainer;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.PipeNetworkManager;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import com.github.will11690.mechanicraft_revived.util.block.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.function.Predicate;

public abstract class BasePipeBlockEntity extends BlockEntity implements MenuProvider {

    /** 1 = basic, 2 = enhanced, 3 = advanced, 4 = elite, 5 = superior, 6 = ultimate */
    public int getTierIndex() { return 0; }

    /* --------------------------------------------------------------------- */
    /* Endpoint Finder Registry                                              */
    /* --------------------------------------------------------------------- */

    public interface EndpointFinder<E> {
        PipeType type();
        E findEndpoint(BasePipeBlockEntity pipe, Level level, BlockPos neighborPos, Direction dirFromPipe);
    }

    private static final EnumMap<PipeType, EndpointFinder<?>> ENDPOINT_FINDERS =
            new EnumMap<>(PipeType.class);

    public static <E> void registerEndpointFinder(EndpointFinder<E> finder) {
        ENDPOINT_FINDERS.put(finder.type(), finder);
    }

    @SuppressWarnings("unchecked")
    public static <E> EndpointFinder<E> getEndpointFinder(PipeType type) {
        return (EndpointFinder<E>) ENDPOINT_FINDERS.get(type);
    }

    protected final PipeType pipeType;
    public final int tierMaxTransfer;
    protected final EnumMap<Direction, PipeSideConfig> sideConfigs = new EnumMap<>(Direction.class);

    protected BasePipeBlockEntity(BlockEntityType<?> type,
                                  BlockPos pos,
                                  BlockState state,
                                  PipeType pipeType,
                                  int tierMaxTransfer) {

        super(type, pos, state);
        this.pipeType = pipeType;
        this.tierMaxTransfer = tierMaxTransfer;

        for (Direction dir : Direction.values()) {
            PipeSideConfig cfg = new PipeSideConfig();

            cfg.extractTransferLimit = tierMaxTransfer;
            cfg.insertTransferLimit  = tierMaxTransfer;

            cfg.extractChannel = 0;
            cfg.insertChannel  = 0;

            cfg.logicMode = PipeLogicMode.NEAREST_FIRST;

            cfg.extractFilterMode = FilterMode.BLACKLIST;
            cfg.insertFilterMode  = FilterMode.BLACKLIST;

            sideConfigs.put(dir, cfg);
        }
    }

    public PipeType getPipeType() { return pipeType; }
    public int getTierMaxTransfer() { return tierMaxTransfer; }

    public PipeSideConfig getSideConfig(Direction side) {
        return sideConfigs.get(side);
    }

    public void setSideConfig(Direction side, PipeSideConfig cfg) {

        sideConfigs.put(side, cfg);
        setChanged();
        refreshConnections();

        if (level != null && !level.isClientSide) {
            PipeNetworkManager.get(level).markDirty(pipeType);
            syncToClient();
        }
    }

    /**
     * Clamp a transfer limit (for either extract or insert) to the tier's max.
     * 0 or less is treated as "0" (no transfer) here; GUI can still display special cases.
     */
    public int clampTransferLimit(int limit) {
        int cap = tierMaxTransfer > 0 ? tierMaxTransfer : Integer.MAX_VALUE;
        if (limit <= 0) return 0;
        return Math.min(limit, cap);
    }

    /**
     * Returns an "effective" channel for network logic.
     * For now this uses the extract channel; if you want something else,
     * adjust this to your chosen semantics (e.g. prefer non-zero side, etc.).
     */
    public int getChannel(Direction side) {
        PipeSideConfig cfg = sideConfigs.get(side);
        if (cfg == null) return 0;

        int ch = cfg.extractChannel;
        if (ch < 0) ch = 0;
        if (ch > 255) ch = 255;
        return ch;
    }

    public PipeLogicMode getLogicMode(Direction side) {
        PipeSideConfig cfg = sideConfigs.get(side);
        return cfg != null ? cfg.logicMode : PipeLogicMode.NEAREST_FIRST;
    }

    /* --------------------------------------------------------------------- */
    /* MenuProvider                                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId,
                                            Inventory inv,
                                            Player player) {
        return new PipeConfigContainer(windowId, inv, this);
    }

    /* --------------------------------------------------------------------- */
    /* IO / redstone helpers                                                 */
    /* --------------------------------------------------------------------- */

    public boolean isSideActiveForExtract(Direction side) {
        PipeSideConfig cfg = sideConfigs.get(side);
        if (cfg == null) return false;

        // Extract is only for EXTRACT / BOTH
        if (cfg.ioMode == IOMode.DISABLED || cfg.ioMode == IOMode.INSERT) return false;

        // Apply redstone mode
        return isRedstoneConditionMet(cfg.redstoneMode);
    }

    public boolean isSideActiveForInsert(Direction side) {
        PipeSideConfig cfg = sideConfigs.get(side);
        if (cfg == null) return false;

        // Insert is only for INSERT / BOTH
        if (cfg.ioMode == IOMode.DISABLED || cfg.ioMode == IOMode.EXTRACT) return false;

        // Apply redstone mode
        return isRedstoneConditionMet(cfg.redstoneMode);
    }

    /** Logical network-open check (DISABLED blocks traversal/endpoints). */
    public boolean isSideNetworkOpen(Direction side) {
        PipeSideConfig cfg = sideConfigs.get(side);
        return cfg != null && cfg.ioMode != IOMode.DISABLED;
    }

    /* --------------------------------------------------------------------- */
    /* Hooks for transport-specific logic                                    */
    /* --------------------------------------------------------------------- */

    public abstract boolean canConnectToBlock(Direction side);
    public abstract int receiveFromSide(Direction fromSide, int maxAmount, boolean simulate);

    /* --------------------------------------------------------------------- */
    /* Item / fluid filter hooks (override in item / fluid pipes)            */
    /* --------------------------------------------------------------------- */

    /**
     * Extraction-side item filter: used when pulling items from neighbors.
     */
    public Predicate<ItemStack> getItemFilter(Direction side) {
        return stack -> true;
    }

    /**
     * Insert-side item filter: used when pushing items into endpoints from this side.
     * Default: same as extract filter.
     */
    public Predicate<ItemStack> getInsertItemFilter(Direction side) {
        return getItemFilter(side);
    }

    /**
     * Extraction-side fluid filter: used when pulling from neighbors.
     */
    public Predicate<FluidStack> getFluidFilter(Direction side) {
        return fs -> true;
    }

    /**
     * Insert-side fluid filter: used when pushing fluids into endpoints from this side.
     * Default: same as extract filter.
     */
    public Predicate<FluidStack> getInsertFluidFilter(Direction side) {
        return getFluidFilter(side);
    }

    /* --------------------------------------------------------------------- */
    /* Connection + config helpers                                           */
    /* --------------------------------------------------------------------- */

    public void toggleDisabled(Direction side) {

        PipeSideConfig cfg = sideConfigs.get(side);
        if (cfg == null) cfg = new PipeSideConfig();

        cfg.ioMode = (cfg.ioMode == IOMode.DISABLED) ? IOMode.BOTH : IOMode.DISABLED;

        sideConfigs.put(side, cfg);
        setChanged();
        refreshConnections();

        if (level != null && !level.isClientSide) {
            PipeNetworkManager.get(level).markDirty(pipeType);
            syncToClient();
        }
    }

    private boolean isRedstoneConditionMet(RedstoneMode mode) {
        // IGNORED: always allow
        if (mode == RedstoneMode.IGNORED) {
            return true;
        }

        if (level == null) {
            // No level = be conservative
            return false;
        }

        int signal = level.getBestNeighborSignal(worldPosition); // 0..15

        return switch (mode) {
            case OFF -> signal == 0;                     // only work when there's NO power
            case LOW -> signal > 0 && signal <= 7;       // low power
            case HIGH -> signal >= 8;                    // high power
            case IGNORED -> true;                        // already handled above, but required by switch
        };
    }

    public void refreshConnections() {

        if (level == null) return;

        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof BasePipe pipeBlock)) return;

        BlockState newState = pipeBlock.updateConnections(state, level, worldPosition);
        if (newState != state && !level.isClientSide) {
            level.setBlock(worldPosition, newState, Block.UPDATE_ALL);
        }
    }

    public boolean isSideVisuallyEnabled(Direction side) {
        PipeSideConfig cfg = sideConfigs.get(side);
        return cfg != null && cfg.ioMode != IOMode.DISABLED;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) refreshConnections();
    }

    /* --------------------------------------------------------------------- */
    /* CLIENT <-> SERVER SYNC                                                */
    /* --------------------------------------------------------------------- */

    /**
     * Call this on the SERVER whenever sideConfigs change
     * so the client BE receives the updated NBT (used by GUI).
     */
    private void syncToClient() {
        if (level == null || level.isClientSide) return;
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag); // include SideConfigs
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag); // apply SideConfigs on client
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // Called when the chunk is sent or when we call sendBlockUpdated.
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        // Use the same path as chunk-based updates
        handleUpdateTag(pkt.getTag());
    }

    /* --------------------------------------------------------------------- */
    /* SAVE / LOAD                                                           */
    /* --------------------------------------------------------------------- */

    @Override
    protected void saveAdditional(CompoundTag tag) {

        super.saveAdditional(tag);

        CompoundTag sidesTag = new CompoundTag();
        for (Direction dir : Direction.values()) {

            PipeSideConfig cfg = sideConfigs.get(dir);
            if (cfg == null) continue;

            CompoundTag sideTag = new CompoundTag();
            sideTag.putInt("IOMode", cfg.ioMode.ordinal());
            sideTag.putInt("RedstoneMode", cfg.redstoneMode.ordinal());
            sideTag.putInt("LogicMode", cfg.logicMode.ordinal());

            // New-format per-direction values
            sideTag.putInt("ExtractPriority",       cfg.extractPriority);
            sideTag.putInt("InsertPriority",        cfg.insertPriority);
            sideTag.putInt("ExtractTransferLimit",  cfg.extractTransferLimit);
            sideTag.putInt("InsertTransferLimit",   cfg.insertTransferLimit);
            sideTag.putInt("ExtractChannel",        cfg.extractChannel);
            sideTag.putInt("InsertChannel",         cfg.insertChannel);

            // NEW: per-direction filter modes
            sideTag.putInt("ExtractFilterMode", cfg.extractFilterMode.ordinal());
            sideTag.putInt("InsertFilterMode",  cfg.insertFilterMode.ordinal());

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

                PipeSideConfig cfg = new PipeSideConfig();
                String key = dir.getName();

                if (sidesTag.contains(key, Tag.TAG_COMPOUND)) {

                    CompoundTag sideTag = sidesTag.getCompound(key);

                    int ioOrdinal    = sideTag.getInt("IOMode");
                    int rsOrdinal    = sideTag.getInt("RedstoneMode");
                    int logicOrdinal = sideTag.contains("LogicMode")
                            ? sideTag.getInt("LogicMode")
                            : 0;

                    // --- Filter modes (optional; default BLACKLIST) ---
                    int exFilterOrd = sideTag.contains("ExtractFilterMode", Tag.TAG_INT)
                            ? sideTag.getInt("ExtractFilterMode") : 0;
                    int inFilterOrd = sideTag.contains("InsertFilterMode", Tag.TAG_INT)
                            ? sideTag.getInt("InsertFilterMode")  : 0;

                    cfg.extractFilterMode = FilterMode.fromOrdinal(exFilterOrd);
                    cfg.insertFilterMode  = FilterMode.fromOrdinal(inFilterOrd);

                    // --- New format detection ---
                    boolean hasNewFormat =
                            sideTag.contains("ExtractChannel",       Tag.TAG_INT) ||
                                    sideTag.contains("InsertChannel",        Tag.TAG_INT) ||
                                    sideTag.contains("ExtractPriority",      Tag.TAG_INT) ||
                                    sideTag.contains("InsertPriority",       Tag.TAG_INT) ||
                                    sideTag.contains("ExtractTransferLimit", Tag.TAG_INT) ||
                                    sideTag.contains("InsertTransferLimit",  Tag.TAG_INT);

                    if (hasNewFormat) {

                        cfg.extractChannel       = clampChannelRaw(sideTag.getInt("ExtractChannel"));
                        cfg.insertChannel        = clampChannelRaw(sideTag.getInt("InsertChannel"));
                        cfg.extractPriority      = sideTag.getInt("ExtractPriority");
                        cfg.insertPriority       = sideTag.getInt("InsertPriority");
                        cfg.extractTransferLimit = clampTransferLimit(sideTag.getInt("ExtractTransferLimit"));
                        cfg.insertTransferLimit  = clampTransferLimit(sideTag.getInt("InsertTransferLimit"));

                    } else {
                        // --- Old format fallback (Channel / Priority / TransferLimit) ---
                        int priority      = sideTag.getInt("Priority");
                        int transferLimit = sideTag.getInt("TransferLimit");
                        int channel       = sideTag.contains("Channel")
                                ? clampChannelRaw(sideTag.getInt("Channel"))
                                : 0;

                        cfg.extractPriority      = priority;
                        cfg.insertPriority       = priority;
                        cfg.extractTransferLimit = clampTransferLimit(transferLimit);
                        cfg.insertTransferLimit  = clampTransferLimit(transferLimit);
                        cfg.extractChannel       = channel;
                        cfg.insertChannel        = channel;
                    }

                    IOMode[]        ioValues    = IOMode.values();
                    RedstoneMode[]  rsValues    = RedstoneMode.values();
                    PipeLogicMode[] logicValues = PipeLogicMode.values();

                    cfg.ioMode       = ioValues[Math.max(0, Math.min(ioOrdinal,     ioValues.length     - 1))];
                    cfg.redstoneMode = rsValues[Math.max(0, Math.min(rsOrdinal,     rsValues.length     - 1))];
                    cfg.logicMode    = logicValues[Math.max(0, Math.min(logicOrdinal, logicValues.length - 1))];

                } else {

                    // Default for missing side entry
                    cfg.extractTransferLimit = tierMaxTransfer;
                    cfg.insertTransferLimit  = tierMaxTransfer;
                    cfg.extractChannel       = 0;
                    cfg.insertChannel        = 0;
                    cfg.logicMode            = PipeLogicMode.NEAREST_FIRST;
                    cfg.extractFilterMode    = FilterMode.BLACKLIST;
                    cfg.insertFilterMode     = FilterMode.BLACKLIST;
                }

                sideConfigs.put(dir, cfg);
            }
        } else {

            // No "SideConfigs" tag at all – fully default
            for (Direction dir : Direction.values()) {

                PipeSideConfig cfg = new PipeSideConfig();
                cfg.extractTransferLimit = tierMaxTransfer;
                cfg.insertTransferLimit  = tierMaxTransfer;
                cfg.extractChannel       = 0;
                cfg.insertChannel        = 0;
                cfg.logicMode            = PipeLogicMode.NEAREST_FIRST;
                cfg.extractFilterMode    = FilterMode.BLACKLIST;
                cfg.insertFilterMode     = FilterMode.BLACKLIST;
                sideConfigs.put(dir, cfg);
            }
        }
    }

    /** Helper for raw channel clamping [0, 255] during load. */
    private int clampChannelRaw(int value) {
        if (value < 0) return 0;
        if (value > 255) return 255;
        return value;
    }
}