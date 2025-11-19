package com.github.will11690.mechanicraft_revived.blocks.pipes.energy.basic;

import com.github.will11690.mechanicraft_revived.blocks.pipes.base.BasePipe;
import com.github.will11690.mechanicraft_revived.blocks.pipes.base.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BasicEnergyPipeBlockEntity extends BasePipeBlockEntity {

    public static final int BASIC_MAX_TRANSFER = 1024; // FE/t

    /* ------------------------------ caps ---------------------------------- */

    private static class PipeEnergyWrapper implements IEnergyStorage {

        private final BasicEnergyPipeBlockEntity pipe;
        private final Direction side;

        PipeEnergyWrapper(BasicEnergyPipeBlockEntity pipe, Direction side) {
            this.pipe = pipe;
            this.side = side;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive()) return 0;
            return pipe.receiveFromSide(side, maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            // no internal buffer
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return 0;
        }

        @Override
        public int getMaxEnergyStored() {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return pipe.level != null &&
                    !pipe.level.isClientSide() &&
                    pipe.isSideActiveForInput(side);
        }
    }

    private final EnumMap<Direction, LazyOptional<IEnergyStorage>> sidedEnergyCaps =
            new EnumMap<>(Direction.class);

    private final LazyOptional<IEnergyStorage> energyCapNull =
            LazyOptional.of(() -> new IEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    // arbitrary entry side
                    return receiveFromSide(Direction.NORTH, maxReceive, simulate);
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return 0;
                }

                @Override
                public int getEnergyStored() {
                    return 0;
                }

                @Override
                public int getMaxEnergyStored() {
                    return 0;
                }

                @Override
                public boolean canExtract() {
                    return false;
                }

                @Override
                public boolean canReceive() {
                    return true;
                }
            });

    public BasicEnergyPipeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.BasicEnergyPipeBE.get(),
                pos, state, PipeType.ENERGY, BASIC_MAX_TRANSFER);

        for (Direction dir : Direction.values()) {
            sidedEnergyCaps.put(dir, LazyOptional.of(() -> new PipeEnergyWrapper(this, dir)));
        }
    }

    /* --------------------------------------------------------------------- */
    /* Capability exposure                                                   */
    /* --------------------------------------------------------------------- */

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (side == null) {
                return energyCapNull.cast();
            }
            LazyOptional<IEnergyStorage> opt = sidedEnergyCaps.get(side);
            if (opt != null) {
                return opt.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<IEnergyStorage> opt : sidedEnergyCaps.values()) {
            opt.invalidate();
        }
        energyCapNull.invalidate();
    }

    /* --------------------------------------------------------------------- */
    /* Pipe-type hooks                                                       */
    /* --------------------------------------------------------------------- */

    @Override
    public boolean canConnectToBlock(Direction side) {
        if (level == null) return false;

        BlockPos neighborPos = worldPosition.relative(side);
        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
        if (neighborBE == null) return false;

        return neighborBE.getCapability(ForgeCapabilities.ENERGY, side.getOpposite()).isPresent();
    }

    private static class EnergyEndpoint {
        final BlockPos pos;
        final Direction side;
        final IEnergyStorage storage;
        final int priority;
        final int transferLimit;

        EnergyEndpoint(BlockPos pos, Direction side, IEnergyStorage storage,
                       int priority, int transferLimit) {
            this.pos = pos;
            this.side = side;
            this.storage = storage;
            this.priority = priority;
            this.transferLimit = transferLimit;
        }
    }

    /* --------------------------------------------------------------------- */
    /* Network entry + distribution                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public int receiveFromSide(Direction fromSide, int maxAmount, boolean simulate) {

        if (level == null || level.isClientSide()) {
            return 0;
        }

        if (!isSideActiveForInput(fromSide)) {
            return 0;
        }

        SideConfig cfg = getSideConfig(fromSide);
        int sideLimit = cfg != null ? clampTransferLimit(cfg.transferLimit) : tierMaxTransfer;

        int allowed = maxAmount;
        if (tierMaxTransfer > 0) {
            allowed = Math.min(allowed, tierMaxTransfer);
        }
        allowed = Math.min(allowed, sideLimit);

        if (allowed <= 0) {
            return 0;
        }

        List<EnergyEndpoint> endpoints = findNetworkOutputs(level, worldPosition);
        if (endpoints.isEmpty()) {
            return 0;
        }

        // Group endpoints by priority
        Map<Integer, List<EnergyEndpoint>> byPriority = new HashMap<>();
        int maxPriority = Integer.MIN_VALUE;

        for (EnergyEndpoint ep : endpoints) {
            byPriority.computeIfAbsent(ep.priority, k -> new ArrayList<>()).add(ep);
            if (ep.priority > maxPriority) {
                maxPriority = ep.priority;
            }
        }

        if (maxPriority == Integer.MIN_VALUE) {
            return 0;
        }

        int remaining = allowed;
        int acceptedTotal = 0;

        // Highest priority down to 0
        for (int pr = maxPriority; pr >= 0 && remaining > 0; pr--) {

            List<EnergyEndpoint> group = byPriority.get(pr);
            if (group == null || group.isEmpty()) {
                continue;
            }

            int count = group.size();
            if (count <= 0) {
                continue;
            }

            int baseShare = remaining / count;
            int leftover = remaining % count;

            if (baseShare <= 0 && leftover <= 0) {
                break;
            }

            for (EnergyEndpoint ep : group) {

                if (remaining <= 0) break;

                int toSend = baseShare;
                if (leftover > 0) {
                    toSend += 1;
                    leftover--;
                }

                if (toSend <= 0) continue;

                int epLimit = clampTransferLimit(ep.transferLimit);
                toSend = Math.min(toSend, epLimit);
                toSend = Math.min(toSend, remaining);

                if (toSend <= 0) continue;

                int accepted = ep.storage.receiveEnergy(toSend, simulate);
                if (accepted > 0) {
                    acceptedTotal += accepted;
                    remaining -= accepted;
                }
            }
        }

        return acceptedTotal;
    }

    private List<EnergyEndpoint> findNetworkOutputs(Level level, BlockPos startPos) {

        List<EnergyEndpoint> endpoints = new ArrayList<>();
        Set<BlockPos> visitedPipes = new HashSet<>();
        Set<String> visitedEndpoints = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();

        visitedPipes.add(startPos);
        queue.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            BlockEntity beAt = level.getBlockEntity(current);
            if (!(beAt instanceof BasePipeBlockEntity pipe)) {
                continue;
            }

            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = current.relative(dir);

                if (!level.isLoaded(neighborPos)) {
                    continue;
                }

                BlockState neighborState = level.getBlockState(neighborPos);

                // ---- traverse only ENERGY pipes (any tier) ----
                if (neighborState.getBlock() instanceof BasePipe) {
                    BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                    if (neighborBE instanceof BasePipeBlockEntity neighborPipe &&
                            neighborPipe.getPipeType() == PipeType.ENERGY) {

                        if (visitedPipes.add(neighborPos)) {
                            queue.add(neighborPos);
                        }
                        continue;
                    }
                }

                // ... rest of endpoint detection stays the same ...
                if (!pipe.isSideActiveForOutput(dir)) {
                    continue;
                }

                BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                if (neighborBE == null) {
                    continue;
                }

                Direction fromSide = dir.getOpposite();
                String key = neighborPos.toShortString() + "|" + fromSide.getName();

                if (visitedEndpoints.contains(key)) {
                    continue;
                }

                neighborBE.getCapability(ForgeCapabilities.ENERGY, fromSide).ifPresent(storage -> {
                    if (storage.canReceive()) {
                        SideConfig cfg = pipe.getSideConfig(dir);
                        int pr = cfg != null ? cfg.priority : 0;
                        int lim = cfg != null ? cfg.transferLimit : pipe.tierMaxTransfer;
                        endpoints.add(new EnergyEndpoint(neighborPos, fromSide, storage, pr, lim));
                        visitedEndpoints.add(key);
                    }
                });
            }
        }

        return endpoints;
    }
}