package com.github.will11690.mechanicraft_revived.blocks.transport.fluid.basic;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.FilterMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeLogicMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeType;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.IFilterablePipe;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.PipeNetworkManager;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BasicFluidPipeBlockEntity extends BasePipeBlockEntity implements IFilterablePipe {

    public static final int BASIC_MAX_TRANSFER = 1_000; // mB/t

    public static final int FILTER_SLOTS_PER_SIDE = 27;
    public static final int FILTER_TOTAL_SLOTS = FILTER_SLOTS_PER_SIDE * 6;

    private final ItemStackHandler filterInv = new ItemStackHandler(FILTER_TOTAL_SLOTS) {

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();

            // Immediately rebuild fluid network + sync to clients when filters change
            if (level != null && !level.isClientSide) {
                PipeNetworkManager.get(level).markDirty(PipeType.FLUID);
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
            }
        }
    };

    public BasicFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.BasicFluidPipeBE.get(),
                pos, state, PipeType.FLUID, BASIC_MAX_TRANSFER);
    }

    @Override
    public int getTierIndex() {
        return 1;
    }

    @Override
    public boolean canConnectToBlock(Direction side) {
        if (level == null) return false;

        BlockPos neighborPos = worldPosition.relative(side);
        if (!level.isLoaded(neighborPos)) return false;

        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
        if (neighborBE == null) return false;

        return neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite())
                .isPresent();
    }

    /** Not used for fluids (numeric FE style), so 0. */
    @Override
    public int receiveFromSide(Direction fromSide, int maxAmount, boolean simulate) {
        return 0;
    }

    /* --------------------------------------------------------------------- */
    /* Filters                                                               */
    /* --------------------------------------------------------------------- */

    public ItemStackHandler getFilterInventory() {
        return filterInv;
    }

    private int sideFilterBase(Direction side) {
        return side.ordinal() * FILTER_SLOTS_PER_SIDE;
    }

    /**
     * EXTRACT-side fluid filter.
     */
    @Override
    public Predicate<FluidStack> getFluidFilter(Direction side) {
        return buildFluidFilterPredicate(side, false);
    }

    /**
     * INSERT-side fluid filter.
     */
    @Override
    public Predicate<FluidStack> getInsertFluidFilter(Direction side) {
        return buildFluidFilterPredicate(side, true);
    }

    private Predicate<FluidStack> buildFluidFilterPredicate(Direction side, boolean insertSide) {

        final int base = sideFilterBase(side);
        PipeSideConfig cfg = getSideConfig(side);
        final FilterMode mode = (cfg != null)
                ? (insertSide ? cfg.insertFilterMode : cfg.extractFilterMode)
                : FilterMode.BLACKLIST;

        return fs -> {

            if (fs.isEmpty()) return false;

            boolean anyFilter = false;
            boolean matches   = false;

            for (int i = 0; i < FILTER_SLOTS_PER_SIDE; i++) {

                ItemStack ghost = filterInv.getStackInSlot(base + i);
                if (ghost.isEmpty()) continue;

                anyFilter = true;

                FluidStack inGhost = FluidUtil.getFluidContained(ghost).orElse(FluidStack.EMPTY);
                if (inGhost.isEmpty()) continue;

                if (inGhost.getFluid() == fs.getFluid()) {
                    matches = true;
                    break;
                }
            }

            // No filters:
            //  - BLACKLIST  -> allow all
            //  - WHITELIST  -> block all
            if (!anyFilter) {
                return mode == FilterMode.BLACKLIST;
            }

            if (mode == FilterMode.BLACKLIST) {
                return !matches;
            } else {
                return matches;
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
                // Ghost semantics: do not consume from cursor
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
    /* Tick logic: pull first, network handles distribution                  */
    /* --------------------------------------------------------------------- */

    public void serverTick() {

        if (level == null || level.isClientSide()) return;

        PipeNetworkManager.FluidNetwork network =
                PipeNetworkManager.get(level).getFluidNetwork(worldPosition);
        if (network == null) return;

        for (Direction side : Direction.values()) {

            PipeSideConfig cfg = getSideConfig(side);
            if (cfg == null || cfg.ioMode == IOMode.DISABLED) continue;

            // INPUT or BOTH = extract fluid from neighbor into network
            if (cfg.ioMode != IOMode.EXTRACT && cfg.ioMode != IOMode.BOTH) continue;

            BlockPos neighborPos = worldPosition.relative(side);
            if (!level.isLoaded(neighborPos)) continue;

            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;

            IFluidHandler handler = neighborBE
                    .getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite())
                    .orElse(null);
            if (handler == null) continue;

            int perSideLimit = clampTransferLimit(cfg.extractTransferLimit);
            if (perSideLimit <= 0) perSideLimit = tierMaxTransfer;

            int remainingForSide = perSideLimit;
            int channel = cfg.extractChannel;
            PipeLogicMode logicMode = cfg.logicMode;

            Predicate<FluidStack> filter = getFluidFilter(side); // EXTRACT filter

            for (int tank = 0; tank < handler.getTanks() && remainingForSide > 0; tank++) {

                FluidStack inTank = handler.getFluidInTank(tank);
                if (inTank.isEmpty()) continue;
                if (!filter.test(inTank)) continue;

                int drainAmount = Math.min(inTank.getAmount(), remainingForSide);
                if (drainAmount <= 0) continue;

                FluidStack simDrain =
                        handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
                if (simDrain.isEmpty()) continue;

                int canAcceptSim =
                        network.distributeFluid(simDrain, true, logicMode, handler, channel);
                if (canAcceptSim <= 0) continue;

                FluidStack drained =
                        handler.drain(canAcceptSim, IFluidHandler.FluidAction.EXECUTE);
                if (drained.isEmpty()) continue;

                int acceptedReal =
                        network.distributeFluid(drained, false, logicMode, handler, channel);

                remainingForSide -= acceptedReal;
            }
        }
    }

    /* --------------------------------------------------------------------- */
    /* Save / load filter inventory                                          */
    /* --------------------------------------------------------------------- */

    @Override
    protected void saveAdditional(@NotNull net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("FilterFluids", filterInv.serializeNBT());
    }

    @Override
    public void load(@NotNull net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        if (tag.contains("FilterFluids")) {
            filterInv.deserializeNBT(tag.getCompound("FilterFluids"));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {
        return super.getCapability(cap, side);
    }
}