package com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.gui;

import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.BaseEnergyCubeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.EnergyCubeTier;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class EnergyCubeContainer extends AbstractContainerMenu {

    public final BaseEnergyCubeBlockEntity cubeBE;
    private final Level level;
    private final BlockPos pos;

    private final int cubeSlotCount;

    public EnergyCubeContainer(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EnergyCubeContainer(int id, Inventory inv, BlockEntity entity) {
        super(MechaniCraftContainers.EnergyCubeCont.get(), id);

        this.cubeBE = (BaseEnergyCubeBlockEntity) entity;
        this.level = inv.player.level();
        this.pos = cubeBE.getBlockPos();

        EnergyCubeTier tier = cubeBE.getTier();

        // + (charge) slots
        for (int i = 0; i < tier.chargePositions.size(); i++) {
            int[] p = tier.chargePositions.get(i);
            addSlot(new SlotItemHandler(cubeBE.getItemHandler(), cubeBE.getChargeStart() + i, p[0], p[1]));
        }

        // - (discharge) slots
        for (int i = 0; i < tier.dischargePositions.size(); i++) {
            int[] p = tier.dischargePositions.get(i);
            addSlot(new SlotItemHandler(cubeBE.getItemHandler(), cubeBE.getDischargeStart() + i, p[0], p[1]));
        }

        // upgrade slots (if any)
        for (int i = 0; i < tier.upgradePositions.size(); i++) {
            int[] p = tier.upgradePositions.get(i);
            if (i >= cubeBE.getUpgradeSlotCount()) break;
            addSlot(new SlotItemHandler(cubeBE.getItemHandler(), cubeBE.getUpgradeStart() + i, p[0], p[1]));
        }

        this.cubeSlotCount = cubeBE.getItemHandler().getSlots();

        // Player inventory
        int invX = 8;
        int invY = 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, invX + col * 18, invY + row * 18));
            }
        }

        // Hotbar
        int hotbarY = invY + 58;
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, invX + col * 18, hotbarY));
        }
    }

    public EnergyCubeTier getTier() {
        return cubeBE.getTier();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (level.getBlockEntity(pos) != cubeBE) return false;
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < cubeSlotCount) {
            // cube -> player
            if (!moveItemStackTo(stack, cubeSlotCount, slots.size(), true)) return empty;
        } else {
            // player -> cube (let handler validation decide)
            if (!moveItemStackTo(stack, 0, cubeSlotCount, false)) return empty;
        }

        if (stack.isEmpty()) slot.set(empty);
        else slot.setChanged();

        return copy;
    }

    public int getEnergyStored() {
        return cubeBE.getEnergy().getEnergyStored();
    }

    public int getEnergyCapacity() {
        return cubeBE.getEnergy().getCapacity();
    }
}