package com.github.will11690.mechanicraft_revived.blocks.primitive.infuser;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrimitiveInfuserBE extends BlockEntity implements MenuProvider {

    //TODO add functionality, BE works and saves data to the block(data is lost on world reload, need to fix this!). Now just need to make the recipes and smelting logic

    private final ItemStackHandler inputHandler = new ItemStackHandler(2) {
        //Inputs
        @Override
        protected void onContentsChanged(int slot) {

            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            //TODO Get recipe inputs and make them only valid inputs for these slots
            return super.isItemValid(slot, stack);
        }
    };

    private final ItemStackHandler outputHandler = new ItemStackHandler(1) {
        //Output
        @Override
        protected void onContentsChanged(int slot) {

            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
            super.onContentsChanged(slot);
        }

        //TODO override insertItem and return false if needed
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            return false;
        }
    };

    private final ItemStackHandler fuelHandler = new ItemStackHandler(1) {
        //Fuel
        @Override
        protected void onContentsChanged(int slot) {

            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            return !(stack.getItem() instanceof BucketItem) && ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
    };

    private final CombinedInvWrapper allSlots = new CombinedInvWrapper(inputHandler, outputHandler, fuelHandler);
    private final CombinedInvWrapper craftingSlots = new CombinedInvWrapper(inputHandler, outputHandler);

    //TODO replace with packets
    protected final ContainerData data;
    public int burnTime = 0;
    //TODO get burn time from stack in fuel slot and set accordingly;
    public int maxBurnTime = 78;
    public int progress = 0;
    public int maxProgress = 78;

    public LazyOptional<IItemHandler> inventory = LazyOptional.empty();
    public LazyOptional<IItemHandler> fuelInventory = LazyOptional.empty();
    public LazyOptional<IItemHandler> craftingInventory = LazyOptional.empty();

    public static final int INPUT1 = 0;
    public static final int INPUT2 = 1;
    public static final int OUTPUT = 2;
    public static final int FUEL = 3;

    public PrimitiveInfuserBE(BlockPos pos, BlockState state) {

        super(MechaniCraftBlockEntities.PrimitiveInfuserBE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {

                return switch(index) {

                    case 0 -> PrimitiveInfuserBE.this.progress;
                    case 1 -> PrimitiveInfuserBE.this.maxProgress;
                    case 2 -> PrimitiveInfuserBE.this.burnTime;
                    case 3 -> PrimitiveInfuserBE.this.maxBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {

                switch (index) {

                    case 0 -> PrimitiveInfuserBE.this.progress = value;
                    case 1 -> PrimitiveInfuserBE.this.maxProgress = value;
                    case 2 -> PrimitiveInfuserBE.this.burnTime = value;
                    case 3 -> PrimitiveInfuserBE.this.maxBurnTime = value;
                };
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {

        return MechaniCraftBlocks.PrimitiveInfuser.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, Inventory playerInventory, Player player) {
        return new PrimitiveInfuserContainer(containerID, playerInventory, this, this.data);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    @Override
    public void onLoad() {
        super.onLoad();
        inventory = LazyOptional.of(() -> allSlots);
        craftingInventory = LazyOptional.of(() -> craftingSlots);
        fuelInventory = LazyOptional.of(() -> fuelHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventory.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {

        tag.put("inputs", inputHandler.serializeNBT());
        tag.put("output", outputHandler.serializeNBT());
        tag.put("fuel", fuelHandler.serializeNBT());
        tag.putInt("progress", this.progress);
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("maxBurnTime", this.maxBurnTime);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        inputHandler.deserializeNBT(tag.getCompound("inputs"));
        outputHandler.deserializeNBT(tag.getCompound("output"));
        fuelHandler.deserializeNBT(tag.getCompound("fuel"));
        progress = tag.getInt("progress");
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if(cap == ForgeCapabilities.ITEM_HANDLER) {

            if(side == null || level != null && level.getBlockState(worldPosition).getBlock() != getBlockState().getBlock()) {
                //Break block
                return inventory.cast();
            }

            if(this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.NORTH) {

                if(side == Direction.SOUTH) {
                    //Sided fuel slot access
                    return fuelInventory.cast();
                }
            }

            if(this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.SOUTH) {

                if(side == Direction.NORTH) {
                    //Sided fuel slot access
                    return fuelInventory.cast();
                }
            }

            if(this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.EAST) {

                if(side == Direction.WEST) {
                    //Sided fuel slot access
                    return fuelInventory.cast();
                }
            }

            if(this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.WEST) {

                if(side == Direction.EAST) {
                    //Sided fuel slot access
                    return fuelInventory.cast();
                }
            }

            return craftingInventory.cast();
        }

            return super.getCapability(cap, side);
    }

    public void dropInventory(Level level, BlockPos pos) {

        if(level != null) {

            for(int i = 0; allSlots.getSlots() > i; i++) {

                if (i > allSlots.getSlots()) {

                    break;
                }
                if(allSlots.getStackInSlot(i).isEmpty()) {

                    continue;
                }
                ItemEntity entityItem = new ItemEntity(level, pos.getX(), pos.getY() + 0.5, pos.getZ(), allSlots.getStackInSlot(i));
                entityItem.setPickUpDelay(40);
                entityItem.setDeltaMovement(entityItem.getDeltaMovement().multiply(0, 1, 0));

                level.addFreshEntity(entityItem);
            }
        }
    }
}
