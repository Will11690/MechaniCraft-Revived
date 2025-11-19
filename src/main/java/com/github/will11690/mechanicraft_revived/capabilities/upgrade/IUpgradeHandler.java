package com.github.will11690.mechanicraft_revived.capabilities.upgrade;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Unified upgrade capability for Speed/Efficiency upgrades.
 *
 * - Backed by an ItemStackHandler with N slots (1–N).
 * - Can be applied to either consumers (machines) or producers (generators).
 */
public interface IUpgradeHandler extends INBTSerializable<CompoundTag> {

    /** Upgrade inventory (Speed/Efficiency slots). */
    ItemStackHandler getUpgradeInventory();

    /** Recalculate & apply upgrades for a consumer machine. */
    UpgradeHandlerMath.ConsumerResult applyToConsumer(int baseTimeTicks, int baseEnergyCostFE);

    /** Recalculate & apply upgrades for a producer (generator). */
    UpgradeHandlerMath.ProducerResult applyToProducer(int baseBurnTimeTicks, int baseTotalFE);

    /** Total Speed upgrades across all slots. */
    int getTotalSpeed();

    /** Total Efficiency upgrades across all slots. */
    int getTotalEfficiency();
}
