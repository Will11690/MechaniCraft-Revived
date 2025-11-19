package com.github.will11690.mechanicraft_revived.capabilities.fluid;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Flexible implementation of a Fluid Storage object.
 *
 * Originally based on Forge's FluidTank, extended with Mechanicraft-style
 * capacity & transfer upgrades (similar to MechaniCraftEnergyStorage).
 */
public class MechaniCraftFluidTank implements IFluidHandler, IFluidTank {

    /* VALIDATION */

    protected Predicate<FluidStack> validator;

    /* FLUID STATE */

    @Nonnull
    protected FluidStack fluid = FluidStack.EMPTY;

    /* CAPACITY / TRANSFER (current) */

    protected int capacity;
    protected int maxFill;
    protected int maxDrain;

    /* BASE / UPGRADED VALUES (for upgrades) */

    protected int baseCapacity;
    protected int baseFill;
    protected int baseDrain;

    protected int upgradedCapacity = 0;
    protected int upgradedFill     = 0;
    protected int upgradedDrain    = 0;

    /* UPGRADE STACKS (4 slots, like your energy storage) */

    protected int upgrade1Count = 0;
    protected int upgrade2Count = 0;
    protected int upgrade3Count = 0;
    protected int upgrade4Count = 0;

    protected ItemStack upgrade1Stack = ItemStack.EMPTY;
    protected ItemStack upgrade2Stack = ItemStack.EMPTY;
    protected ItemStack upgrade3Stack = ItemStack.EMPTY;
    protected ItemStack upgrade4Stack = ItemStack.EMPTY;

    /* CONSTRUCTORS */

    public MechaniCraftFluidTank(int capacity) {
        this(capacity, e -> true);
    }

    public MechaniCraftFluidTank(int capacity, Predicate<FluidStack> validator) {
        this.baseCapacity = capacity;
        this.capacity = capacity;
        this.validator = validator;
        // Default: no transfer limits (0 = no transfer) until configured
        this.baseFill = 0;
        this.baseDrain = 0;
        this.maxFill = 0;
        this.maxDrain = 0;
    }

    /* BASIC CONFIG */

    public MechaniCraftFluidTank setCapacity(int capacity) {
        this.capacity = capacity;

        if (fluid.getAmount() > capacity) {
            fluid.setAmount(capacity);
        }
        onContentsChanged();
        return this;
    }

