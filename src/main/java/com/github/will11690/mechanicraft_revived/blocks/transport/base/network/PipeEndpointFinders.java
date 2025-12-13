package com.github.will11690.mechanicraft_revived.blocks.transport.base.network;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class PipeEndpointFinders {

    public static void registerAll() {

        // ENERGY
        BasePipeBlockEntity.registerEndpointFinder(
                new BasePipeBlockEntity.EndpointFinder<PipeNetworkManager.EnergyNetwork.Endpoint>() {

                    @Override
                    public PipeType type() {

                        return PipeType.ENERGY;
                    }

                    @Override
                    public PipeNetworkManager.EnergyNetwork.Endpoint findEndpoint(
                            BasePipeBlockEntity pipe,
                            Level level,
                            BlockPos neighborPos,
                            Direction dirFromPipe) {

                        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                        if (neighborBE == null) return null;

                        Direction fromSide = dirFromPipe.getOpposite();
                        LazyOptional<IEnergyStorage> cap =
                                neighborBE.getCapability(ForgeCapabilities.ENERGY, fromSide);

                        IEnergyStorage storage = cap.resolve().orElse(null);
                        if (storage == null || !storage.canReceive()) return null;

                        // This is an OUTPUT from the pipe into the neighbor
                        PipeSideConfig cfg = pipe.getSideConfig(dirFromPipe);
                        int pr = (cfg != null) ? cfg.insertPriority : 0;
                        int lim = (cfg != null)
                                ? pipe.clampTransferLimit(cfg.insertTransferLimit)
                                : pipe.getTierMaxTransfer();
                        int ch = (cfg != null) ? cfg.insertChannel : 0;

                        return new PipeNetworkManager.EnergyNetwork.Endpoint(storage, pr, lim, ch);
                    }
                });

        // ITEM
        BasePipeBlockEntity.registerEndpointFinder(
                new BasePipeBlockEntity.EndpointFinder<PipeNetworkManager.ItemNetwork.Endpoint>() {

                    @Override
                    public PipeType type() {

                        return PipeType.ITEM;
                    }

                    @Override
                    public PipeNetworkManager.ItemNetwork.Endpoint findEndpoint(
                            BasePipeBlockEntity pipe,
                            Level level,
                            BlockPos neighborPos,
                            Direction dirFromPipe) {

                        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                        if (neighborBE == null) return null;

                        Direction fromSide = dirFromPipe.getOpposite();
                        LazyOptional<IItemHandler> cap =
                                neighborBE.getCapability(ForgeCapabilities.ITEM_HANDLER, fromSide);

                        IItemHandler handler = cap.resolve().orElse(null);
                        if (handler == null) return null;

                        // OUTPUT settings
                        PipeSideConfig cfg = pipe.getSideConfig(dirFromPipe);
                        int pr = (cfg != null) ? cfg.insertPriority : 0;
                        int lim = (cfg != null)
                                ? pipe.clampTransferLimit(cfg.insertTransferLimit)
                                : pipe.getTierMaxTransfer();
                        int ch = (cfg != null) ? cfg.insertChannel : 0;

                        java.util.function.Predicate<ItemStack> insertFilter =
                                pipe.getInsertItemFilter(dirFromPipe);

                        return new PipeNetworkManager.ItemNetwork.Endpoint(handler, pr, lim, ch, insertFilter);
                    }
                });

        // FLUID
        BasePipeBlockEntity.registerEndpointFinder(
                new BasePipeBlockEntity.EndpointFinder<PipeNetworkManager.FluidNetwork.Endpoint>() {

                    @Override
                    public PipeType type() {

                        return PipeType.FLUID;
                    }

                    @Override
                    public PipeNetworkManager.FluidNetwork.Endpoint findEndpoint(
                            BasePipeBlockEntity pipe,
                            Level level,
                            BlockPos neighborPos,
                            Direction dirFromPipe) {

                        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                        if (neighborBE == null) return null;

                        Direction fromSide = dirFromPipe.getOpposite();
                        LazyOptional<IFluidHandler> cap =
                                neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, fromSide);

                        IFluidHandler handler = cap.resolve().orElse(null);
                        if (handler == null) return null;

                        // OUTPUT settings
                        PipeSideConfig cfg = pipe.getSideConfig(dirFromPipe);
                        int pr = (cfg != null) ? cfg.insertPriority : 0;
                        int lim = (cfg != null)
                                ? pipe.clampTransferLimit(cfg.insertTransferLimit)
                                : pipe.getTierMaxTransfer();
                        int ch = (cfg != null) ? cfg.insertChannel : 0;

                        java.util.function.Predicate<FluidStack> insertFilter =
                                pipe.getInsertFluidFilter(dirFromPipe);

                        return new PipeNetworkManager.FluidNetwork.Endpoint(handler, pr, lim, ch, insertFilter);
                    }
                });

        // GAS stub: still none for now.
    }
}