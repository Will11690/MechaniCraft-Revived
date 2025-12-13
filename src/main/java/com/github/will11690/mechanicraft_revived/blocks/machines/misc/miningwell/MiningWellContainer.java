package com.github.will11690.mechanicraft_revived.blocks.machines.misc.miningwell;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

// Minimal "no-slot" container, may implement GUI later so placeholder for now
public class MiningWellContainer extends AbstractContainerMenu {

    public MiningWellContainer(int windowId, Inventory playerInv, MiningWellBE be) {
        super(MechaniCraftContainers.MiningWellContainer.get(), windowId);
    }

    public MiningWellContainer(int windowId, Inventory playerInv, net.minecraft.network.FriendlyByteBuf buf) {

        this(windowId, playerInv, (MiningWellBE) playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {

        return true;
    }
}