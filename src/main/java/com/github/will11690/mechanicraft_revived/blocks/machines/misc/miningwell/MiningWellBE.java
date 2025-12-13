package com.github.will11690.mechanicraft_revived.blocks.machines.misc.miningwell;

import com.github.will11690.mechanicraft_revived.capabilities.energy.MechaniCraftEnergyStorage;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MiningWellBE extends BlockEntity implements MenuProvider {

    /* CONFIG */

    public static final int ENERGY_CAPACITY = 200_000;
    public static final int MAX_RECEIVE = 1600;
    public static final int MAX_EXTRACT = 0; // does not output FE
    public static final int ENERGY_PER_HARDNESS = 100;

    /* ENERGY */

    private final MechaniCraftEnergyStorage energy =
            new MechaniCraftEnergyStorage(ENERGY_CAPACITY, MAX_RECEIVE, MAX_EXTRACT) {

                @Override
                protected void onEnergyChanged() {
                    MiningWellBE.this.setChanged();
                }
            };

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);

    /* DATA SYNC */

    private final ContainerData data = new SimpleContainerData(2) {

        @Override
        public int get(int index) {

            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {

            if (index == 0) {
                energy.setEnergy(value);
            }
        }

        @Override
        public int getCount() {

            return 2;
        }
    };

    private int nextY;

    public MiningWellBE(BlockPos pos, BlockState state) {

        super(MechaniCraftBlockEntities.MiningWellBE.get(), pos, state);

        energy.updateEnergyStorageNoUpgrades(ENERGY_CAPACITY, MAX_RECEIVE, MAX_EXTRACT);

        nextY = pos.below().getY();
    }

    /* TICK */

    public void serverTick() {

        if (level == null || level.isClientSide()) {

            return;
        }

        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof MiningWell)) {

            return;
        }

        if (state.getValue(MiningWell.STATE) == MiningWell.WellState.COMPLETE) {
            return;
        }

        BlockPos targetPos = new BlockPos(worldPosition.getX(), nextY, worldPosition.getZ());

        if (targetPos.getY() < level.getMinBuildHeight()) {

            level.setBlock(worldPosition, state.setValue(MiningWell.STATE, MiningWell.WellState.COMPLETE), Block.UPDATE_ALL);
            setChanged();
            return;
        }

        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.isAir()) {

            placePipe(targetPos);
            nextY--;
            setChanged();
            return;
        }

        float hardness = targetState.getDestroySpeed(level, targetPos);
        if (hardness < 0) {

            level.setBlock(worldPosition, state.setValue(MiningWell.STATE, MiningWell.WellState.COMPLETE), Block.UPDATE_ALL);
            setChanged();
            return;
        }

        int cost = (int) Math.ceil(hardness * ENERGY_PER_HARDNESS);

        if (energy.getEnergyStored() < cost) {
            return;
        }

        energy.consumeEnergy(cost);

        if (!targetState.isAir()) {

            Block.dropResources(targetState, level, targetPos, null);
            level.destroyBlock(targetPos, false);

            handleMinedBlockDrops(targetPos);

            placePipe(targetPos);
        }

        nextY--;

        if (nextY < level.getMinBuildHeight()) {

            level.setBlock(worldPosition, state.setValue(MiningWell.STATE, MiningWell.WellState.COMPLETE), Block.UPDATE_ALL);
        }

        setChanged();
    }

    private void handleMinedBlockDrops(BlockPos sourcePos) {

        if (level == null || level.isClientSide()) {

            return;
        }

        BlockPos outputPos = worldPosition.above();
        BlockEntity outputBE = level.getBlockEntity(outputPos);

        IItemHandler chestHandler = null;
        if (outputBE != null) {

            chestHandler = outputBE.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
        }

        AABB searchBox = new AABB(sourcePos).inflate(0.5);
        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity itemEntity : itemEntities) {

            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) {

                continue;
            }

            if (chestHandler != null) {

                ItemStack leftover = ItemHandlerHelper.insertItem(chestHandler, stack, false);

                if (leftover.isEmpty()) {

                    itemEntity.discard();
                } else {

                    itemEntity.setItem(leftover);
                    launchFromTop(itemEntity);
                }

            } else {
                launchFromTop(itemEntity);
            }
        }
    }

    private void launchFromTop(ItemEntity itemEntity) {

        if (level == null) {

            return;
        }

        BlockPos topPos = worldPosition.above();

        double x = topPos.getX() + 0.5;
        double y = topPos.getY() + 1.0;
        double z = topPos.getZ() + 0.5;

        itemEntity.setPos(x, y, z);

        RandomSource random = level.random;
        double dx = (random.nextDouble() - 0.5) * 0.1;
        double dz = (random.nextDouble() - 0.5) * 0.1;

        itemEntity.setDeltaMovement(dx, 0.25, dz);
    }

    private void placePipe(BlockPos pipePos) {

        if (level == null) {

            return;
        }

        BlockState current = level.getBlockState(pipePos);

        if (current.isAir() || !current.getFluidState().isEmpty()) {

            BlockState pipeState = MechaniCraftBlocks.MiningPipe.get().defaultBlockState();
            level.setBlock(pipePos, pipeState, Block.UPDATE_ALL);
        }

        solidifyFluidsAround(pipePos);
    }

    private void solidifyFluidsAround(BlockPos pipePos) {

        if (level == null) {

            return;
        }

        for (Direction dir : Direction.values()) {

            BlockPos adjPos = pipePos.relative(dir);
            BlockState adjState = level.getBlockState(adjPos);
            FluidState fluidState = adjState.getFluidState();

            if (!fluidState.isEmpty() && (fluidState.is(FluidTags.WATER) || fluidState.is(FluidTags.LAVA))) {

                level.setBlock(adjPos, Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    /* MENU PROVIDER */

    @Override
    public @NotNull Component getDisplayName() {

        return MechaniCraftBlocks.MiningWell.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory playerInventory, @NotNull Player player) {

        // No GUI for now
        return null;
    }

    /* CAPABILITIES */

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.ENERGY) {

            return energyCap.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {

        super.invalidateCaps();
        energyCap.invalidate();
    }

    /* SAVE / LOAD */

    @Override
    protected void saveAdditional(CompoundTag tag) {

        super.saveAdditional(tag);

        tag.putInt("NextY", nextY);

        Tag energyTag = energy.serializeNBT();
        if (energyTag instanceof CompoundTag compoundTag) {

            tag.put("Energy", compoundTag);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {

        super.load(tag);

        nextY = tag.getInt("NextY");

        if (tag.contains("Energy")) {

            Tag energyTag = tag.get("Energy");
            energy.deserializeNBT(energyTag);
        }
    }

    public ContainerData getData() {
        return data;
    }
}