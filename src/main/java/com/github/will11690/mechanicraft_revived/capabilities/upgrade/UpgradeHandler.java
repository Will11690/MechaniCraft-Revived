package com.github.will11690.mechanicraft_revived.capabilities.upgrade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Concrete implementation of IUpgradeHandler.
 *
 * - Holds the upgrade inventory (variable slot count).
 * - Delegates math to UpgradeHandlerMath.
 * - Handles NBT for the inventory.
 *
 * NOTE: New constructor allows passing an external inventory so machines
 * can use one unified upgrade slot group for ALL upgrade types.
 */
public class UpgradeHandler implements IUpgradeHandler, INBTSerializable<CompoundTag> {

    private static final String NBT_INVENTORY = "Upgrades";

    private final ItemStackHandler upgrades;
    private final UpgradeHandlerMath math;

    /**
     * Default ctor: creates a new UpgradeInventory(slots).
     */
    public UpgradeHandler(int slots) {
        this.upgrades = new UpgradeInventory(slots);
        this.math = new UpgradeHandlerMath(upgrades);
    }

    /**
     * New ctor: use a provided inventory (ex: MachineUpgradeInventory).
     */
    public UpgradeHandler(ItemStackHandler externalInventory) {
        this.upgrades = externalInventory;
        this.math = new UpgradeHandlerMath(upgrades);
    }

    @Override
    public ItemStackHandler getUpgradeInventory() {
        return upgrades;
    }

    @Override
    public UpgradeHandlerMath.ConsumerResult applyToConsumer(int baseTimeTicks, int baseEnergyCostFE) {
        return math.applyToConsumer(baseTimeTicks, baseEnergyCostFE);
    }

    @Override
    public UpgradeHandlerMath.ProducerResult applyToProducer(int baseBurnTimeTicks, int baseTotalFE) {
        return math.applyToProducer(baseBurnTimeTicks, baseTotalFE);
    }

    @Override
    public int getTotalSpeed() {
        return math.getTotalSpeed();
    }

    @Override
    public int getTotalEfficiency() {
        return math.getTotalEfficiency();
    }

    // ----- NBT -----

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBT_INVENTORY, upgrades.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NBT_INVENTORY, Tag.TAG_COMPOUND)) {
            upgrades.deserializeNBT(nbt.getCompound(NBT_INVENTORY));
        }
    }
}