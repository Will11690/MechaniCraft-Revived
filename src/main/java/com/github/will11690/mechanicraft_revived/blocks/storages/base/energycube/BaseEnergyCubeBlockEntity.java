package com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube;

import com.github.will11690.mechanicraft_revived.capabilities.energy.MechaniCraftEnergyStorage;
import com.github.will11690.mechanicraft_revived.network.MechaniCraftNetwork;
import com.github.will11690.mechanicraft_revived.network.packet.client.EnergyCubeSyncPacket;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import com.github.will11690.mechanicraft_revived.util.block.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public abstract class BaseEnergyCubeBlockEntity extends BlockEntity implements MenuProvider {

    protected final EnergyCubeTier tier;

    /* --------------------------------------------------------------------- */
    /* Energy                                                                */
    /* --------------------------------------------------------------------- */

    protected final MechaniCraftEnergyStorage energy;
    private final LazyOptional<IEnergyStorage>[] sideCaps;
    private final LazyOptional<IEnergyStorage> nullSideCap;

    /* --------------------------------------------------------------------- */
    /* Items                                                                 */
    /* --------------------------------------------------------------------- */

    protected final ItemStackHandler itemHandler;
    private final LazyOptional<IItemHandler> itemCap;

    // slot ranges (indices in itemHandler)
    private final int chargeStart = 0;
    private final int chargeCount;
    private final int dischargeStart;
    private final int dischargeCount;
    private final int upgradeStart;
    private final int upgradeCount;

    /* --------------------------------------------------------------------- */
    /* Per-side config                                                       */
    /* --------------------------------------------------------------------- */

    protected final IOMode[] ioModes = new IOMode[6];
    protected final RedstoneMode[] rsModes = new RedstoneMode[6]; // stored per-side, but SETTERS keep it global
    protected final int[] transferLimits = new int[6];

    /* --------------------------------------------------------------------- */
    /* Sync                                                                  */
    /* --------------------------------------------------------------------- */

    private int lastSyncedEnergy = -1;
    private int lastSyncedCap = -1;
    private int lastSyncedHash = 0;
    private int syncCooldown = 0;

    @SuppressWarnings("unchecked")
    protected BaseEnergyCubeBlockEntity(BlockEntityType<?> type,
                                        BlockPos pos,
                                        BlockState state,
                                        EnergyCubeTier tier) {
        super(type, pos, state);

        this.tier = tier;

        this.chargeCount = tier.chargeSlots;
        this.dischargeCount = tier.dischargeSlots;
        this.upgradeCount = Math.min(4, Math.max(0, tier.upgradeSlots)); // your energy storage supports 4 stacks

        this.dischargeStart = chargeStart + chargeCount;
        this.upgradeStart = dischargeStart + dischargeCount;

        int totalSlots = chargeCount + dischargeCount + upgradeCount;

        this.itemHandler = new ItemStackHandler(totalSlots) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (stack.isEmpty()) return false;

                if (isUpgradeSlot(slot)) {
                    return stack.getItem().equals(MechaniCraftItems.CapacityUpgrade.get())
                            || stack.getItem().equals(MechaniCraftItems.TransferUpgrade.get());
                }

                // charge/discharge: anything with FE capability
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BaseEnergyCubeBlockEntity.this.setChanged();

                if (isUpgradeSlot(slot)) {
                    updateEnergyFromUpgrades();
                }
            }
        };
        this.itemCap = LazyOptional.of(() -> itemHandler);

        this.energy = new MechaniCraftEnergyStorage(tier.baseCapacity, tier.baseTransfer, tier.baseTransfer) {
            @Override
            protected void onEnergyChanged() {
                BaseEnergyCubeBlockEntity.this.setChanged();
            }
        };
        this.energy.updateEnergyStorageNoUpgrades(tier.baseCapacity, tier.baseTransfer, tier.baseTransfer);

        // Defaults: all sides BOTH, RS IGNORED, limit = base transfer
        for (Direction d : Direction.values()) {
            int i = d.ordinal();
            ioModes[i] = IOMode.BOTH;
            rsModes[i] = RedstoneMode.IGNORED;
            transferLimits[i] = tier.baseTransfer;
        }

        this.sideCaps = (LazyOptional<IEnergyStorage>[]) new LazyOptional[6];
        for (Direction d : Direction.values()) {
            this.sideCaps[d.ordinal()] = LazyOptional.of(() -> new SideEnergyView(d));
        }
        this.nullSideCap = LazyOptional.of(() -> new SideEnergyView(null));
    }

    public EnergyCubeTier getTier() {
        return tier;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public int getChargeSlotCount() {
        return chargeCount;
    }

    public int getDischargeSlotCount() {
        return dischargeCount;
    }

    public int getUpgradeSlotCount() {
        return upgradeCount;
    }

    public int getChargeStart() { return chargeStart; }
    public int getDischargeStart() { return dischargeStart; }
    public int getUpgradeStart() { return upgradeStart; }

    private boolean isChargeSlot(int slot) {
        return slot >= chargeStart && slot < chargeStart + chargeCount;
    }

    private boolean isDischargeSlot(int slot) {
        return slot >= dischargeStart && slot < dischargeStart + dischargeCount;
    }

    private boolean isUpgradeSlot(int slot) {
        return slot >= upgradeStart && slot < upgradeStart + upgradeCount;
    }

    /* --------------------------------------------------------------------- */
    /* Ticking                                                               */
    /* --------------------------------------------------------------------- */

    public void tickServer() {
        if (level == null || level.isClientSide) return;

        // Charge/discharge items
        doItemIO();

        // Push energy out to neighbors (OUTPUT/BOTH)
        pushEnergyToNeighbors();

        syncToClients();
    }

    private void doItemIO() {
        int perSlotRate = energy.getMaxExtract(); // respects upgrades if enabled later
        if (perSlotRate <= 0) return;

        // DISCHARGE slots: item -> cube
        if (energy.getEnergyStored() < energy.getCapacity()) {
            for (int i = 0; i < dischargeCount; i++) {
                int slot = dischargeStart + i;
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (stack.isEmpty()) continue;

                stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemEnergy -> {
                    if (!itemEnergy.canExtract()) return;

                    int want = Math.min(perSlotRate, energy.getCapacity() - energy.getEnergyStored());
                    if (want <= 0) return;

                    int canPull = itemEnergy.extractEnergy(want, true);
                    if (canPull <= 0) return;

                    int accepted = energy.receiveEnergy(canPull, false);
                    if (accepted > 0) {
                        itemEnergy.extractEnergy(accepted, false);
                        setChanged();
                    }
                });
            }
        }

        // CHARGE slots: cube -> item
        if (energy.getEnergyStored() > 0) {
            for (int i = 0; i < chargeCount; i++) {
                int slot = chargeStart + i;
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (stack.isEmpty()) continue;

                stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemEnergy -> {
                    if (!itemEnergy.canReceive()) return;

                    int canGive = energy.extractEnergy(perSlotRate, true);
                    if (canGive <= 0) return;

                    int accepted = itemEnergy.receiveEnergy(canGive, false);
                    if (accepted > 0) {
                        energy.extractEnergy(accepted, false);
                        setChanged();
                    }
                });
            }
        }
    }

    protected void pushEnergyToNeighbors() {
        if (level == null) return;
        if (energy.getEnergyStored() <= 0) return;

        for (Direction outDir : Direction.values()) {
            if (!canExtractTo(outDir)) continue;

            BlockPos otherPos = worldPosition.relative(outDir);
            BlockEntity otherBE = level.getBlockEntity(otherPos);
            if (otherBE == null) continue;

            LazyOptional<IEnergyStorage> otherCap =
                    otherBE.getCapability(ForgeCapabilities.ENERGY, outDir.getOpposite());
            if (!otherCap.isPresent()) continue;

            IEnergyStorage target = otherCap.orElse(null);
            if (target == null || !target.canReceive()) continue;

            int limit = getSideLimit(outDir);
            if (limit <= 0) continue;

            int canExtractSim = energy.extractEnergy(limit, true);
            if (canExtractSim <= 0) continue;

            int accepted = target.receiveEnergy(canExtractSim, false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                setChanged();
            }
        }
    }

    /* --------------------------------------------------------------------- */
    /* Redstone gating                                                       */
    /* --------------------------------------------------------------------- */

    protected boolean isSideAllowedByRedstone(@Nullable Direction side) {
        if (level == null) return true;
        if (side == null) return true;

        RedstoneMode mode = rsModes[side.ordinal()];
        if (mode == null) mode = RedstoneMode.IGNORED;

        if (mode == RedstoneMode.IGNORED) return true;
        if (mode == RedstoneMode.OFF) return false;

        boolean powered = level.hasNeighborSignal(worldPosition);

        if (mode == RedstoneMode.HIGH) return powered;
        return !powered; // LOW
    }

    /* --------------------------------------------------------------------- */
    /* Capability rules                                                      */
    /* --------------------------------------------------------------------- */

    protected boolean canReceiveFrom(@Nullable Direction side) {
        if (!isSideAllowedByRedstone(side)) return false;
        if (side == null) return true;

        IOMode m = ioModes[side.ordinal()];
        if (m == null) m = IOMode.BOTH;

        return m == IOMode.INSERT || m == IOMode.BOTH;
    }

    protected boolean canExtractTo(@Nullable Direction side) {
        if (!isSideAllowedByRedstone(side)) return false;
        if (side == null) return true;

        IOMode m = ioModes[side.ordinal()];
        if (m == null) m = IOMode.BOTH;

        return m == IOMode.EXTRACT || m == IOMode.BOTH;
    }

    protected int getSideLimit(@Nullable Direction side) {
        int max = energy.getMaxExtract(); // upgraded transfer if applicable
        if (side == null) return max;

        int v = transferLimits[side.ordinal()];
        if (v <= 0) return 0;
        return Math.min(v, max);
    }

    private final class SideEnergyView implements IEnergyStorage {
        private final @Nullable Direction side;

        private SideEnergyView(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceiveFrom(side)) return 0;
            int limit = getSideLimit(side);
            if (limit <= 0) return 0;
            int clamped = Math.min(maxReceive, limit);
            return energy.receiveEnergy(clamped, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!canExtractTo(side)) return 0;
            int limit = getSideLimit(side);
            if (limit <= 0) return 0;
            int clamped = Math.min(maxExtract, limit);
            return energy.extractEnergy(clamped, simulate);
        }

        @Override public int getEnergyStored() { return energy.getEnergyStored(); }
        @Override public int getMaxEnergyStored() { return energy.getMaxEnergyStored(); }
        @Override public boolean canExtract() { return canExtractTo(side); }
        @Override public boolean canReceive() { return canReceiveFrom(side); }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (side == null) return nullSideCap.cast();
            return sideCaps[side.ordinal()].cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<IEnergyStorage> o : sideCaps) if (o != null) o.invalidate();
        nullSideCap.invalidate();
        itemCap.invalidate();
    }

    /* --------------------------------------------------------------------- */
    /* Upgrades -> apply to energy storage                                   */
    /* --------------------------------------------------------------------- */

    private void updateEnergyFromUpgrades() {
        // Map upgrade slots (0..3) into your energy storage’s 4 stacks
        // Missing slots are EMPTY
        ItemStack u1 = (upgradeCount >= 1) ? itemHandler.getStackInSlot(upgradeStart) : ItemStack.EMPTY;
        ItemStack u2 = (upgradeCount >= 2) ? itemHandler.getStackInSlot(upgradeStart + 1) : ItemStack.EMPTY;
        ItemStack u3 = (upgradeCount >= 3) ? itemHandler.getStackInSlot(upgradeStart + 2) : ItemStack.EMPTY;
        ItemStack u4 = (upgradeCount >= 4) ? itemHandler.getStackInSlot(upgradeStart + 3) : ItemStack.EMPTY;

        energy.setUpgrade1Stack(u1);
        energy.setUpgrade2Stack(u2);
        energy.setUpgrade3Stack(u3);
        energy.setUpgrade4Stack(u4);

        // Recompute upgraded capacity/transfer
        energy.updateEnergyStorageWithUpgrades(tier.baseCapacity, tier.baseTransfer, tier.baseTransfer);

        // Ensure per-side limits don’t exceed new max
        int max = energy.getMaxExtract();
        for (Direction d : Direction.values()) {
            int i = d.ordinal();
            if (transferLimits[i] > max) transferLimits[i] = max;
        }

        forceSync();
    }

    /* --------------------------------------------------------------------- */
    /* MenuProvider                                                          */
    /* --------------------------------------------------------------------- */

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Energy Cube");
    }

    @Override
    public abstract @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull net.minecraft.world.entity.player.Player player);

    /* --------------------------------------------------------------------- */
    /* Save / Load                                                           */
    /* --------------------------------------------------------------------- */

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("Energy", (CompoundTag) energy.serializeNBT());
        tag.put("Items", itemHandler.serializeNBT());

        tag.putIntArray("IOModes", packIOModes());
        tag.putIntArray("RSModes", packRSModes());
        tag.putIntArray("Limits", packLimits());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Energy", Tag.TAG_COMPOUND)) {
            energy.deserializeNBT(tag.getCompound("Energy"));
        }

        if (tag.contains("Items", Tag.TAG_COMPOUND)) {
            itemHandler.deserializeNBT(tag.getCompound("Items"));
        }

        if (tag.contains("IOModes", Tag.TAG_INT_ARRAY)) unpackIOModes(tag.getIntArray("IOModes"));
        if (tag.contains("RSModes", Tag.TAG_INT_ARRAY)) unpackRSModes(tag.getIntArray("RSModes"));
        if (tag.contains("Limits", Tag.TAG_INT_ARRAY)) unpackLimits(tag.getIntArray("Limits"));

        // If this tier supports upgrades, make sure the loaded items apply to energy stats
        if (upgradeCount > 0) {
            updateEnergyFromUpgrades();
        }
    }

    private int[] packIOModes() {
        int[] out = new int[6];
        for (Direction d : Direction.values()) {
            IOMode m = ioModes[d.ordinal()];
            out[d.ordinal()] = (m == null) ? IOMode.BOTH.ordinal() : m.ordinal();
        }
        return out;
    }

    private int[] packRSModes() {
        int[] out = new int[6];
        for (Direction d : Direction.values()) {
            RedstoneMode m = rsModes[d.ordinal()];
            out[d.ordinal()] = (m == null) ? RedstoneMode.IGNORED.ordinal() : m.ordinal();
        }
        return out;
    }

    private int[] packLimits() {
        int[] out = new int[6];
        for (Direction d : Direction.values()) {
            out[d.ordinal()] = Math.max(0, transferLimits[d.ordinal()]);
        }
        return out;
    }

    private void unpackIOModes(int[] arr) {
        IOMode[] v = IOMode.values();
        for (Direction d : Direction.values()) {
            int i = d.ordinal();
            if (i < arr.length) {
                int ord = arr[i];
                ioModes[i] = (ord >= 0 && ord < v.length) ? v[ord] : IOMode.BOTH;
            }
        }
    }

    private void unpackRSModes(int[] arr) {
        RedstoneMode[] v = RedstoneMode.values();
        for (Direction d : Direction.values()) {
            int i = d.ordinal();
            if (i < arr.length) {
                int ord = arr[i];
                rsModes[i] = (ord >= 0 && ord < v.length) ? v[ord] : RedstoneMode.IGNORED;
            }
        }
        normalizeRedstoneGlobal();
    }

    private void unpackLimits(int[] arr) {
        for (Direction d : Direction.values()) {
            int i = d.ordinal();
            if (i < arr.length) transferLimits[i] = clampLimit(arr[i]);
        }
    }

    /* --------------------------------------------------------------------- */
    /* Config mutation                                                       */
    /* --------------------------------------------------------------------- */

    public void setSideIOMode(Direction side, IOMode mode) {
        ioModes[side.ordinal()] = (mode == null) ? IOMode.BOTH : mode;
        setChanged();
        forceSync();
    }

    public void setSideRedstoneMode(Direction side, RedstoneMode mode) {
        RedstoneMode m = (mode == null) ? RedstoneMode.IGNORED : mode;
        for (Direction d : Direction.values()) {
            rsModes[d.ordinal()] = m;
        }
        setChanged();
        forceSync();
    }

    public void setSideTransferLimit(Direction side, int limit) {
        transferLimits[side.ordinal()] = clampLimit(limit);
        setChanged();
        forceSync();
    }

    public IOMode getSideIOMode(Direction side) {
        IOMode m = ioModes[side.ordinal()];
        return (m == null) ? IOMode.BOTH : m;
    }

    public RedstoneMode getSideRedstoneMode(Direction side) {
        RedstoneMode m = rsModes[side.ordinal()];
        return (m == null) ? RedstoneMode.IGNORED : m;
    }

    public int getSideTransferLimit(Direction side) {
        return transferLimits[side.ordinal()];
    }

    protected int clampLimit(int in) {
        if (in < 0) return 0;
        return Math.min(in, energy.getMaxExtract());
    }

    private void normalizeRedstoneGlobal() {
        RedstoneMode m = getSideRedstoneMode(Direction.NORTH);
        for (Direction d : Direction.values()) rsModes[d.ordinal()] = m;
    }

    /* --------------------------------------------------------------------- */
    /* Sync                                                                  */
    /* --------------------------------------------------------------------- */

    protected void forceSync() {
        this.lastSyncedEnergy = -1;
        this.lastSyncedCap = -1;
        this.lastSyncedHash = 0;
        this.syncCooldown = 0;
        syncToClients();
    }

    protected void syncToClients() {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (syncCooldown > 0) {
            syncCooldown--;
            return;
        }
        syncCooldown = 5;

        int stored = energy.getEnergyStored();
        int cap = energy.getCapacity();
        int hash = computeConfigHash();

        if (stored == lastSyncedEnergy && cap == lastSyncedCap && hash == lastSyncedHash) return;

        lastSyncedEnergy = stored;
        lastSyncedCap = cap;
        lastSyncedHash = hash;

        MechaniCraftNetwork.sendToTracking(
                serverLevel,
                worldPosition,
                new EnergyCubeSyncPacket(worldPosition, stored, cap, packIOModes(), packRSModes(), packLimits())
        );
    }

    private int computeConfigHash() {
        int h = 1;
        for (Direction d : Direction.values()) {
            h = 31 * h + getSideIOMode(d).ordinal();
            h = 31 * h + getSideRedstoneMode(d).ordinal();
            h = 31 * h + getSideTransferLimit(d);
        }
        return h;
    }

    public void applyClientSync(int energyStored, int energyCapacity, int[] io, int[] rs, int[] limits) {
        energy.setCapacity(energyCapacity);
        energy.setEnergy(energyStored);

        unpackIOModes(io);
        unpackRSModes(rs);
        unpackLimits(limits);

        setChanged();
    }

    public static String dirLabel(Direction d) {
        return d.getName().toUpperCase(Locale.ROOT);
    }

    public MechaniCraftEnergyStorage getEnergy() {
        return energy;
    }
}