package com.github.will11690.mechanicraft_revived.blocks.primitive.infuser;

import com.github.will11690.mechanicraft_revived.recipe.InfuserRecipes;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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

import java.util.Collection;
import java.util.Optional;

public class PrimitiveInfuserBE extends BlockEntity implements MenuProvider {

    private final ItemStackHandler inputHandler = new ItemStackHandler(2) {
        //Inputs
        @Override
        protected void onContentsChanged(int slot) {

            if(level != null) {

                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
                setChanged();
            }
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            final int other = (slot == 0) ? 1 : 0;
            final ItemStack otherStack = getStackInSlot(other);

            final boolean input1 = isRecipeInput1(level, stack);
            final boolean input2 = isRecipeInput2(level, stack);

            if (otherStack.isEmpty()) {
                return input1 || input2;
            }

            final boolean otherSlot1 = isRecipeInput1(level, otherStack);
            final boolean otherSlot2 = isRecipeInput2(level, otherStack);

            if (!otherSlot1 && !otherSlot2) return false;

            if (slot == 0) {
                return otherSlot1 ? input2 : input1;
            }

            if (slot == 1) {
                return otherSlot2 ? input1 : input2;
            }

            return super.isItemValid(slot, stack);
        }
    };

    private final ItemStackHandler outputHandler = new ItemStackHandler(1) {
        //Output
        @Override
        protected void onContentsChanged(int slot) {

            if(level != null) {

                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
                setChanged();
            }
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

            if(level != null) {

                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
                setChanged();
            }
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
    public int maxBurnTime = 0;
    public int progress = 0;
    public int maxProgress = 200;

    public LazyOptional<IItemHandler> inventory = LazyOptional.empty();
    public LazyOptional<IItemHandler> fuelInventory = LazyOptional.empty();
    public LazyOptional<IItemHandler> craftingInventory = LazyOptional.empty();

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

    public static Iterable<InfuserRecipes> getRecipes(Level level) {

        Collection<InfuserRecipes> unfilteredRecipes = level.getRecipeManager().getAllRecipesFor(InfuserRecipes.InfuserType.INSTANCE);

        return Iterables.filter(unfilteredRecipes, InfuserRecipes.class);
    }

    public static boolean isRecipeInput1(Level level, ItemStack stack) {

        for (InfuserRecipes recipe : getRecipes(level)) {

            if (recipe.getInput1().test(stack)) {

                return true;
            }
        }
        return false;
    }

    public static boolean isRecipeInput2(Level level, ItemStack stack) {

        for (InfuserRecipes recipe : getRecipes(level)) {

            if (recipe.getInput2().test(stack)) {

                return true;
            }
        }
        return false;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

        if(level != null) {

            if(!fuelHandler.getStackInSlot(0).isEmpty() && (maxBurnTime <= 0 || maxBurnTime != ForgeHooks.getBurnTime(fuelHandler.getStackInSlot(0), RecipeType.SMELTING))) {
                setMaxBurn();
            }

            if(burnTime <=0 && maxBurnTime > 0) {

                maxBurnTime = 0;
            }

            if(burnTime > 0) {

                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PrimitiveInfuser.LIT, Boolean.TRUE));
            }

            if(burnTime <= 0 && getBlockState().getValue(PrimitiveInfuser.LIT)) {

                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PrimitiveInfuser.LIT, Boolean.FALSE));
            }

            if(burnTime == 0 && canCraft()) {

                consumeFuel();
            }

            if(burnTime > 0 && canCraft()) {

                startCrafting();
            }

            if(burnTime > 0) {

                --burnTime;
            }

            if(progress > 0 && burnTime == 0) {

                progress -=2;
            }

