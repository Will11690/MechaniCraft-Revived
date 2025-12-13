package com.github.will11690.mechanicraft_revived.blocks.transport.energy.superior;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeType;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.PipeNetworkManager;
import com.github.will11690.mechanicraft_revived.capabilities.energy.MechaniCraftEnergyStorage;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class SuperiorEnergyPipeBlockEntity extends BasePipeBlockEntity {

    public static final int SUPERIOR_MAX_TRANSFER = 262_144; // FE/t

    private final EnumMap<Direction, LazyOptional<MechaniCraftEnergyStorage>> sidedEnergyCaps =
            new EnumMap<>(Direction.class);

    private final LazyOptional<MechaniCraftEnergyStorage> energyCapNull =
            LazyOptional.of(this::createNullSideStorage);

    public SuperiorEnergyPipeBlockEntity(BlockPos pos, BlockState state) {

        super(MechaniCraftBlockEntities.SuperiorEnergyPipeBE.get(),
                pos, state, PipeType.ENERGY, SUPERIOR_MAX_TRANSFER);

        for (Direction dir : Direction.values()) {

            sidedEnergyCaps.put(dir, LazyOptional.of(() -> createSideStorage(dir)));
        }
    }

    private MechaniCraftEnergyStorage createSideStorage(Direction side) {

        return new MechaniCraftEnergyStorage(SUPERIOR_MAX_TRANSFER, SUPERIOR_MAX_TRANSFER, 0) {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {

                if (!canReceive()) return 0;
                return SuperiorEnergyPipeBlockEntity.this.receiveFromSide(side, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {

                return 0; // no internal buffer
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

                return level != null && !level.isClientSide() && isSideActiveForExtract(side);
            }

            @Override
            protected void onEnergyChanged() {
                // pipes do not buffer
            }
        };
    }

    private MechaniCraftEnergyStorage createNullSideStorage() {

        return new MechaniCraftEnergyStorage(SUPERIOR_MAX_TRANSFER, SUPERIOR_MAX_TRANSFER, 0) {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {

                if (level == null || level.isClientSide()) return 0;
                // treat as extract from NORTH for internal/no-side usage
                return SuperiorEnergyPipeBlockEntity.this.receiveFromSide(Direction.NORTH,
                        maxReceive, simulate);
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

            @Override
            protected void onEnergyChanged() {
                // no buffer
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {

        if (cap == ForgeCapabilities.ENERGY) {

            if (side == null) {

                return energyCapNull.cast();
            }

            LazyOptional<MechaniCraftEnergyStorage> opt = sidedEnergyCaps.get(side);
            if (opt != null) {

                return opt.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {

        super.invalidateCaps();

        for (LazyOptional<MechaniCraftEnergyStorage> opt : sidedEnergyCaps.values()) {

            opt.invalidate();
        }

        energyCapNull.invalidate();
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

        return neighborBE.getCapability(ForgeCapabilities.ENERGY, side.getOpposite()).isPresent();
    }

    @Override
    public int receiveFromSide(Direction fromSide, int maxAmount, boolean simulate) {

        if (level == null || level.isClientSide()) return 0;
        if (!isSideActiveForExtract(fromSide)) return 0;

        // Use EXTRACT-side config (EXTRACT side from the neighbor into the network)
        PipeSideConfig cfg = getSideConfig(fromSide);
        int sideLimit = (cfg != null)
                ? clampTransferLimit(cfg.extractTransferLimit)
                : tierMaxTransfer;

        int allowed = Math.min(maxAmount, tierMaxTransfer);
        allowed = Math.min(allowed, sideLimit);
        if (allowed <= 0) return 0;

        PipeNetworkManager.EnergyNetwork network =
                PipeNetworkManager.get(level).getEnergyNetwork(worldPosition);
        if (network == null) return 0;

        int channel = (cfg != null) ? cfg.extractChannel : 0;
        return network.distributeEnergy(allowed, simulate, channel);
    }
}