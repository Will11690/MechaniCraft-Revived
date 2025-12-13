package com.github.will11690.mechanicraft_revived.blocks.machines.enhanced.infuser;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class EnhancedInfuserContainer extends AbstractContainerMenu {

    public final EnhancedInfuserBlockEntity infuserBE;
    private final Level level;

    public EnhancedInfuserContainer(int containerID, Inventory inv, FriendlyByteBuf extraData) {

        this(containerID, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EnhancedInfuserContainer(int pContainerId, Inventory inv, BlockEntity entity) {

        super(MechaniCraftContainers.EnhancedInfuserCont.get(), pContainerId);
        checkContainerSize(inv, 4);
        infuserBE = (EnhancedInfuserBlockEntity) entity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        infuserBE.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {

            this.addSlot(new SlotItemHandler(itemHandler, 0, 38, 32));
            this.addSlot(new SlotItemHandler(itemHandler, 1, 66, 32));
            this.addSlot(new SlotItemHandler(itemHandler, 2, 132, 32) {

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {

                    return false;
                }

                @Override
                public int getMaxStackSize() {

                    return 0;
                }

                @Override
                public int getMaxStackSize(@NotNull ItemStack stack) {

                    return 0;
                }
            });

            this.addSlot(new SlotItemHandler(itemHandler, 3, 8, 55));
        });
    }

    @Override
    public boolean stillValid(Player player) {

        return stillValid(ContainerLevelAccess.create(level, infuserBE.getBlockPos()), player, MechaniCraftBlocks.EnhancedInfuser.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {

        for (int i = 0; i < 3; ++i) {

            for (int l = 0; l < 9; ++l) {

                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {

        for (int i = 0; i < 9; ++i) {

            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    /* ---------- Progress / Energy accessors (from BE, synced via packet) ---------- */

    public boolean isCrafting() {

        return getProgress() > 0;
    }

    public int getProgress() {

        return infuserBE.progress;
    }

    public int getMaxProgress() {

        return infuserBE.maxProgress;
    }

    /** Current FE stored. */
    public int getEnergyStored() {

        return infuserBE.getEnergyStorage() != null ? infuserBE.getEnergyStorage().getEnergyStored() : 0;
    }

    /** FE capacity. */
    public int getEnergyCapacity() {

        return infuserBE.getEnergyStorage() != null ? infuserBE.getEnergyStorage().getCapacity() : 0;
    }

    public boolean hasEnergy() {

        return getEnergyCapacity() > 0;
    }

    public int getProgressScaled(int width) {

        int cookProgress = getProgress();
        int cookTimeForRecipe = getMaxProgress();
        return cookTimeForRecipe != 0 && cookProgress != 0 ? cookProgress * width / cookTimeForRecipe : 0;
    }

    /** Energy bar scale. */
    public int getEnergyScaled(int height) {

        int stored = getEnergyStored();
        int cap = getEnergyCapacity();
        return cap != 0 && stored != 0 ? stored * height / cap : 0;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {

        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }

        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

        } else {
            return ItemStack.EMPTY;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
}