    public MechaniCraftFluidTank setValidator(Predicate<FluidStack> validator) {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxFill() {
        return maxFill;
    }

    public int getMaxDrain() {
        return maxDrain;
    }

    public void setMaxFill(int maxFill) {
        this.maxFill = maxFill;
        onContentsChanged();
    }

    public void setMaxDrain(int maxDrain) {
        this.maxDrain = maxDrain;
        onContentsChanged();
    }

    @Nonnull
    public FluidStack getFluid() {
        return fluid;
    }

    public int getFluidAmount() {
        return fluid.getAmount();
    }

    /* NBT: fluid only (base/upgraded values are recomputed at runtime) */

    public MechaniCraftFluidTank readFromNBT(CompoundTag nbt) {
        FluidStack loaded = FluidStack.loadFluidStackFromNBT(nbt);
        setFluid(loaded);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        fluid.writeToNBT(nbt);
        return nbt;
    }

    /* IFluidHandler / IFluidTank IMPLEMENTATION */

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {

        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        // Respect transfer limit: if maxFill <= 0, cannot fill
        if (maxFill <= 0) {
            return 0;
        }

        if (action.simulate()) {
            if (fluid.isEmpty()) {
                int fillable = Math.min(capacity, resource.getAmount());
                fillable = Math.min(fillable, maxFill);
                return fillable;
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }

            int space = capacity - fluid.getAmount();
            if (space <= 0) {
                return 0;
            }

            int fillable = Math.min(space, resource.getAmount());
            fillable = Math.min(fillable, maxFill);
            return Math.max(fillable, 0);
        }

        // EXECUTE
        if (fluid.isEmpty()) {
            int toFill = Math.min(capacity, resource.getAmount());
            toFill = Math.min(toFill, maxFill);

            if (toFill <= 0) {
                return 0;
            }

            fluid = new FluidStack(resource, toFill);
            onContentsChanged();
            return fluid.getAmount();
        }

        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }

        int space = capacity - fluid.getAmount();
        if (space <= 0) {
            return 0;
        }

        int filled = Math.min(space, resource.getAmount());
        filled = Math.min(filled, maxFill);

        if (filled <= 0) {
            return 0;
        }

        fluid.grow(filled);
        onContentsChanged();
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {

        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }

        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrainReq, FluidAction action) {

        if (fluid.isEmpty() || maxDrainReq <= 0) {
            return FluidStack.EMPTY;
        }
        // Respect transfer limit: if maxDrain <= 0, cannot drain
        if (maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        int toDrain = Math.min(maxDrainReq, maxDrain);
        if (toDrain <= 0) {
            return FluidStack.EMPTY;
        }

        if (fluid.getAmount() < toDrain) {
            toDrain = fluid.getAmount();
        }

        if (toDrain <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack stack = new FluidStack(fluid, toDrain);

        if (action.execute()) {
            fluid.shrink(toDrain);
            onContentsChanged();
        }

        return stack;
    }

    /* HOOKS */

    protected void onContentsChanged() {
        // Override in BE wrapper to call setChanged()
    }

    public void setFluid(FluidStack stack) {
        this.fluid = stack;
        if (fluid.getAmount() > capacity) {
            fluid.setAmount(capacity);
        }
        onContentsChanged();
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    public int getSpace() {
        return Math.max(0, capacity - fluid.getAmount());
    }

    /* ---------- Mechanicraft upgrade support (capacity + transfer) ---------- */

    /**
     * Simple no-upgrade setup, similar to updateEnergyStorageNoUpgrades.
     */
    public void updateFluidTankNoUpgrades(int capacity, int fill, int drain) {

        this.baseCapacity = capacity;
        this.baseFill = fill;
        this.baseDrain = drain;

        if (this.capacity <= 0 && baseCapacity > 0) {
            setCapacity(baseCapacity);
        }

        if (this.maxFill <= 0 && baseFill > 0) {
            setMaxFill(baseFill);
        }

        if (this.maxDrain <= 0 && baseDrain > 0) {
            setMaxDrain(baseDrain);
        }
    }

    /**
     * Unified upgrade logic, mirroring MechaniCraftEnergyStorage.updateEnergyStorageWithUpgrades:
     * - Count CAPACITY_UPGRADE stacks across upgrade1-4
     * - Count TRANSFER_UPGRADE stacks across upgrade1-4
     * - Each upgrade is +10% of base for its category.
     */
    public void updateFluidTankWithUpgrades(int capacity, int fill, int drain) {

        this.baseCapacity = capacity;
        this.baseFill = fill;
        this.baseDrain = drain;

        // Ensure base values are at least applied once
        if (this.baseCapacity > 0 && this.capacity < this.baseCapacity) {
            setCapacity(baseCapacity);
        }

        if (this.baseFill > 0 && this.maxFill < this.baseFill) {
            setMaxFill(baseFill);
        }

        if (this.baseDrain > 0 && this.maxDrain < this.baseDrain) {
            setMaxDrain(baseDrain);
        }

        // Per-slot counts (for debug / UI if needed)
        upgrade1Count = upgrade1Stack.isEmpty() ? 0 : upgrade1Stack.getCount();
        upgrade2Count = upgrade2Stack.isEmpty() ? 0 : upgrade2Stack.getCount();
        upgrade3Count = upgrade3Stack.isEmpty() ? 0 : upgrade3Stack.getCount();
        upgrade4Count = upgrade4Stack.isEmpty() ? 0 : upgrade4Stack.getCount();

        ItemStack[] upgrades = { upgrade1Stack, upgrade2Stack, upgrade3Stack, upgrade4Stack };

        int totalCapacityUpgrades = 0;
        int totalTransferUpgrades = 0;

        for (ItemStack upgrade : upgrades) {
            if (upgrade.isEmpty()) {
                continue;
            }

            if (upgrade.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())) {
                totalCapacityUpgrades += upgrade.getCount();
            } else if (upgrade.getItem().equals(MechaniCraftItems.TransferUpgrade.get())) {
                totalTransferUpgrades += upgrade.getCount();
            }
        }

        if (totalCapacityUpgrades <= 0 && totalTransferUpgrades <= 0) {
            // Default (no upgrades)
            setCapacity(baseCapacity);
            setMaxFill(baseFill);
            setMaxDrain(baseDrain);
            onContentsChanged();
            return;
        }

        double capacityMultiplier = 1.0D + (0.10D * totalCapacityUpgrades);
        double transferMultiplier = 1.0D + (0.10D * totalTransferUpgrades);

        int newCapacity = baseCapacity;
        int newFill = baseFill;
        int newDrain = baseDrain;

        if (totalCapacityUpgrades > 0) {
            newCapacity = (int) (baseCapacity * capacityMultiplier);
        }

        if (totalTransferUpgrades > 0) {
            if (baseFill > 0) {
                newFill = (int) (baseFill * transferMultiplier);
            }
            if (baseDrain > 0) {
                newDrain = (int) (baseDrain * transferMultiplier);
            }
        }

        upgradedCapacity = newCapacity;
        upgradedFill = newFill;
        upgradedDrain = newDrain;

        setCapacity(upgradedCapacity);
        setMaxFill(upgradedFill);
        setMaxDrain(upgradedDrain);
        onContentsChanged();
    }

    /* Base / upgraded getters & setters */

    public int getBaseCapacity() {
        return this.baseCapacity;
    }

    public int getBaseFill() {
        return this.baseFill;
    }

    public int getBaseDrain() {
        return this.baseDrain;
    }

    public int getUpgradedCapacity() {
        return this.upgradedCapacity;
    }

    public int getUpgradedFill() {
        return this.upgradedFill;
    }

    public int getUpgradedDrain() {
        return this.upgradedDrain;
    }

    public int setBaseCapacity(int baseCapacity) {
        this.baseCapacity = baseCapacity;
        return this.baseCapacity;
    }

    public int setBaseFill(int baseFill) {
        this.baseFill = baseFill;
        return this.baseFill;
    }

    public int setBaseDrain(int baseDrain) {
        this.baseDrain = baseDrain;
        return this.baseDrain;
    }

    public int setUpgradedCapacity(int upgradedCapacity) {
        this.upgradedCapacity = upgradedCapacity;
        return this.upgradedCapacity;
    }

    public int setUpgradedFill(int upgradedFill) {
        this.upgradedFill = upgradedFill;
        return this.upgradedFill;
    }

    public int setUpgradedDrain(int upgradedDrain) {
        this.upgradedDrain = upgradedDrain;
        return this.upgradedDrain;
    }

    /* Upgrade stacks */

    public ItemStack getUpgrade1Stack() {
        return upgrade1Stack;
    }

    public ItemStack getUpgrade2Stack() {
        return upgrade2Stack;
    }

    public ItemStack getUpgrade3Stack() {
        return upgrade3Stack;
    }

    public ItemStack getUpgrade4Stack() {
        return upgrade4Stack;
    }

    public ItemStack setUpgrade1Stack(ItemStack upgrade1) {
        upgrade1Stack = upgrade1;
        return upgrade1Stack;
    }

    public ItemStack setUpgrade2Stack(ItemStack upgrade2) {
        upgrade2Stack = upgrade2;
        return upgrade2Stack;
    }

    public ItemStack setUpgrade3Stack(ItemStack upgrade3) {
        upgrade3Stack = upgrade3;
        return upgrade3Stack;
    }

    public ItemStack setUpgrade4Stack(ItemStack upgrade4) {
        upgrade4Stack = upgrade4;
        return upgrade4Stack;
    }

    public int getUpgrade1Count() {
        return upgrade1Count;
    }

    public int getUpgrade2Count() {
        return upgrade2Count;
    }

    public int getUpgrade3Count() {
        return upgrade3Count;
    }

    public int getUpgrade4Count() {
        return upgrade4Count;
    }
}