            if(!canCraft() && progress > 0) {

                progress -= 2;
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {

        return MechaniCraftBlocks.PrimitiveInfuser.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory playerInventory, @NotNull Player player) {

        return new PrimitiveInfuserContainer(containerID, playerInventory, this, this.data);
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
    public void load(@NotNull CompoundTag tag) {
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

            if(level != null && this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.NORTH) {

                if(side == Direction.SOUTH) {
                    //Sided fuel slot access
                    return fuelInventory.cast();
                }
            }

            if(level != null && this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.SOUTH) {

                if(side == Direction.NORTH) {
                    //Sided fuel slot access
                    return fuelInventory.cast();
                }
            }

            if(level != null && this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.EAST) {

                if(side == Direction.WEST) {
                    //Sided fuel slot access
                    return fuelInventory.cast();
                }
            }

            if(level != null && this.level.getBlockState(this.worldPosition).getValue(PrimitiveInfuser.FACING) == Direction.WEST) {

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
                entityItem.setPickUpDelay(20);
                entityItem.setDeltaMovement(entityItem.getDeltaMovement().multiply(0, 1, 0));

                level.addFreshEntity(entityItem);
            }
        }
    }

    /* CRAFTING LOGIC START*/

    private void startCrafting() {

        SimpleContainer craftingInventory = craftingInventory();
        InfuserRecipes recipe = getCurrentRecipe().orElse(null);

        ItemStack current = craftingInventory.getItem(2);
        ItemStack output = recipe.assemble(craftingInventory, getLevel().registryAccess());

        if(burnTime > 0) {

            if(progress < maxProgress) {

                ++progress;
            }

            if(progress >= maxProgress) {

                if(current.isEmpty()) {

                    craftingInventory.setItem(2, output.copy());
                    outputHandler.setStackInSlot(0, craftingInventory.getItem(2));
                    progress = 0;
                    craftingInventory.removeItem(0, 1);
                    craftingInventory.removeItem(1, 1);

                } else {

                    if(current.getItem().equals(output.getItem()) && current.getCount() < output.getMaxStackSize()) {

                        current.grow(output.getCount());
                        outputHandler.setStackInSlot(0, craftingInventory.getItem(2));
                        progress = 0;
                        craftingInventory.removeItem(0, 1);
                        craftingInventory.removeItem(1, 1);
                    }
                }
            }
        }
    }

    private void consumeFuel() {

        if(!fuelHandler.getStackInSlot(0).isEmpty()) {

            ItemStack fuelStack = fuelHandler.getStackInSlot(0);

            if(ForgeHooks.getBurnTime(fuelHandler.getStackInSlot(0), RecipeType.SMELTING) > 0) {

                int burn = ForgeHooks.getBurnTime(fuelHandler.getStackInSlot(0), RecipeType.SMELTING);

                fuelStack.shrink(1);
                burnTime = burn;
            }
        }
    }

    private int setMaxBurn() {

        return maxBurnTime = ForgeHooks.getBurnTime(fuelHandler.getStackInSlot(0), RecipeType.SMELTING);
    }

    private boolean canCraft() {

        SimpleContainer recipeInventory = craftingInventory();
        InfuserRecipes recipe = getCurrentRecipe().orElse(null);
        ItemStack outputStack = ItemStack.EMPTY;

        if(inventory.isPresent()) {

            if(recipe != null) {

                outputStack = recipe.assemble(recipeInventory, getLevel().registryAccess()).copy();
            }
        }

        ItemStack outputHandlerStack = outputHandler.getStackInSlot(0);

        if(recipe != null &&
                (outputHandlerStack.getItem().equals(outputHandler.getStackInSlot(0).getItem()) || outputHandler.equals(ItemStack.EMPTY)) &&
                (outputHandlerStack.getCount() + outputStack.getCount() <= outputHandler.getSlotLimit(0))) {

            return true;
        }
        return false;
    }

    private SimpleContainer craftingInventory() {

        if(inventory.isPresent()) {

            return new SimpleContainer(craftingSlots.getStackInSlot(0), craftingSlots.getStackInSlot(1), craftingSlots.getStackInSlot(2));
        }
        return null;
    }

    private Optional<InfuserRecipes> getCurrentRecipe() {

        SimpleContainer recipeInventory = craftingInventory();

        return this.level.getRecipeManager().getRecipeFor(InfuserRecipes.InfuserType.INSTANCE, recipeInventory, level);
    }

    /* CRAFTING LOGIC END*/
}