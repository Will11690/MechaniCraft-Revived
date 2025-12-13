package com.github.will11690.mechanicraft_revived.capabilities.upgrade;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Unified Speed/Efficiency upgrade logic for both consumers and producers,
 * backed by a dynamic-size ItemStackHandler upgrade inventory.
 *
 * CONSUMER:
 *   Speed:      time  *= 0.9  each, energy *= 1.05 each
 *   Efficiency: time  *= 1.05 each, energy *= 0.95 each
 *
 * PRODUCER:
 *   Speed:      burn  *= 0.9  each, totalFE *= 0.95 each
 *   Efficiency: burn  *= 1.05 each, totalFE *= 1.05 each
 *
 * No cap on upgrade counts; only constraint is min 1 tick for time/burn.
 */
public class UpgradeHandlerMath {

    private final ItemStackHandler upgrades;

    private int totalSpeed = 0;
    private int totalEfficiency = 0;

    public UpgradeHandlerMath(ItemStackHandler upgrades) {
        this.upgrades = upgrades;
    }

    // ---------------------------------------------------------------------
    // Counting upgrades – works for any slot count
    // ---------------------------------------------------------------------

    private void recalcCounts() {
        totalSpeed = 0;
        totalEfficiency = 0;

        int slots = upgrades.getSlots();
        for (int i = 0; i < slots; i++) {
            ItemStack stack = upgrades.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem().equals(MechaniCraftItems.SpeedUpgrade.get())) {
                totalSpeed += stack.getCount();
            } else if (stack.getItem().equals(MechaniCraftItems.EfficiencyUpgrade.get())) {
                totalEfficiency += stack.getCount();
            }
        }
    }

    public int getTotalSpeed() {
        recalcCounts();
        return totalSpeed;
    }

    public int getTotalEfficiency() {
        recalcCounts();
        return totalEfficiency;
    }

    // ---------------------------------------------------------------------
    // CONSUMER: apply to processing time + FE/t cost
    // ---------------------------------------------------------------------

    /**
     * Apply speed/efficiency to a consumer machine.
     *
     * @param baseTime    base processing time in ticks
     * @param baseEnergy  base FE/t cost
     * @return ConsumerResult with upgraded time and FE/t
     */
    public ConsumerResult applyToConsumer(int baseTime, int baseEnergy) {
        if (baseTime <= 0 || baseEnergy <= 0) {
            return new ConsumerResult(baseTime, baseEnergy);
        }

        recalcCounts();
        if (totalSpeed == 0 && totalEfficiency == 0) {
            return new ConsumerResult(baseTime, baseEnergy);
        }

        // Speed:      time *= 0.9 each, energy *= 1.05 each
        // Efficiency: time *= 1.05 each, energy *= 0.95 each
        double timeMultiplier =
                Math.pow(0.9, totalSpeed) *
                        Math.pow(1.05, totalEfficiency);

        double energyMultiplier =
                Math.pow(1.05, totalSpeed) *
                        Math.pow(0.95, totalEfficiency);

        int newTime = (int) Math.round(baseTime * timeMultiplier);
        int newEnergy = (int) Math.round(baseEnergy * energyMultiplier);

        if (newTime < 1) newTime = 1;      // min 1 tick
        if (newEnergy < 1) newEnergy = 1;  // min 1 FE/t

        return new ConsumerResult(newTime, newEnergy);
    }

    // ---------------------------------------------------------------------
    // PRODUCER: apply to burn time + total FE generated per fuel
    // ---------------------------------------------------------------------

    /**
     * Apply speed/efficiency to a producer (generator).
     *
     * @param baseBurnTime   base fuel burn time in ticks
     * @param baseTotalFE    base total FE produced by that fuel
     * @return ProducerResult with upgraded burn time and total FE
     */
    public ProducerResult applyToProducer(int baseBurnTime, int baseTotalFE) {
        if (baseBurnTime <= 0 || baseTotalFE <= 0) {
            return new ProducerResult(baseBurnTime, baseTotalFE);
        }

        recalcCounts();
        if (totalSpeed == 0 && totalEfficiency == 0) {
            return new ProducerResult(baseBurnTime, baseTotalFE);
        }

        // Speed:      burn   *= 0.9  each, totalFE *= 0.95 each
        // Efficiency: burn   *= 1.05 each, totalFE *= 1.05 each
        double burnMultiplier =
                Math.pow(0.9, totalSpeed) *
                        Math.pow(1.05, totalEfficiency);

        double energyMultiplier =
                Math.pow(0.95, totalSpeed) *
                        Math.pow(1.05, totalEfficiency);

        int newBurn = (int) Math.round(baseBurnTime * burnMultiplier);
        int newTotalFE = (int) Math.round(baseTotalFE * energyMultiplier);

        if (newBurn < 1) newBurn = 1;    // min 1 tick
        if (newTotalFE < 0) newTotalFE = 0;

        return new ProducerResult(newBurn, newTotalFE);
    }

    // ---------------------------------------------------------------------
    // Result containers
    // ---------------------------------------------------------------------

    public record ConsumerResult(int timeTicks, int energyCostFE) {}
    public record ProducerResult(int burnTimeTicks, int totalFE) {}
}
