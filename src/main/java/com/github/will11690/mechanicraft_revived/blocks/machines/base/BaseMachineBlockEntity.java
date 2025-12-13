package com.github.will11690.mechanicraft_revived.blocks.machines.base;

import com.github.will11690.mechanicraft_revived.capabilities.energy.MechaniCraftEnergyStorage;
import com.github.will11690.mechanicraft_revived.capabilities.upgrade.IUpgradeHandler;
import com.github.will11690.mechanicraft_revived.capabilities.upgrade.UpgradeHandlerMath;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftCapabilities;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base machine BE:
 * - Optional energy system (MechaniCraftEnergyStorage).
 * - Optional battery slot (1) that only accepts FE items.
 * - Optional unified upgrade slots (accept ANY upgrade item).
 *
 * Unified upgrades:
 * - Capacity/Transfer upgrades -> MechaniCraftEnergyStorage.updateEnergyStorageWithUpgrades(...)
 * - Speed/Efficiency upgrades -> UpgradeHandlerMath (same inventory)
 *
 * IMPORTANT:
 * - Machines are treated as FE SINKS ONLY from the outside:
 *   Pipes / other blocks can RECEIVE into them but CANNOT EXTRACT
 *   via the Forge energy capability.
 */
public abstract class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {

    /* --------------------------------------------------------------------- */
    /* Energy                                                                */
    /* --------------------------------------------------------------------- */

    protected final boolean hasEnergy;
    protected final MechaniCraftEnergyStorage energy;
    private final IEnergyStorage machineEnergyView;
    private final LazyOptional<IEnergyStorage> energyCap;

    protected final int baseEnergyCapacity;
    protected final int baseEnergyReceive;

    /* --------------------------------------------------------------------- */
    /* Battery slot                                                          */
    /* --------------------------------------------------------------------- */

    protected final boolean hasBatterySlot;
    protected final ItemStackHandler batterySlot;
    private final LazyOptional<IItemHandler> batteryCap;

    /* --------------------------------------------------------------------- */
    /* Unified upgrades                                                      */
    /* --------------------------------------------------------------------- */

    protected final int upgradeSlots;
    protected final ItemStackHandler upgradeInventory; // accepts any upgrade type
    protected final UpgradeHandlerMath upgradeMath;    // speed/eff math
    private final LazyOptional<IUpgradeHandler> upgradeCap;

    /* --------------------------------------------------------------------- */

    protected BaseMachineBlockEntity(BlockEntityType<?> type,
                                     BlockPos pos,
                                     BlockState state,
                                     boolean hasEnergy,
                                     int baseEnergyCapacity,
                                     int baseEnergyReceive,
                                     boolean hasBatterySlot,
                                     int upgradeSlots) {

        super(type, pos, state);

        this.hasEnergy = hasEnergy;
        this.baseEnergyCapacity = baseEnergyCapacity;
        this.baseEnergyReceive  = baseEnergyReceive;

        if (hasEnergy) {
            // Internal storage: full MechaniCraftEnergyStorage as-is.
            this.energy = new MechaniCraftEnergyStorage(baseEnergyCapacity, baseEnergyReceive, 0) {
                @Override
                protected void onEnergyChanged() {
                    // All internal energy changes mark the BE dirty.
                    BaseMachineBlockEntity.this.setChanged();
                }
            };
            this.energy.updateEnergyStorageNoUpgrades(baseEnergyCapacity, baseEnergyReceive, 0);

            // External view: machines are FE sinks only.
            this.machineEnergyView = new IEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return BaseMachineBlockEntity.this.energy.receiveEnergy(maxReceive, simulate);
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return 0;
                }

                @Override
                public int getEnergyStored() {
                    return BaseMachineBlockEntity.this.energy.getEnergyStored();
                }

                @Override
                public int getMaxEnergyStored() {
                    return BaseMachineBlockEntity.this.energy.getMaxEnergyStored();
                }

                @Override
                public boolean canExtract() {
                    return false;
                }

                @Override
                public boolean canReceive() {
                    return true;
                }
            };

