package com.github.will11690.mechanicraft_revived.blocks.generators.basic.solidfuelgen;

import com.github.will11690.mechanicraft_revived.capabilities.energy.MechaniCraftEnergyStorage;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicSolidFuelGeneratorBE extends BlockEntity implements MenuProvider {

    public static final int ENERGY_CAPACITY = 76_800;
    public static final int MAX_RECEIVE = 0;       // Generator does not accept external FE
    public static final int MAX_EXTRACT = 256;     // FE/t out
    public static final int FE_PER_TICK = 24;      // Generation rate
    public static final int SLOT_FUEL = 0;

    private final MechaniCraftEnergyStorage energy =
            new MechaniCraftEnergyStorage(ENERGY_CAPACITY, MAX_RECEIVE, MAX_EXTRACT) {
                @Override
                protected void onEnergyChanged() {

                    BasicSolidFuelGeneratorBE.this.setChanged();
                }
            };

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);

    private final ItemStackHandler items = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {

            setChanged();
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            return isValidFuel(stack);
        }
    };

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> items);

    private int burnTime = 0;
    private int burnTimeTotal = 0;
    private boolean lastLit = false;

    private final ContainerData data = new SimpleContainerData(4) {

        @Override
        public int get(int index) {

            return switch (index) {

                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> burnTime;
                case 3 -> burnTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {

            switch (index) {

                case 0 -> energy.setEnergy(value);
                case 2 -> burnTime = value;
                case 3 -> burnTimeTotal = value;
            }
        }

        @Override
        public int getCount() {

            return 4;
        }
    };

    public BasicSolidFuelGeneratorBE(BlockPos pos, BlockState state) {

        super(MechaniCraftBlockEntities.BasicSolidFuelGeneratorBE.get(), pos, state);
        energy.updateEnergyStorageNoUpgrades(ENERGY_CAPACITY, MAX_RECEIVE, MAX_EXTRACT);
    }

    /* TICK */

    public void serverTick() {

        if (level == null || level.isClientSide()) {

            return;
        }

        boolean dirty = false;

        if(burnTime <= 0 && !isEnergyFull()) {

            tryStartBurn();
            if(burnTime > 0) {

                dirty = true;
            }
        }

        if(burnTime > 0) {

            if(!isEnergyFull()) {

                int gen = Math.min(FE_PER_TICK, energy.getMaxEnergyStored() - energy.getEnergyStored());
                if(gen > 0) {

                    energy.addEnergy(gen);
                    dirty = true;
                }
            }

            burnTime--;
            dirty = true;
        }

        if(energy.getEnergyStored() > 0 && level != null && !level.isClientSide()) {

            if(pushEnergyOut()) {

                dirty = true;
            }
        }

        boolean litNow = burnTime > 0;
        if(litNow != lastLit && level != null) {

            BlockState state = level.getBlockState(worldPosition);
            if(state.getBlock() instanceof BasicSolidFuelGenerator && state.hasProperty(BasicSolidFuelGenerator.LIT)) {

                level.setBlock(worldPosition, state.setValue(BasicSolidFuelGenerator.LIT, litNow), Block.UPDATE_ALL);
            }
            lastLit = litNow;
            dirty = true;
        }

        if(dirty) {

            setChanged();
        }
    }

    private boolean isEnergyFull() {

        return energy.getEnergyStored() >= energy.getMaxEnergyStored();
    }

    /* ENERGY IO */

    private boolean pushEnergyOut() {
        if (level == null) return false;

        boolean moved = false;

        int beforeTotal = energy.getEnergyStored();

        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;

            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
                    .ifPresent(otherStorage -> {
                        if (!otherStorage.canReceive()) return;

                        int toSend = Math.min(energy.getEnergyStored(), MAX_EXTRACT);
                        if (toSend <= 0) return;

                        int received = otherStorage.receiveEnergy(toSend, false);
                        if (received > 0) {
                            int before = energy.getEnergyStored();
                            energy.extractEnergy(received, false);
                        }
                    });
        }

        moved = energy.getEnergyStored() < beforeTotal;
        return moved;
    }

    /* FUEL HANDLING */

    private void tryStartBurn() {

        ItemStack fuel = items.getStackInSlot(SLOT_FUEL);
        if(fuel.isEmpty()) return;

        int time = getFuelTimeExcludingLava(fuel);
        if(time <= 0) return;

        burnTime = burnTimeTotal = time;

        ItemStack remainder = fuel.getCraftingRemainingItem();
        fuel.shrink(1);

        if(!remainder.isEmpty() && level != null) {

            ItemStack leftover = ItemHandlerHelper.insertItem(items, remainder, false);
            if(!leftover.isEmpty()) {

                Block.popResource(level, worldPosition.above(), leftover);
            }
        }
    }

    public static boolean isValidFuel(ItemStack stack) {

        return getFuelTimeExcludingLava(stack) > 0;
    }

    public static int getFuelTimeExcludingLava(ItemStack stack) {

        Item item = stack.getItem();
        if(item == Items.LAVA_BUCKET) return 0;

        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }

    /* MENU PROVIDER */

    @Override
    public @NotNull Component getDisplayName() {

        return MechaniCraftBlocks.BasicSolidFuelGenerator.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory playerInventory, @NotNull Player player) {

        return new BasicSolidFuelGeneratorContainer(containerID, playerInventory, this, this.data);
    }

    /* CAPABILITIES */

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if(cap == ForgeCapabilities.ITEM_HANDLER) {

            return itemCap.cast();
        }

        if(cap == ForgeCapabilities.ENERGY) {

            return energyCap.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {

        super.invalidateCaps();
        energyCap.invalidate();
        itemCap.invalidate();
    }

    /* SAVE / LOAD */

    @Override
    protected void saveAdditional(CompoundTag tag) {

        super.saveAdditional(tag);

        tag.put("Items", items.serializeNBT());
        tag.putInt("Burn", burnTime);
        tag.putInt("BurnTotal", burnTimeTotal);

        Tag energyTag = energy.serializeNBT();
        if(energyTag instanceof CompoundTag compoundTag) {

            tag.put("Energy", compoundTag);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {

        super.load(tag);

        if(tag.contains("Items")) {

            items.deserializeNBT(tag.getCompound("Items"));
        }

        burnTime = tag.getInt("Burn");
        burnTimeTotal = tag.getInt("BurnTotal");

        if(tag.contains("Energy")) {

            Tag energyTag = tag.get("Energy");
            energy.deserializeNBT(energyTag);
        }
    }
}