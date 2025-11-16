package com.github.will11690.mechanicraft_revived.blocks.powered.miningwell;

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

    // Current Y level being mined
    private int nextY;

    public MiningWellBE(BlockPos pos, BlockState state) {

        super(MechaniCraftBlockEntities.MiningWellBE.get(), pos, state);

        energy.updateEnergyStorageNoUpgrades(ENERGY_CAPACITY, MAX_RECEIVE, MAX_EXTRACT);

        // Start at the block directly below the well
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

        // If already complete, do nothing
        if (state.getValue(MiningWell.STATE) == MiningWell.WellState.COMPLETE) {
            return;
        }

        BlockPos targetPos = new BlockPos(worldPosition.getX(), nextY, worldPosition.getZ());

        // Safety: if we've gone below world min height, mark complete
        if (targetPos.getY() < level.getMinBuildHeight()) {
            level.setBlock(worldPosition,
                    state.setValue(MiningWell.STATE, MiningWell.WellState.COMPLETE),
                    Block.UPDATE_ALL);
            setChanged();
            return;
        }

        BlockState targetState = level.getBlockState(targetPos);

        // If it's air, just place a pipe and continue downward (no energy cost, no drops)
        if (targetState.isAir()) {

            placePipe(targetPos);
            nextY--;
            setChanged();
            return;
        }

        // For non-air blocks, handle mining with energy cost
        float hardness = targetState.getDestroySpeed(level, targetPos);
        if (hardness < 0) {
            // Unbreakable block - mark complete
            level.setBlock(worldPosition,
                    state.setValue(MiningWell.STATE, MiningWell.WellState.COMPLETE),
                    Block.UPDATE_ALL);
            setChanged();
            return;
        }

        int cost = (int) Math.ceil(hardness * ENERGY_PER_HARDNESS);

        // Not enough FE yet
        if (energy.getEnergyStored() < cost) {
            return;
        }

        // Spend energy
        energy.consumeEnergy(cost);

        // Mine the block normally
        if (!targetState.isAir()) {

            // Drop loot from the mined block at its position
            Block.dropResources(targetState, level, targetPos, null);
            level.destroyBlock(targetPos, false);

            // Move drops into chest above or make a fountain from the top
            handleMinedBlockDrops(targetPos);

            // Place mining pipe at the mined location
            placePipe(targetPos);
        }

        // Move down for next tick
        nextY--;

        // If we've gone past build height, mark complete
        if (nextY < level.getMinBuildHeight()) {
            level.setBlock(worldPosition,
                    state.setValue(MiningWell.STATE, MiningWell.WellState.COMPLETE),
                    Block.UPDATE_ALL);
        }

        setChanged();
    }

    /**
     * Move items dropped at the mined block position into a chest directly above the well
     * (if present). Any leftovers, or if no chest exists, get teleported to the top of the well
     * and spat out like a fountain.
     */
    private void handleMinedBlockDrops(BlockPos sourcePos) {

        if (level == null || level.isClientSide()) {
            return;
        }

        // Block entity directly above the well (chest must be ABOVE, not on sides)
        BlockPos outputPos = worldPosition.above();
        BlockEntity outputBE = level.getBlockEntity(outputPos);

        IItemHandler chestHandler = null;
        if (outputBE != null) {
            // We only ever look at the block above, so side doesn't matter here
            chestHandler = outputBE
                    .getCapability(ForgeCapabilities.ITEM_HANDLER, null)
                    .orElse(null);
        }

        // Look for item entities that spawned where the block was mined
        AABB searchBox = new AABB(sourcePos).inflate(0.5);
        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity itemEntity : itemEntities) {

            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) {
                continue;
            }

            // If we have a chest above, try to insert into it
            if (chestHandler != null) {

                ItemStack leftover = ItemHandlerHelper.insertItem(chestHandler, stack, false);

                if (leftover.isEmpty()) {
                    // Everything fit in the chest; remove the entity
                    itemEntity.discard();
                } else {
                    // Chest full or partially full; keep and fountain the leftovers
                    itemEntity.setItem(leftover);
                    launchFromTop(itemEntity);
                }

            } else {
                // No chest: fountain the items from the top of the well
                launchFromTop(itemEntity);
            }
        }
    }

    /**
     * Teleport an item entity to the top of the well and give it some upward motion
     * so it "fountains" out.
     */
    private void launchFromTop(ItemEntity itemEntity) {

        if (level == null) {
            return;
        }

        BlockPos topPos = worldPosition.above();

        double x = topPos.getX() + 0.5;
        double y = topPos.getY() + 1.0;
        double z = topPos.getZ() + 0.5;

        itemEntity.setPos(x, y, z);

        // Use the world's RandomSource; simple small spread
        RandomSource random = level.random;
        double dx = (random.nextDouble() - 0.5) * 0.1;
        double dz = (random.nextDouble() - 0.5) * 0.1;

        itemEntity.setDeltaMovement(dx, 0.25, dz);
    }

    /**
     * Place a mining pipe block at the given position (the position of the mined block or air).
     * Then solidify nearby water/lava into cobblestone.
     */
    private void placePipe(BlockPos pipePos) {

        if (level == null) {
            return;
        }

        BlockState current = level.getBlockState(pipePos);

        // Only place a pipe into air or fluid (safety)
        if (current.isAir() || !current.getFluidState().isEmpty()) {
            BlockState pipeState = MechaniCraftBlocks.MiningPipe.get().defaultBlockState();
            level.setBlock(pipePos, pipeState, Block.UPDATE_ALL);
        }

        // After placing the pipe, solidify nearby fluids into cobblestone
        solidifyFluidsAround(pipePos);
    }

    /**
     * Turn any water/lava (source or flowing) directly around the pipe into cobblestone.
     * Checks all 6 adjacent blocks.
     */
    private void solidifyFluidsAround(BlockPos pipePos) {

        if (level == null) {
            return;
        }

        for (Direction dir : Direction.values()) {

            BlockPos adjPos = pipePos.relative(dir);
            BlockState adjState = level.getBlockState(adjPos);
            FluidState fluidState = adjState.getFluidState();

            if (!fluidState.isEmpty()
                    && (fluidState.is(FluidTags.WATER) || fluidState.is(FluidTags.LAVA))) {

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
    public @Nullable AbstractContainerMenu createMenu(int containerID,
                                                      @NotNull Inventory playerInventory,
                                                      @NotNull Player player) {

        // No GUI for now
        return null;
    }

    /* CAPABILITIES */

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {

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