            this.energyCap = LazyOptional.of(() -> this.machineEnergyView);
        } else {
            this.energy = null;
            this.machineEnergyView = null;
            this.energyCap = LazyOptional.empty();
        }

        this.hasBatterySlot = hasBatterySlot;
        if (hasBatterySlot) {
            this.batterySlot = new ItemStackHandler(1) {
                @Override
                protected void onContentsChanged(int slot) {
                    super.onContentsChanged(slot);
                    setChanged();
                }

                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                    return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
                }
            };
            this.batteryCap = LazyOptional.of(() -> batterySlot);
        } else {
            this.batterySlot = null;
            this.batteryCap = LazyOptional.empty();
        }

        this.upgradeSlots = Math.max(0, upgradeSlots);
        if (this.upgradeSlots > 0) {
            this.upgradeInventory = new ItemStackHandler(this.upgradeSlots) {
                @Override
                protected void onContentsChanged(int slot) {
                    super.onContentsChanged(slot);
                    onUpgradesChanged();
                }

                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                    // Accept ANY of your upgrades (capacity/transfer/speed/efficiency).
                    return stack.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())
                            || stack.getItem().equals(MechaniCraftItems.TransferUpgrade.get())
                            || stack.getItem().equals(MechaniCraftItems.SpeedUpgrade.get())
                            || stack.getItem().equals(MechaniCraftItems.EfficiencyUpgrade.get());
                }
            };

            this.upgradeMath = new UpgradeHandlerMath(this.upgradeInventory);

            // Adapter to the unified upgrade capability.
            this.upgradeCap = LazyOptional.of(() -> new IUpgradeHandler() {
                @Override
                public ItemStackHandler getUpgradeInventory() {
                    return upgradeInventory;
                }

                @Override
                public UpgradeHandlerMath.ConsumerResult applyToConsumer(int baseTimeTicks,
                                                                         int baseEnergyCostFE) {
                    return upgradeMath.applyToConsumer(baseTimeTicks, baseEnergyCostFE);
                }

                @Override
                public UpgradeHandlerMath.ProducerResult applyToProducer(int baseBurnTimeTicks,
                                                                         int baseTotalFE) {
                    return upgradeMath.applyToProducer(baseBurnTimeTicks, baseTotalFE);
                }

                @Override
                public int getTotalSpeed() {
                    return upgradeMath.getTotalSpeed();
                }

                @Override
                public int getTotalEfficiency() {
                    return upgradeMath.getTotalEfficiency();
                }

                @Override
                public CompoundTag serializeNBT() {
                    CompoundTag t = new CompoundTag();
                    t.put("Upgrades", upgradeInventory.serializeNBT());
                    return t;
                }

                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    if (nbt.contains("Upgrades", Tag.TAG_COMPOUND)) {
                        upgradeInventory.deserializeNBT(nbt.getCompound("Upgrades"));
                    }
                }
            });
        } else {
            this.upgradeInventory = null;
            this.upgradeMath = null;
            this.upgradeCap = LazyOptional.empty();
        }
    }

    /* --------------------------------------------------------------------- */
    /* Tick                                                                  */
    /* --------------------------------------------------------------------- */

    /** Called by BaseMachine ticker. */
    public void tickServer() {
        if (level == null || level.isClientSide) return;

        if (hasEnergy && hasBatterySlot) {
            pullEnergyFromBattery();
        }
    }

    /**
     * Pull FE from battery item into internal storage.
     */
    protected void pullEnergyFromBattery() {
        if (level == null || level.isClientSide) return;
        if (!hasEnergy || !hasBatterySlot) return;

        ItemStack stack = batterySlot.getStackInSlot(0);
        if (stack.isEmpty()) return;

        IEnergyStorage itemEnergy =
                stack.getCapability(ForgeCapabilities.ENERGY, null).orElse(null);
        if (itemEnergy == null || !itemEnergy.canExtract()) return;

        int room = energy.getMaxReceive();
        if (room <= 0) return;

        int canPull = itemEnergy.extractEnergy(room, true);
        if (canPull <= 0) return;

        int accepted = energy.receiveEnergy(canPull, false);
        if (accepted > 0) {
            itemEnergy.extractEnergy(accepted, false);
            setChanged();
        }
    }

    /* --------------------------------------------------------------------- */
    /* Unified upgrade change hook                                           */
    /* --------------------------------------------------------------------- */

    protected void onUpgradesChanged() {
        if (level == null || level.isClientSide) return;

        if (hasEnergy && upgradeInventory != null) {
            int totalCap = 0;
            int totalXfer = 0;

            for (int i = 0; i < upgradeInventory.getSlots(); i++) {
                ItemStack u = upgradeInventory.getStackInSlot(i);
                if (u.isEmpty()) continue;

                if (u.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())) {
                    totalCap += u.getCount();
                } else if (u.getItem().equals(MechaniCraftItems.TransferUpgrade.get())) {
                    totalXfer += u.getCount();
                }
            }

            energy.setUpgrade1Stack(totalCap > 0
                    ? new ItemStack(MechaniCraftItems.CapacityUpgrade.get(), totalCap)
                    : ItemStack.EMPTY);

            energy.setUpgrade2Stack(totalXfer > 0
                    ? new ItemStack(MechaniCraftItems.TransferUpgrade.get(), totalXfer)
                    : ItemStack.EMPTY);

            energy.setUpgrade3Stack(ItemStack.EMPTY);
            energy.setUpgrade4Stack(ItemStack.EMPTY);

            // Machines don't need external extract, so pass 0 for extract base.
            energy.updateEnergyStorageWithUpgrades(
                    baseEnergyCapacity,
                    baseEnergyReceive,
                    0
            );
        }

        onSpeedEfficiencyUpgradesChanged();

        setChanged();
    }

    protected void onSpeedEfficiencyUpgradesChanged() {}

    protected UpgradeHandlerMath.ConsumerResult getConsumerUpgrades(int baseTime, int baseEnergyCost) {
        if (upgradeMath == null) return new UpgradeHandlerMath.ConsumerResult(baseTime, baseEnergyCost);
        return upgradeMath.applyToConsumer(baseTime, baseEnergyCost);
    }

    protected UpgradeHandlerMath.ProducerResult getProducerUpgrades(int baseBurn, int baseFE) {
        if (upgradeMath == null) return new UpgradeHandlerMath.ProducerResult(baseBurn, baseFE);
        return upgradeMath.applyToProducer(baseBurn, baseFE);
    }

    /* --------------------------------------------------------------------- */
    /* Inventory dropping                                                    */
    /* --------------------------------------------------------------------- */

    protected IItemHandler[] getAdditionalDropHandlers() {
        return new IItemHandler[0];
    }

    public void dropInventory(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return;

        List<IItemHandler> handlers = new ArrayList<>();

        IItemHandler[] extra = getAdditionalDropHandlers();
        if (extra != null && extra.length > 0) {
            handlers.addAll(Arrays.asList(extra));
        }

        if (hasBatterySlot && batterySlot != null) {
            handlers.add(batterySlot);
        }

        if (upgradeSlots > 0 && upgradeInventory != null) {
            handlers.add(upgradeInventory);
        }

        for (IItemHandler h : handlers) {
            for (int i = 0; i < h.getSlots(); i++) {
                ItemStack stack = h.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                ItemEntity ent = new ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        stack.copy()
                );
                ent.setPickUpDelay(20);
                ent.setDeltaMovement(ent.getDeltaMovement().multiply(0, 1, 0));
                level.addFreshEntity(ent);

                if (h instanceof ItemStackHandler ish) {
                    ish.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
    }

    /* --------------------------------------------------------------------- */
    /* Capabilities                                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {

        // Machines: FE sink only view to the outside.
        if (hasEnergy && cap == ForgeCapabilities.ENERGY) {
            return energyCap.cast();
        }

        if (upgradeSlots > 0 && cap == MechaniCraftCapabilities.UpgradeHandler) {
            return upgradeCap.cast();
        }

        // Item handlers (battery + IO) are exposed by subclasses with proper
        // slot-level rules (output/battery extract only, etc).
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
        batteryCap.invalidate();
        upgradeCap.invalidate();
    }

    /* --------------------------------------------------------------------- */
    /* Save / Load                                                           */
    /* --------------------------------------------------------------------- */

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (hasEnergy) {
            tag.put("Energy", (CompoundTag) energy.serializeNBT());
        }

        if (hasBatterySlot) {
            tag.put("BatterySlot", batterySlot.serializeNBT());
        }

        if (upgradeSlots > 0) {
            tag.put("Upgrades", upgradeInventory.serializeNBT());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (hasEnergy && tag.contains("Energy", Tag.TAG_COMPOUND)) {
            energy.deserializeNBT(tag.getCompound("Energy"));
        }

        if (hasBatterySlot && tag.contains("BatterySlot", Tag.TAG_COMPOUND)) {
            batterySlot.deserializeNBT(tag.getCompound("BatterySlot"));
        }

        if (upgradeSlots > 0 && tag.contains("Upgrades", Tag.TAG_COMPOUND)) {
            upgradeInventory.deserializeNBT(tag.getCompound("Upgrades"));
        }

        if (upgradeSlots > 0) {
            onUpgradesChanged();
        }
    }

    /* --------------------------------------------------------------------- */
    /* Getters for subclasses                                                */
    /* --------------------------------------------------------------------- */

    @Nullable
    public MechaniCraftEnergyStorage getEnergyStorage() {
        return energy;
    }

    @Nullable
    public ItemStackHandler getBatterySlot() {
        return batterySlot;
    }

    @Nullable
    public ItemStackHandler getUpgradeInventory() {
        return upgradeInventory;
    }
}