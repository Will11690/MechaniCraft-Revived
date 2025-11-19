package com.github.will11690.mechanicraft_revived.capabilities.upgrade;

import com.github.will11690.mechanicraft_revived.registry.MechaniCraftCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Capability provider for IUpgradeHandler.
 *
 * Attach this to block entities that support Speed/Efficiency upgrades,
 * or embed the UpgradeHandler directly in the BE and proxy to it.
 */
public class UpgradeHandlerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final UpgradeHandler handler;
    private final LazyOptional<IUpgradeHandler> optional;

    /**
     * @param slots number of upgrade slots this machine/generator supports.
     */
    public UpgradeHandlerProvider(int slots) {
        this.handler = new UpgradeHandler(slots);
        this.optional = LazyOptional.of(() -> handler);
    }

    public IUpgradeHandler getHandler() {
        return handler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {
        if (cap == MechaniCraftCapabilities.UpgradeHandler) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        handler.deserializeNBT(nbt);
    }
}