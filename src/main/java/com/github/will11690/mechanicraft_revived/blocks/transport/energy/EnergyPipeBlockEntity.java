package com.github.will11690.mechanicraft_revived.blocks.transport.energy;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeType;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.PipeNetworkManager;
import com.github.will11690.mechanicraft_revived.capabilities.energy.MechaniCraftEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public abstract class EnergyPipeBlockEntity extends BasePipeBlockEntity {

    private final int tierIndex;
    private final EnumMap<Direction, LazyOptional<MechaniCraftEnergyStorage>> sidedEnergyCaps = new EnumMap<>(Direction.class);
    private final LazyOptional<MechaniCraftEnergyStorage> energyCapNull;

    protected EnergyPipeBlockEntity(BlockEntityType<?> type,
                                     BlockPos pos,
                                     BlockState state,
                                     int tierMaxTransfer,
                                     int tierIndex) {

        super(type, pos, state, PipeType.ENERGY, tierMaxTransfer);
        this.tierIndex = tierIndex;

        for (Direction dir : Direction.values()) {
            sidedEnergyCaps.put(dir, LazyOptional.of(() -> createSideStorage(dir)));
        }
        energyCapNull = LazyOptional.of(this::createNullSideStorage);
    }

    private MechaniCraftEnergyStorage createSideStorage(Direction side) {
        return new MechaniCraftEnergyStorage(tierMaxTransfer, tierMaxTransfer, 0) {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (!canReceive()) return 0;
                return EnergyPipeBlockEntity.this.receiveFromSide(side, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
            @Override
            public int getEnergyStored() { return 0; }
            @Override
            public int getMaxEnergyStored() { return 0; }
            @Override
            public boolean canExtract() { return false; }

            @Override
            public boolean canReceive() {
                return level != null && !level.isClientSide() && isSideActiveForExtract(side);
            }

            @Override
            protected void onEnergyChanged() { }
        };
    }

    private MechaniCraftEnergyStorage createNullSideStorage() {
        return new MechaniCraftEnergyStorage(tierMaxTransfer, tierMaxTransfer, 0) {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (level == null || level.isClientSide()) return 0;
                return EnergyPipeBlockEntity.this.receiveFromSide(Direction.NORTH, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
            @Override
            public int getEnergyStored() { return 0; }
            @Override
            public int getMaxEnergyStored() { return 0; }
            @Override
            public boolean canExtract() { return false; }
            @Override
            public boolean canReceive() { return true; }

            @Override
            protected void onEnergyChanged() { }
        };
    }

    @Override
    public int getTierIndex() { return tierIndex; }

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

        PipeSideConfig cfg = getSideConfig(fromSide);
        int sideLimit = (cfg != null) ? clampTransferLimit(cfg.extractTransferLimit) : tierMaxTransfer;

        int allowed = Math.min(Math.min(maxAmount, tierMaxTransfer), sideLimit);
        if (allowed <= 0) return 0;

        PipeNetworkManager.EnergyNetwork network = PipeNetworkManager.get(level).getEnergyNetwork(worldPosition);
        if (network == null) return 0;

        int channel = (cfg != null) ? cfg.extractChannel : 0;
        return network.distributeEnergy(allowed, simulate, channel);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (side == null) return energyCapNull.cast();
            LazyOptional<MechaniCraftEnergyStorage> opt = sidedEnergyCaps.get(side);
            if (opt != null) return opt.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        sidedEnergyCaps.values().forEach(LazyOptional::invalidate);
        energyCapNull.invalidate();
    }
}
