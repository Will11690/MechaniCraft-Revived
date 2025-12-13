package com.github.will11690.mechanicraft_revived.blocks.transport.item;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.FilterMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeLogicMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeType;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.IFilterablePipe;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.PipeNetworkManager;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class ItemPipeBlockEntity extends BasePipeBlockEntity implements IFilterablePipe {

    public static final int FILTER_SLOTS_PER_SIDE = 27;
    public static final int FILTER_TOTAL_SLOTS = FILTER_SLOTS_PER_SIDE * 6;

    private final int tierIndex;

    private final ItemStackHandler filterInv = new ItemStackHandler(FILTER_TOTAL_SLOTS) {

        @Override
        protected void onContentsChanged(int slot) {

            // Mark BE dirty so it saves / includes filter in update tags
            setChanged();

            if (level != null && !level.isClientSide) {
                // Rebuild item networks so insert filters are up-to-date
                PipeNetworkManager.get(level).markDirty(PipeType.ITEM);

                // Also push an update tag to clients (for client-side BE copy)
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
            }
        }
    };

    protected ItemPipeBlockEntity(BlockEntityType<?> type,
                                  BlockPos pos,
                                  BlockState state,
                                  int tierMaxTransfer,
                                  int tierIndex) {

        super(type, pos, state, PipeType.ITEM, tierMaxTransfer);
        this.tierIndex = tierIndex;
    }

    @Override
    public int getTierIndex() {
        return tierIndex;
    }

    @Override
    public boolean canConnectToBlock(Direction side) {

        if (level == null) return false;

        BlockPos neighborPos = worldPosition.relative(side);
        if (!level.isLoaded(neighborPos)) return false;

        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
        if (neighborBE == null) return false;

        return neighborBE.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite())
                .isPresent();
    }

    /** We don't use numeric "receiveFromSide" for items, so always 0. */
    @Override
    public int receiveFromSide(Direction fromSide, int maxAmount, boolean simulate) {
        return 0;
    }

    /* --------------------------------------------------------------------- */
    /* Filters                                                               */
    /* --------------------------------------------------------------------- */

    private int sideFilterBase(Direction side) {
        return side.ordinal() * FILTER_SLOTS_PER_SIDE;
    }

    /**
     * EXTRACT-side item filter.
     *
     * BLACKLIST (default):
     *   - empty list  -> allow all items
     *   - entries     -> BLOCK matching items
     * WHITELIST:
     *   - empty list  -> block all
     *   - entries     -> ALLOW matching items only
     */
    @Override
    public Predicate<ItemStack> getItemFilter(Direction side) {
        return buildItemFilterPredicate(side, false);
    }

    /**
     * INSERT-side item filter (used when pushing from network into neighbors).
     * Semantics are identical to getItemFilter(...) but using insertFilterMode.
     */
    @Override
    public Predicate<ItemStack> getInsertItemFilter(Direction side) {
        return buildItemFilterPredicate(side, true);
    }

    private Predicate<ItemStack> buildItemFilterPredicate(Direction side, boolean insertSide) {

        final int base = sideFilterBase(side);
        PipeSideConfig cfg = getSideConfig(side);
        final FilterMode mode = (cfg != null)
                ? (insertSide ? cfg.insertFilterMode : cfg.extractFilterMode)
                : FilterMode.BLACKLIST;

        return stack -> {

            if (stack.isEmpty()) return false;

            boolean anyFilter = false;
            boolean matches   = false;

            for (int i = 0; i < FILTER_SLOTS_PER_SIDE; i++) {

                ItemStack ghost = filterInv.getStackInSlot(base + i);
                if (ghost.isEmpty()) continue;

                anyFilter = true;
                if (ItemStack.isSameItemSameTags(ghost, stack)) {
                    matches = true;
                    break;
                }
            }

            // No filters:
            //  - BLACKLIST  -> allow everything
            //  - WHITELIST  -> block everything
            if (!anyFilter) {
                return mode == FilterMode.BLACKLIST;
            }

            // We have at least one filter entry
            if (mode == FilterMode.BLACKLIST) {
                return !matches;     // block matches, allow others
            } else {
                return matches;      // allow matches, block others
            }
        };
    }

    @Override
    public IItemHandler getFilterForSide(Direction side) {

        final int base = sideFilterBase(side);

        return new IItemHandlerModifiable() {

            @Override
            public int getSlots() {

                return FILTER_SLOTS_PER_SIDE;
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {

                return filterInv.getStackInSlot(base + slot);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {

                filterInv.setStackInSlot(base + slot, stack);
            }

            @Override
            public @NotNull ItemStack insertItem(int slot,
                                                 @NotNull ItemStack stack,
                                                 boolean simulate) {

                if (stack.isEmpty()) return ItemStack.EMPTY;

                ItemStack one = stack.copy();
                one.setCount(1);
                if (!simulate) {

                    filterInv.setStackInSlot(base + slot, one);
                }
                // Do not consume from cursor; PipeFilterContainer enforces ghost semantics.
                return stack;
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {

                if (!simulate) {

                    filterInv.setStackInSlot(base + slot, ItemStack.EMPTY);
                }
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {

                return 1;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {

                return true;
            }
        };
    }

    /* --------------------------------------------------------------------- */
    /* Tick logic: pull first, then let network insert elsewhere             */
    /* --------------------------------------------------------------------- */

    public void serverTick() {

        if (level == null || level.isClientSide) return;

        PipeNetworkManager.ItemNetwork network =
                PipeNetworkManager.get(level).getItemNetwork(worldPosition);
        if (network == null) return;

        for (Direction side : Direction.values()) {

            PipeSideConfig cfg = getSideConfig(side);
            if (cfg == null || cfg.ioMode == IOMode.DISABLED) continue;

            // EXTRACT or BOTH = pull items from neighbor into network
            if (cfg.ioMode != IOMode.EXTRACT && cfg.ioMode != IOMode.BOTH) continue;

            BlockPos neighborPos = worldPosition.relative(side);
            if (!level.isLoaded(neighborPos)) continue;

            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;

            IItemHandler handler = neighborBE
                    .getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite())
                    .orElse(null);
            if (handler == null) continue;

            int perSideLimit = clampTransferLimit(cfg.extractTransferLimit);
            if (perSideLimit <= 0) perSideLimit = tierMaxTransfer;

            int remainingForSide = perSideLimit;
            int channel = cfg.extractChannel;
            PipeLogicMode logicMode = cfg.logicMode;

            Predicate<ItemStack> filter = getItemFilter(side); // EXTRACT filter

            for (int slot = 0; slot < handler.getSlots() && remainingForSide > 0; slot++) {

                ItemStack available = handler.extractItem(slot, remainingForSide, true);
                if (available.isEmpty()) continue;
                if (!filter.test(available)) continue;

                // Simulate sending through network
                ItemStack simRemaining =
                        network.distributeItems(available, true, logicMode, handler, channel);

                int canSend = available.getCount() - simRemaining.getCount();
                if (canSend <= 0) continue;

                canSend = Math.min(canSend, remainingForSide);

                // Real extraction
                ItemStack extracted = handler.extractItem(slot, canSend, false);
                if (extracted.isEmpty()) continue;

                // Real distribution
                ItemStack leftoverReal =
                        network.distributeItems(extracted, false, logicMode, handler, channel);

                int sent = extracted.getCount() - leftoverReal.getCount();
                remainingForSide -= sent;

                // Try to return leftovers back into the same handler
                if (!leftoverReal.isEmpty()) {
                    ItemStack remainder = handler.insertItem(slot, leftoverReal, false);

                    if (!remainder.isEmpty()) {
                        for (int backSlot = 0; backSlot < handler.getSlots() && !remainder.isEmpty(); backSlot++) {
                            if (backSlot == slot) continue;
                            remainder = handler.insertItem(backSlot, remainder, false);
                        }
                    }
                }
            }
        }
    }

    /* --------------------------------------------------------------------- */
    /* Save / load filter inventory                                          */
    /* --------------------------------------------------------------------- */

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {

        super.saveAdditional(tag);
        tag.put("FilterItems", filterInv.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {

        super.load(tag);
        if (tag.contains("FilterItems")) {

            filterInv.deserializeNBT(tag.getCompound("FilterItems"));
        }
    }

    /* --------------------------------------------------------------------- */
    /* No external item capability (pipes aren't inventories)                */
    /* --------------------------------------------------------------------- */

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {

        return super.getCapability(cap, side);
    }
}