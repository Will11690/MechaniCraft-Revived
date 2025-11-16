package com.github.will11690.mechanicraft_revived.capabilities.energy;

import java.util.function.BooleanSupplier;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class MechaniCraftEnergyStorage extends EnergyStorage implements INBTSerializable<Tag> {

    protected BooleanSupplier creative;

    protected int baseCapacity;
    protected int baseReceive;
    protected int baseExtract;

    protected int upgradedCapacity = 0;
    protected int upgradedExtract = 0;
    protected int upgradedReceive = 0;

    protected int upgrade1Count = 0;
    protected int upgrade2Count = 0;
    protected int upgrade3Count = 0;
    protected int upgrade4Count = 0;

    protected ItemStack upgrade1Stack = ItemStack.EMPTY;
    protected ItemStack upgrade2Stack = ItemStack.EMPTY;
    protected ItemStack upgrade3Stack = ItemStack.EMPTY;
    protected ItemStack upgrade4Stack = ItemStack.EMPTY;

    public MechaniCraftEnergyStorage(final int setCapacity) {

        super(setCapacity);

        capacity = setCapacity;
    }

    public MechaniCraftEnergyStorage(final int setCapacity, final int setMaxTransfer) {

        super(setCapacity, setMaxTransfer);

        capacity = setCapacity;
        maxReceive = setMaxTransfer;
        maxExtract = setMaxTransfer;
    }

    public MechaniCraftEnergyStorage(final int setCapacity, final int setMaxReceive, final int setMaxExtract) {

        super(setCapacity, setMaxReceive, setMaxExtract);

        capacity = setCapacity;
        maxReceive = setMaxReceive;
        maxExtract = setMaxExtract;
    }

    public MechaniCraftEnergyStorage(final int setCapacity, final int setMaxReceive, final int setMaxExtract, final int setEnergy) {

        super(setCapacity, setMaxReceive, setMaxExtract, setEnergy);

        capacity = setCapacity;
        maxReceive = setMaxReceive;
        maxExtract = setMaxExtract;

        energy = Math.max(0, Math.min(setCapacity, setEnergy));
    }

    @Override
    public Tag serializeNBT() {

        CompoundTag tag = new CompoundTag();

        tag.putInt("energy", getEnergyStored());
        tag.putInt("baseCapacity", getBaseCapacity());
        tag.putInt("upgradedCapacity", getUpgradedCapacity());
        tag.putInt("capacity", getCapacity());
        tag.putInt("baseReceive", getBaseReceive());
        tag.putInt("upgradedReceive", getUpgradedReceive());
        tag.putInt("maxReceive", getMaxReceive());
        tag.putInt("baseExtract", getBaseExtract());
        tag.putInt("upgradedExtract", getUpgradedExtract());
        tag.putInt("maxExtract", getMaxExtract());

        if(!upgrade1Stack.isEmpty()
                && (upgrade1Stack.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())
                || upgrade1Stack.getItem().equals(MechaniCraftItems.TransferUpgrade.get()))) {

            CompoundTag upgradeOneTag = new CompoundTag();
            upgrade1Stack.save(upgradeOneTag);
            tag.put("upgradeOne", upgradeOneTag);
        }

        if(!upgrade2Stack.isEmpty()
                && (upgrade2Stack.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())
                || upgrade2Stack.getItem().equals(MechaniCraftItems.TransferUpgrade.get()))) {

            CompoundTag upgradeTwoTag = new CompoundTag();
            upgrade2Stack.save(upgradeTwoTag);
            tag.put("upgradeTwo", upgradeTwoTag);
        }

        if(!upgrade3Stack.isEmpty()
                && (upgrade3Stack.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())
                || upgrade3Stack.getItem().equals(MechaniCraftItems.TransferUpgrade.get()))) {

            CompoundTag upgradeThreeTag = new CompoundTag();
            upgrade3Stack.save(upgradeThreeTag);
            tag.put("upgradeThree", upgradeThreeTag);
        }

        if(!upgrade4Stack.isEmpty()
                && (upgrade4Stack.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())
                || upgrade4Stack.getItem().equals(MechaniCraftItems.TransferUpgrade.get()))) {

            CompoundTag upgradeFourTag = new CompoundTag();
            upgrade4Stack.save(upgradeFourTag);
            tag.put("upgradeFour", upgradeFourTag);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if(!(nbt instanceof CompoundTag tag)) return;

        setEnergy(tag.getInt("energy"));
        setBaseCapacity(tag.getInt("baseCapacity"));
        setUpgradedCapacity(tag.getInt("upgradedCapacity"));
        setCapacity(tag.getInt("capacity"));
        setBaseReceive(tag.getInt("baseReceive"));
        setUpgradedReceive(tag.getInt("upgradedReceive"));
        setMaxReceive(tag.getInt("maxReceive"));
        setBaseExtract(tag.getInt("baseExtract"));
        setUpgradedExtract(tag.getInt("upgradedExtract"));
        setMaxExtract(tag.getInt("maxExtract"));

        if(tag.contains("upgradeOne")) {
            CompoundTag upgradeOneTag = tag.getCompound("upgradeOne");
            upgrade1Stack = ItemStack.of(upgradeOneTag);
        }

        if(tag.contains("upgradeTwo")) {
            CompoundTag upgradeTwoTag = tag.getCompound("upgradeTwo");
            upgrade2Stack = ItemStack.of(upgradeTwoTag);
        }

        if(tag.contains("upgradeThree")) {
            CompoundTag upgradeThreeTag = tag.getCompound("upgradeThree");
            upgrade3Stack = ItemStack.of(upgradeThreeTag);
        }

        if(tag.contains("upgradeFour")) {
            CompoundTag upgradeFourTag = tag.getCompound("upgradeFour");
            upgrade4Stack = ItemStack.of(upgradeFourTag);
        }
    }

    // TODO Test implementation on upgradeable machine

    public void setCreative(BooleanSupplier creativeUpgrade, int maxCreativeReceive, int maxCreativeExtract) {

        creative = creativeUpgrade;

        if(isCreative()) {

            energy = capacity;
            maxReceive = maxCreativeReceive;
            maxExtract = maxCreativeExtract;
            onEnergyChanged();
        }
    }

    public void consumeEnergy(int consumedEnergy) {

        if(energy > 0) {

            energy -= consumedEnergy;
            if(energy < 0) {
                energy = 0;
            }

            onEnergyChanged();
        } else {

            energy = 0;
            onEnergyChanged();
        }
    }

    public void addEnergy(int addedEnergy) {

        if(energy < capacity && energy + addedEnergy <= capacity) {

            energy += addedEnergy;
            onEnergyChanged();

        } else {

            energy = capacity;
            onEnergyChanged();
        }
    }

    public int getMaxReceive() {

        return maxReceive;
    }

    public void setMaxReceive(final int setReceive) {

        maxReceive = setReceive;
        onEnergyChanged();
    }

    public int getMaxExtract() {

        return maxExtract;
    }

    public void setMaxExtract(final int setExtract) {

        maxExtract = setExtract;
        onEnergyChanged();
    }

    public int getCapacity() {

        return capacity;
    }

    public void setCapacity(int setCapacity) {

        capacity = setCapacity;
        if(energy > capacity) {
            setEnergy(capacity);
        }
        onEnergyChanged();
    }

    public boolean isCreative() {

        return creative != null && creative.getAsBoolean();
    }

    public void setEnergy(final int setEnergy) {

        energy = setEnergy;
        onEnergyChanged();
    }

    /**
     * Capped by max receive
     */
    public int setEnergyStored(final int setEnergy, final boolean simulate) {

        final int toSet = Math.min(setEnergy, maxReceive);

        if(!simulate) {

            energy = toSet;
            onEnergyChanged();
        }

        return toSet;
    }

    /**
     * Override this in TE to use setChanged() for Energy Storage
     */
    protected void onEnergyChanged() {

    }

    /**
     * Simple no-upgrade setup.
     */
    public void updateEnergyStorageNoUpgrades(int capacity, int receive, int extract) {

        this.baseCapacity = capacity;
        this.baseExtract = extract;
        this.baseReceive = receive;

        if(this.capacity <= 0 && baseCapacity > 0) {

            setCapacity(baseCapacity);
        }

        if(this.maxExtract <= 0 && baseExtract > 0) {

            setMaxExtract(baseExtract);
        }

        if(this.maxReceive <= 0 && baseReceive > 0) {

            setMaxReceive(baseReceive);
        }
    }

    /**
     * Unified upgrade logic:
     * - Count CAPACITY_UPGRADE stacks across upgrade1-4
     * - Count TRANSFER_UPGRADE stacks across upgrade1-4
     * - Each upgrade is +10% of base for its category.
     */
    public void updateEnergyStorageWithUpgrades(int capacity, int receive, int extract) {

        this.baseCapacity = capacity;
        this.baseExtract = extract;
        this.baseReceive = receive;

        // Ensure base values are at least applied once
        if(this.baseCapacity > 0 && this.capacity < this.baseCapacity) {

            setCapacity(baseCapacity);
        }

        if(this.baseExtract > 0 && this.maxExtract < this.baseExtract) {

            setMaxExtract(baseExtract);
        }

        if(this.baseReceive > 0 && this.maxReceive < this.baseReceive) {

            setMaxReceive(baseReceive);
        }

        // Per-slot counts (for debug / UI if you need them)
        upgrade1Count = upgrade1Stack.isEmpty() ? 0 : upgrade1Stack.getCount();
        upgrade2Count = upgrade2Stack.isEmpty() ? 0 : upgrade2Stack.getCount();
        upgrade3Count = upgrade3Stack.isEmpty() ? 0 : upgrade3Stack.getCount();
        upgrade4Count = upgrade4Stack.isEmpty() ? 0 : upgrade4Stack.getCount();

        ItemStack[] upgrades = { upgrade1Stack, upgrade2Stack, upgrade3Stack, upgrade4Stack };

        int totalCapacityUpgrades = 0;
        int totalTransferUpgrades = 0;

        for(ItemStack upgrade : upgrades) {

            if(upgrade.isEmpty()) {
                continue;
            }

            if(upgrade.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())) {

                totalCapacityUpgrades += upgrade.getCount();
            } else if(upgrade.getItem().equals(MechaniCraftItems.TransferUpgrade.get())) {

                totalTransferUpgrades += upgrade.getCount();
            }
        }

        if(totalCapacityUpgrades <= 0 && totalTransferUpgrades <= 0) {

            // Default (no upgrades)
            setCapacity(baseCapacity);
            setMaxExtract(baseExtract);
            setMaxReceive(baseReceive);
            onEnergyChanged();
            return;
        }

        double capacityMultiplier = 1.0D + (0.10D * totalCapacityUpgrades);
        double transferMultiplier = 1.0D + (0.10D * totalTransferUpgrades);

        int newCapacity = baseCapacity;
        int newExtract = baseExtract;
        int newReceive = baseReceive;

        if(totalCapacityUpgrades > 0) {

            newCapacity = (int) (baseCapacity * capacityMultiplier);
        }

        if(totalTransferUpgrades > 0) {

            if(baseExtract > 0) {
                newExtract = (int) (baseExtract * transferMultiplier);
            }

            if(baseReceive > 0) {
                newReceive = (int) (baseReceive * transferMultiplier);
            }
        }

        upgradedCapacity = newCapacity;
        upgradedExtract = newExtract;
        upgradedReceive = newReceive;

        setCapacity(upgradedCapacity);
        setMaxExtract(upgradedExtract);
        setMaxReceive(upgradedReceive);
        onEnergyChanged();
    }

    /* Base / upgraded getters & setters */

    public int getBaseCapacity() {

        return this.baseCapacity;
    }

    public int getBaseExtract() {

        return this.baseExtract;
    }

    public int getBaseReceive() {

        return this.baseReceive;
    }

    public int getUpgradedCapacity() {

        return this.upgradedCapacity;
    }

    public int getUpgradedExtract() {

        return this.upgradedExtract;
    }

    public int getUpgradedReceive() {

        return this.upgradedReceive;
    }

    public int setBaseCapacity(int baseCapacity) {

        return this.baseCapacity = baseCapacity;
    }

    public int setBaseExtract(int baseExtract) {

        return this.baseExtract = baseExtract;
    }

    public int setBaseReceive(int baseReceive) {

        return this.baseReceive = baseReceive;
    }

    public int setUpgradedCapacity(int upgradedCapacity) {

        return this.upgradedCapacity = upgradedCapacity;
    }

    public int setUpgradedExtract(int upgradedExtract) {

        return this.upgradedExtract = upgradedExtract;
    }

    public int setUpgradedReceive(int upgradedReceive) {

        return this.upgradedReceive = upgradedReceive;
    }

    /* Upgrade stacks and per-slot counts */

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

    public boolean canExtractFromSlot(int energy) {

        return energy <= baseCapacity;
    }
}