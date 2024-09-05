package net.github.will11690.mechanicraft_revived.capabilities.energy;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;

public class MechaniCraftEnergyStorage extends EnergyStorage {

    int energy;
    int capacity;
    int maxReceive;
    int maxExtract;

    public MechaniCraftEnergyStorage(int capacity) {

        super(capacity);

        this.capacity = capacity;
    }

    public MechaniCraftEnergyStorage(int capacity, int maxTransfer) {

        super(capacity, maxTransfer);

        this.capacity = capacity;
        this.maxReceive = maxTransfer;
        this.maxExtract = maxTransfer;
    }

    public MechaniCraftEnergyStorage(int capacity, int maxReceive, int maxExtract) {

        super(capacity, maxReceive, maxExtract);

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public MechaniCraftEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {

        super(capacity, maxReceive, maxExtract, energy);

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = energy;
    }

    //Will11690 start
    public int receiveEnergyInternal(int maxReceive) {
        //TODO
        return maxReceive;
    }

    public int extractEnergyInternal(int maxExtract) {
        //TODO
        return maxExtract;
    }

    public int setCapacity(int capacity) {
        //TODO
        return capacity;
    }

    public int setMaxExtract(int maxExtract) {
        //TODO
        return maxExtract;
    }

    public int setMaxReceive(int maxReceive) {
        //TODO
        return maxReceive;
    }

    public int setCreative(int energy, int capacity) {
        //TODO
        return energy = capacity;
    }
    //Will11690 end

    //Overrides
    public int receiveEnergy(int maxReceive, boolean simulate) {
        //TODO
        if (!this.canReceive()) {
            return 0;
        } else {
            int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
            if (!simulate) {
                this.energy += energyReceived;
            }

            return energyReceived;
        }
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        //TODO
        if (!this.canExtract()) {
            return 0;
        } else {
            int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
            if (!simulate) {
                this.energy -= energyExtracted;
            }

            return energyExtracted;
        }
    }

    public int getEnergyStored() {
        //TODO
        return this.energy;
    }

    public int getMaxEnergyStored() {
        //TODO
        return this.capacity;
    }

    public boolean canExtract() {
        //TODO
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        //TODO
        return this.maxReceive > 0;
    }

    public Tag serializeNBT() {
        //TODO
        return IntTag.valueOf(this.getEnergyStored());
    }

    public void deserializeNBT(Tag nbt) {
        //TODO
        if (nbt instanceof IntTag intNbt) {
            this.energy = intNbt.getAsInt();
        } else {
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        }
    }
}
