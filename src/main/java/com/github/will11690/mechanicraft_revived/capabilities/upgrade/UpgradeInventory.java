package com.github.will11690.mechanicraft_revived.capabilities.upgrade;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Upgrade inventory for Speed/Efficiency upgrades.
 * Slot count is configurable per machine (1, 2, 3, ...).
 */
public class UpgradeInventory extends ItemStackHandler {

    public UpgradeInventory(int slots) {
        super(slots);
    }
}