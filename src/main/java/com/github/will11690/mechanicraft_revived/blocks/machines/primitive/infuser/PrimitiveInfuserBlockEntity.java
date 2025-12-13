package com.github.will11690.mechanicraft_revived.blocks.machines.primitive.infuser;

import com.github.will11690.mechanicraft_revived.blocks.machines.base.BaseMachineBlockEntity;
import com.github.will11690.mechanicraft_revived.recipe.InfuserRecipes;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
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

public class PrimitiveInfuserBlockEntity extends BaseMachineBlockEntity {

    /* --------------------------------------------------------------------- */
    /* Inventories                                                           */
    /* --------------------------------------------------------------------- */

    protected final ItemStackHandler inputHandler = new ItemStackHandler(2) {

        @Override
        protected void onContentsChanged(int slot) {

            if (level != null) {

                BlockState st = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, st, st, 3);
                setChanged();

                if (!canCraft()) progress = 0;
            }
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            if (level == null) return false;

            if (!isUsedInAnyRecipe(level, stack)) return false;

            int otherSlot = (slot == 0) ? 1 : 0;
            ItemStack other = getStackInSlot(otherSlot);

            if (other.isEmpty()) return true;

            return comboMatchesAnyRecipe(level, stack, other);
        }
    };

    protected final ItemStackHandler outputHandler = new ItemStackHandler(1) {

        @Override
        protected void onContentsChanged(int slot) {

            if (level != null) {

                BlockState st = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, st, st, 3);
                setChanged();
            }
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            return false;
        }
    };

    protected final ItemStackHandler fuelHandler = new ItemStackHandler(1) {

        @Override
        protected void onContentsChanged(int slot) {

            if (level != null) {

                BlockState st = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, st, st, 3);
                setChanged();
            }
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            return !(stack.getItem() instanceof BucketItem)
                    && ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
    };

    private final CombinedInvWrapper allSlots = new CombinedInvWrapper(inputHandler, outputHandler, fuelHandler);
    private final CombinedInvWrapper craftingSlots = new CombinedInvWrapper(inputHandler, outputHandler);

    public LazyOptional<IItemHandler> inventory = LazyOptional.empty();         // GUI / internal
    public LazyOptional<IItemHandler> fuelInventory = LazyOptional.empty();     // fuel side (back)
    public LazyOptional<IItemHandler> craftingInventory = LazyOptional.empty(); // automation IO (inputs+output)

    /* --------------------------------------------------------------------- */
    /* Data (GUI)                                                            */
    /* --------------------------------------------------------------------- */

    protected final ContainerData data;
    public int burnTime = 0;
    public int maxBurnTime = 0;   // persists while burning for GUI
    public int progress = 0;
    public int maxProgress = 400;

    public PrimitiveInfuserBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.PrimitiveInfuserBE.get(),
                pos, state, false, 0, 0, false, 0);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {

                return switch (index) {

                    case 0 -> PrimitiveInfuserBlockEntity.this.progress;
                    case 1 -> PrimitiveInfuserBlockEntity.this.maxProgress;
                    case 2 -> PrimitiveInfuserBlockEntity.this.burnTime;
                    case 3 -> PrimitiveInfuserBlockEntity.this.maxBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {

                switch (index) {

                    case 0 -> PrimitiveInfuserBlockEntity.this.progress = value;
                    case 1 -> PrimitiveInfuserBlockEntity.this.maxProgress = value;
                    case 2 -> PrimitiveInfuserBlockEntity.this.burnTime = value;
                    case 3 -> PrimitiveInfuserBlockEntity.this.maxBurnTime = value;
                }
            }

            @Override
            public int getCount() { return 4; }
        };
    }

    /* --------------------------------------------------------------------- */
    /* Recipes                                                               */
    /* --------------------------------------------------------------------- */

    private static Iterable<InfuserRecipes> getRecipes(Level level) {

        Collection<InfuserRecipes> unfiltered =
                level.getRecipeManager().getAllRecipesFor(InfuserRecipes.InfuserType.INSTANCE);
        return Iterables.filter(unfiltered, InfuserRecipes.class);
    }

    private static boolean isUsedInAnyRecipe(Level level, ItemStack stack) {

        for (InfuserRecipes r : getRecipes(level)) {

            if (r.getInput1().test(stack) || r.getInput2().test(stack)) return true;
        }
        return false;
    }

    private static boolean comboMatchesAnyRecipe(Level level, ItemStack a, ItemStack b) {

        for (InfuserRecipes r : getRecipes(level)) {

            boolean ab = r.getInput1().test(a) && r.getInput2().test(b);
            boolean ba = r.getInput1().test(b) && r.getInput2().test(a);
            if (ab || ba) return true;
        }
        return false;
    }

    @Nullable
    private SimpleContainer findMatchedRecipeInventory() {

        if (level == null) return null;

        ItemStack a = inputHandler.getStackInSlot(0);
        ItemStack b = inputHandler.getStackInSlot(1);
        if (a.isEmpty() || b.isEmpty()) return null;

        SimpleContainer invAB = new SimpleContainer(a, b, outputHandler.getStackInSlot(0));
        Optional<InfuserRecipes> ab =
                level.getRecipeManager().getRecipeFor(InfuserRecipes.InfuserType.INSTANCE, invAB, level);
        if (ab.isPresent()) return invAB;

        SimpleContainer invBA = new SimpleContainer(b, a, outputHandler.getStackInSlot(0));
        Optional<InfuserRecipes> ba =
                level.getRecipeManager().getRecipeFor(InfuserRecipes.InfuserType.INSTANCE, invBA, level);
        return ba.isPresent() ? invBA : null;
    }

    private ItemStack assembleFrom(SimpleContainer inv) {

        if (level == null) return ItemStack.EMPTY;
        Optional<InfuserRecipes> r =
                level.getRecipeManager().getRecipeFor(InfuserRecipes.InfuserType.INSTANCE, inv, level);
        return r.map(recipe -> recipe.assemble(inv, level.registryAccess())).orElse(ItemStack.EMPTY);
    }

    private boolean canAcceptOutput(ItemStack output) {

        if (output.isEmpty()) return false;

        ItemStack outSlot = outputHandler.getStackInSlot(0);
        if (outSlot.isEmpty()) {

            return output.getCount() <= outputHandler.getSlotLimit(0);
        }
        if (!outSlot.getItem().equals(output.getItem())) return false;
        return outSlot.getCount() + output.getCount() <= outputHandler.getSlotLimit(0);
    }

    private boolean canCraft() {

        SimpleContainer match = findMatchedRecipeInventory();
        if (match == null) return false;
        ItemStack output = assembleFrom(match);
        return canAcceptOutput(output);
    }

    /* --------------------------------------------------------------------- */
    /* Fuel / burn                                                           */
    /* --------------------------------------------------------------------- */

    private void setMaxBurnIfNeeded() {
        if (burnTime > 0) return;

        ItemStack fuel = fuelHandler.getStackInSlot(0);
        if (fuel.isEmpty()) {

            maxBurnTime = 0;
            return;
        }

        int burn = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
        maxBurnTime = Math.max(0, burn);
    }

    private void consumeFuel() {

        ItemStack fuel = fuelHandler.getStackInSlot(0);
        if (fuel.isEmpty()) return;

        int burn = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
        if (burn <= 0) return;

        fuel.shrink(1);
        burnTime = burn;
        maxBurnTime = burn;
        setChanged();
    }

    /* --------------------------------------------------------------------- */
    /* Tick (CALLED BY BASE TICKER)                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public void tickServer() {

        if (level == null || level.isClientSide) {

            return;
        }

        setMaxBurnIfNeeded();

        boolean canCraftNow = canCraft();
        boolean isLit = getBlockState().getValue(PrimitiveInfuser.LIT);

        if (burnTime > 0 && !isLit) {

            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PrimitiveInfuser.LIT, Boolean.TRUE));

        } else if (burnTime <= 0 && isLit) {

            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PrimitiveInfuser.LIT, Boolean.FALSE));
        }

        if (!canCraftNow) {

            if (progress != 0) progress = 0;
        }

        if (burnTime <= 0 && canCraftNow) {

            consumeFuel();
        }

        if (burnTime > 0 && canCraftNow) {

            SimpleContainer match = findMatchedRecipeInventory();
            if (match != null) {

                ItemStack output = assembleFrom(match);

                if (canAcceptOutput(output)) {

                    if (progress < maxProgress) progress++;

                    if (progress >= maxProgress) {

                        ItemStack outSlot = outputHandler.getStackInSlot(0);

                        if (outSlot.isEmpty()) {

                            outputHandler.setStackInSlot(0, output.copy());
                        } else {

                            outSlot.grow(output.getCount());
                            outputHandler.setStackInSlot(0, outSlot);
                        }

                        inputHandler.extractItem(0, 1, false);
                        inputHandler.extractItem(1, 1, false);
                        progress = 0;
                    }
                } else {

                    progress = 0;
                }
            }
        }

        if (burnTime > 0) burnTime--;

        if (burnTime <= 0 && canCraftNow && progress > 0) {

            progress = Math.max(0, progress - 2);
        }
    }

    /* --------------------------------------------------------------------- */
    /* Base drop support                                                     */
    /* --------------------------------------------------------------------- */

    @Override
    protected IItemHandler[] getAdditionalDropHandlers() {

        return new IItemHandler[] { inputHandler, outputHandler, fuelHandler };
    }

    /* --------------------------------------------------------------------- */
    /* Menu / name                                                           */
    /* --------------------------------------------------------------------- */

    @Override
    public @NotNull Component getDisplayName() {

        return MechaniCraftBlocks.PrimitiveInfuser.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id,
                                                      @NotNull Inventory inv,
                                                      @NotNull Player player) {

        return new PrimitiveInfuserContainer(id, inv, this, this.data);
    }

    /* --------------------------------------------------------------------- */
    /* Capabilities                                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public void onLoad() {
        super.onLoad();

        // Full view for GUI / internal use.
        inventory = LazyOptional.of(() -> allSlots);

        // Automation: IO (inputs+output, no fuel).
        craftingInventory = LazyOptional.of(this::createIoHandler);

        // Automation: fuel side (insert-only, no extract).
        fuelInventory = LazyOptional.of(this::createFuelHandler);
    }

    @Override
    public void invalidateCaps() {

        super.invalidateCaps();
        inventory.invalidate();
        craftingInventory.invalidate();
        fuelInventory.invalidate();
    }

    private IItemHandler createIoHandler() {
        // Slots:
        // 0 -> input 0
        // 1 -> input 1
        // 2 -> output 0
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 3;
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return switch (slot) {
                    case 0 -> inputHandler.getStackInSlot(0);
                    case 1 -> inputHandler.getStackInSlot(1);
                    case 2 -> outputHandler.getStackInSlot(0);
                    default -> ItemStack.EMPTY;
                };
            }

            @Override
            public @NotNull ItemStack insertItem(int slot,
                                                 @NotNull ItemStack stack,
                                                 boolean simulate) {
                if (stack.isEmpty()) return ItemStack.EMPTY;

                return switch (slot) {
                    case 0, 1 -> inputHandler.insertItem(slot, stack, simulate);
                    case 2 -> outputHandler.insertItem(0, stack, simulate);
                    default -> stack;
                };
            }

            @Override
            public @NotNull ItemStack extractItem(int slot,
                                                  int amount,
                                                  boolean simulate) {
                if (amount <= 0) return ItemStack.EMPTY;

                // Only allow extraction from output slot.
                return switch (slot) {
                    case 2 -> outputHandler.extractItem(0, amount, simulate);
                    default -> ItemStack.EMPTY;
                };
            }

            @Override
            public int getSlotLimit(int slot) {
                return switch (slot) {
                    case 0, 1 -> inputHandler.getSlotLimit(slot);
                    case 2 -> outputHandler.getSlotLimit(0);
                    default -> 64;
                };
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case 0, 1 -> inputHandler.isItemValid(slot, stack);
                    case 2 -> outputHandler.isItemValid(0, stack);
                    default -> false;
                };
            }
        };
    }

    private IItemHandler createFuelHandler() {
        // Single fuel slot, insert-only, no extract.
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return slot == 0 ? fuelHandler.getStackInSlot(0) : ItemStack.EMPTY;
            }

            @Override
            public @NotNull ItemStack insertItem(int slot,
                                                 @NotNull ItemStack stack,
                                                 boolean simulate) {
                if (slot != 0 || stack.isEmpty()) return stack;
                return fuelHandler.insertItem(0, stack, simulate);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot,
                                                  int amount,
                                                  boolean simulate) {
                // No extraction from fuel slot via pipes.
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return fuelHandler.getSlotLimit(0);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot == 0 && fuelHandler.isItemValid(0, stack);
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {

        if (cap == ForgeCapabilities.ITEM_HANDLER) {

            // Null side / mismatched block: full internal view (GUI, etc).
            if (side == null
                    || level != null
                    && level.getBlockState(worldPosition).getBlock() != getBlockState().getBlock()) {

                return inventory.cast();
            }

            // Back face -> fuel slot ONLY (insert-only, no extract).
            if (level != null) {

                Direction facing = level.getBlockState(worldPosition)
                        .getValue(PrimitiveInfuser.FACING);
                Direction back = facing.getOpposite();
                if (side == back) {

                    return fuelInventory.cast();
                }
            }

            // All other faces -> IO handler (inputs+output).
            return craftingInventory.cast();
        }

        return super.getCapability(cap, side);
    }

    /* --------------------------------------------------------------------- */
    /* Save / Load                                                           */
    /* --------------------------------------------------------------------- */

    @Override
    protected void saveAdditional(CompoundTag tag) {

        tag.put("inputs", inputHandler.serializeNBT());
        tag.put("output", outputHandler.serializeNBT());
        tag.put("fuel", fuelHandler.serializeNBT());
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putInt("burnTime", burnTime);
        tag.putInt("maxBurnTime", maxBurnTime);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.contains("inputs", Tag.TAG_COMPOUND)) inputHandler.deserializeNBT(tag.getCompound("inputs"));
        if (tag.contains("output", Tag.TAG_COMPOUND)) outputHandler.deserializeNBT(tag.getCompound("output"));
        if (tag.contains("fuel", Tag.TAG_COMPOUND)) fuelHandler.deserializeNBT(tag.getCompound("fuel"));

        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
    }
}