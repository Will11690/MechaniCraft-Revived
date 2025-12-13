package com.github.will11690.mechanicraft_revived.blocks.machines.elite.infuser;

import com.github.will11690.mechanicraft_revived.blocks.machines.base.BaseMachineBlockEntity;
import com.github.will11690.mechanicraft_revived.capabilities.upgrade.UpgradeHandlerMath;
import com.github.will11690.mechanicraft_revived.network.MechaniCraftNetwork;
import com.github.will11690.mechanicraft_revived.network.packet.client.InfuserSyncPacket;
import com.github.will11690.mechanicraft_revived.recipe.InfuserRecipes;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import com.github.will11690.mechanicraft_revived.util.block.interfac.IInfuserSync;
import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

public class EliteInfuserBlockEntity extends BaseMachineBlockEntity implements IInfuserSync {

    private static final int BASE_CAPACITY = 230_400;
    private static final int BASE_TRANSFER = 768;
    private static final int FE_PER_TICK = 96;
    private static final int BASE_TIME = 200;

    private int fePerTick = FE_PER_TICK;

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

    private final CombinedInvWrapper allSlots;

    public LazyOptional<IItemHandler> inventory = LazyOptional.empty();
    public LazyOptional<IItemHandler> craftingInventory = LazyOptional.empty();

    /* --------------------------------------------------------------------- */
    /* GUI data (synced via packet)                                          */
    /* --------------------------------------------------------------------- */

    public int progress = 0;
    public int maxProgress = BASE_TIME;

    // last synced values (server side)
    private int lastSyncedProgress = -1;
    private int lastSyncedMaxProgress = -1;
    private int lastSyncedEnergyStored = -1;
    private int lastSyncedEnergyCapacity = -1;
    private int syncCooldown = 0;

    public EliteInfuserBlockEntity(BlockPos pos, BlockState state) {

        // 2 upgrade slots at elite tier
        super(MechaniCraftBlockEntities.EliteInfuserBE.get(), pos, state, true, BASE_CAPACITY, BASE_TRANSFER, true, 2);

        ItemStackHandler battery = getBatterySlot();
        this.allSlots = new CombinedInvWrapper(inputHandler, outputHandler, battery);

        // Initialize caps here so they exist immediately
        this.inventory = LazyOptional.of(() -> allSlots);
        this.craftingInventory = LazyOptional.of(this::createAutomationHandler);
    }

    /* --------------------------------------------------------------------- */
    /* Speed / Efficiency upgrades                                           */
    /* --------------------------------------------------------------------- */

    @Override
    protected void onSpeedEfficiencyUpgradesChanged() {

        int oldMax = this.maxProgress;
        if (oldMax <= 0) {

            oldMax = BASE_TIME;
        }

        UpgradeHandlerMath.ConsumerResult result = getConsumerUpgrades(BASE_TIME, FE_PER_TICK);

        int newMax = result.timeTicks();
        int newFePerTick = result.energyCostFE();

        // Rescale current progress so upgrades apply immediately mid-craft.
        if (progress > 0 && oldMax > 0 && newMax > 0) {

            progress = (int) Math.round((double) progress * newMax / oldMax);

            if (progress >= newMax) {

                progress = newMax - 1;
            }
        }

        this.maxProgress = newMax;
        this.fePerTick = newFePerTick;

        setChanged();
    }

    /* --------------------------------------------------------------------- */
    /* Recipes                                                               */
    /* --------------------------------------------------------------------- */

    private static Iterable<InfuserRecipes> getRecipes(Level level) {

        Collection<InfuserRecipes> unfiltered = level.getRecipeManager().getAllRecipesFor(InfuserRecipes.InfuserType.INSTANCE);
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
        Optional<InfuserRecipes> ab = level.getRecipeManager().getRecipeFor(InfuserRecipes.InfuserType.INSTANCE, invAB, level);
        if (ab.isPresent()) return invAB;

        SimpleContainer invBA = new SimpleContainer(b, a, outputHandler.getStackInSlot(0));
        Optional<InfuserRecipes> ba = level.getRecipeManager().getRecipeFor(InfuserRecipes.InfuserType.INSTANCE, invBA, level);
        return ba.isPresent() ? invBA : null;
    }

