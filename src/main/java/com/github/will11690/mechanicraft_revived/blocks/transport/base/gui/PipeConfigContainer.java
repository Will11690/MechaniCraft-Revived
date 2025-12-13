package com.github.will11690.mechanicraft_revived.blocks.transport.base.gui;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PipeConfigContainer extends AbstractContainerMenu {

    public final BasePipeBlockEntity pipeBE;
    private final Level level;

    public PipeConfigContainer(int windowId, Inventory inv, FriendlyByteBuf extraData) {

        this(windowId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public PipeConfigContainer(int windowId, Inventory inv, BlockEntity entity) {

        super(MechaniCraftContainers.PipeConfigCont.get(), windowId);
        this.pipeBE = (BasePipeBlockEntity) entity;
        this.level = inv.player.level();
    }

    @Override
    public boolean stillValid(Player player) {

        return stillValid(ContainerLevelAccess.create(level, pipeBE.getBlockPos()), player, pipeBE.getBlockState().getBlock());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {

        // No shift-click logic, no slots.
        return ItemStack.EMPTY;
    }
}
