package com.github.will11690.mechanicraft_revived.blocks.transport.base.gui;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.IFilterablePipe;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftContainers;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PipeFilterContainer extends AbstractContainerMenu {

    public static final int FILTER_SIZE = 27; // 3x9 "chest-like" grid

    public final BasePipeBlockEntity pipeBE;
    private final Level level;
    private final Direction side;
    private final IItemHandler filterHandler;

    public PipeFilterContainer(int windowId, Inventory inv, FriendlyByteBuf extraData) {
        this(windowId, inv,
                inv.player.level().getBlockEntity(extraData.readBlockPos()),
                Direction.from3DDataValue(extraData.readByte()));
    }

    public PipeFilterContainer(int windowId, Inventory inv, BlockEntity entity, Direction side) {
        super(MechaniCraftContainers.PipeFilterCont.get(), windowId);
        this.level = inv.player.level();
        this.side = side;

        if (!(entity instanceof BasePipeBlockEntity basePipe))
            throw new IllegalStateException("PipeFilterContainer entity is not a BasePipeBlockEntity!");
        this.pipeBE = basePipe;

        if (!(basePipe instanceof IFilterablePipe filterable))
            throw new IllegalStateException("Pipe at " + basePipe.getBlockPos() + " is not filterable!");

        this.filterHandler = filterable.getFilterForSide(side);

        // 3x9 filter slots (ghost slots)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int index = col + row * 9;
                int x = 8 + col * 18;
                int y = 18 + row * 18;

                this.addSlot(new SlotItemHandler(filterHandler, index, x, y) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return true; // ghost semantics handled in clicked()
                    }

                    @Override
                    public int getMaxStackSize() {
                        return 1; // show as single ghost
                    }
                });
            }
        }

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, pipeBE.getBlockPos()),
                player, pipeBE.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i)
            for (int l = 0; l < 9; ++l)
                this.addSlot(new Slot(playerInventory,
                        l + i * 9 + 9,
                        8 + l * 18,
                        84 + i * 18));
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory,
                    i,
                    8 + i * 18,
                    142));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // No shift-click moves; filter is purely ghost
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        // Only intercept clicks on the filter slots (0..FILTER_SIZE-1)
        if (slotId >= 0 && slotId < FILTER_SIZE) {
            Slot slot = this.slots.get(slotId);
            ItemStack carried = getCarried();

            if (!carried.isEmpty()) {
                ItemStack ghost = carried.copy();
                ghost.setCount(1);
                slot.set(ghost);        // writes to ItemStackHandler
            } else {
                slot.set(ItemStack.EMPTY);
            }
            // SlotItemHandler -> ItemStackHandler.setStackInSlot -> onContentsChanged -> pipeBE.setChanged()
            return;
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Make absolutely sure the pipe BE is marked dirty when we close
        pipeBE.setChanged();
    }

    public Direction getSide() {
        return side;
    }
}