    private ItemStack assembleFrom(SimpleContainer inv) {

        if (level == null) return ItemStack.EMPTY;
        Optional<InfuserRecipes> r = level.getRecipeManager().getRecipeFor(InfuserRecipes.InfuserType.INSTANCE, inv, level);
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
    /* Tick (CALLED BY BASE TICKER)                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public void tickServer() {
        if (level == null || level.isClientSide) return;

        super.tickServer();

        boolean wasLit = getBlockState().getValue(EliteInfuser.LIT);
        boolean workingThisTick = false;

        SimpleContainer match = findMatchedRecipeInventory();
        if (match == null) {

            if (progress != 0) progress = 0;
        } else {

            ItemStack output = assembleFrom(match);
            if (!canAcceptOutput(output)) {

                if (progress != 0) progress = 0;
            } else if (energy == null || energy.getEnergyStored() < fePerTick) {

                if (progress > 0) progress = Math.max(0, progress - 2);
            } else {

                energy.consumeEnergy(fePerTick);
                workingThisTick = true;

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
            }
        }

        if (wasLit != workingThisTick) {

            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(EliteInfuser.LIT, workingThisTick));
        }

        syncToClients();
    }

    private void syncToClients() {

        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (syncCooldown > 0) {

            syncCooldown--;
            return;
        }
        syncCooldown = 2;

        int stored = (energy != null) ? energy.getEnergyStored() : 0;
        int cap    = (energy != null) ? energy.getCapacity()      : 0;

        if (stored == lastSyncedEnergyStored && cap == lastSyncedEnergyCapacity && progress == lastSyncedProgress && maxProgress == lastSyncedMaxProgress) {

            return;
        }

        lastSyncedEnergyStored   = stored;
        lastSyncedEnergyCapacity = cap;
        lastSyncedProgress       = progress;
        lastSyncedMaxProgress    = maxProgress;

        MechaniCraftNetwork.sendToTracking(serverLevel, worldPosition, new InfuserSyncPacket(this));
    }

    /* --------------------------------------------------------------------- */
    /* Base drop support                                                     */
    /* --------------------------------------------------------------------- */

    @Override
    protected IItemHandler[] getAdditionalDropHandlers() {

        return new IItemHandler[] { inputHandler, outputHandler };
    }

    /* --------------------------------------------------------------------- */
    /* Menu / name                                                           */
    /* --------------------------------------------------------------------- */

    @Override
    public @NotNull Component getDisplayName() {

        return MechaniCraftBlocks.EliteInfuser.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {

        return new EliteInfuserContainer(id, inv, this);
    }

    /* --------------------------------------------------------------------- */
    /* Capabilities                                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public void invalidateCaps() {

        super.invalidateCaps();
        inventory.invalidate();
        craftingInventory.invalidate();
    }

    private IItemHandler createAutomationHandler() {

        return new IItemHandler() {

            @Override
            public int getSlots() {

                return 4;
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {

                ItemStackHandler battery = getBatterySlot();
                return switch (slot) {

                    case 0 -> inputHandler.getStackInSlot(0);
                    case 1 -> inputHandler.getStackInSlot(1);
                    case 2 -> outputHandler.getStackInSlot(0);
                    case 3 -> (battery != null ? battery.getStackInSlot(0) : ItemStack.EMPTY);
                    default -> ItemStack.EMPTY;
                };
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {

                if (stack.isEmpty()) return ItemStack.EMPTY;
                ItemStackHandler battery = getBatterySlot();

                return switch (slot) {

                    case 0, 1 -> inputHandler.insertItem(slot, stack, simulate);
                    case 2 -> outputHandler.insertItem(0, stack, simulate);
                    case 3 -> (battery != null ? battery.insertItem(0, stack, simulate) : stack);
                    default -> stack;
                };
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {

                if (amount <= 0) return ItemStack.EMPTY;
                ItemStackHandler battery = getBatterySlot();

                return switch (slot) {

                    case 2 -> outputHandler.extractItem(0, amount, simulate);
                    case 3 -> (battery != null ? battery.extractItem(0, amount, simulate) : ItemStack.EMPTY);
                    default -> ItemStack.EMPTY; // no extraction from inputs
                };
            }

            @Override
            public int getSlotLimit(int slot) {

                ItemStackHandler battery = getBatterySlot();
                return switch (slot) {

                    case 0, 1 -> inputHandler.getSlotLimit(slot);
                    case 2 -> outputHandler.getSlotLimit(0);
                    case 3 -> (battery != null ? battery.getSlotLimit(0) : 64);
                    default -> 64;
                };
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {

                ItemStackHandler battery = getBatterySlot();
                return switch (slot) {

                    case 0, 1 -> inputHandler.isItemValid(slot, stack);
                    case 2 -> outputHandler.isItemValid(0, stack);
                    case 3 -> (battery != null && battery.isItemValid(0, stack));
                    default -> false;
                };
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.ITEM_HANDLER) {

            if (side == null) {

                return inventory.cast();
            }
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
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {

        super.load(tag);

        if (tag.contains("inputs", Tag.TAG_COMPOUND)) inputHandler.deserializeNBT(tag.getCompound("inputs"));
        if (tag.contains("output", Tag.TAG_COMPOUND)) outputHandler.deserializeNBT(tag.getCompound("output"));

        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
    }

    /* --------------------------------------------------------------------- */
    /* IInfuserSync implementation                                           */
    /* --------------------------------------------------------------------- */

    @Override
    public BlockPos getBlockPos() {

        return this.worldPosition;
    }

    @Override
    public int getGuiProgress() {

        return progress;
    }

    @Override
    public int getGuiMaxProgress() {

        return maxProgress;
    }

    @Override
    public int getGuiEnergyStored() {

        return energy != null ? energy.getEnergyStored() : 0;
    }

    @Override
    public int getGuiEnergyCapacity() {

        return energy != null ? energy.getCapacity() : 0;
    }

    @Override
    public void applyInfuserSync(int progress, int maxProgress, int energyStored, int energyCapacity) {
        this.progress = progress;
        this.maxProgress = maxProgress;

        if (energy != null) {

            energy.setCapacity(energyCapacity);
            energy.setEnergy(energyStored);
        }
    }
}