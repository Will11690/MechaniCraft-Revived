package com.github.will11690.mechanicraft_revived.util.block.interfac;

import net.minecraft.core.BlockPos;

public interface IInfuserSync {

    BlockPos getBlockPos(); // usually from BlockEntity#getBlockPos()

    int getGuiProgress();
    int getGuiMaxProgress();
    int getGuiEnergyStored();
    int getGuiEnergyCapacity();

    void applyInfuserSync(int progress, int maxProgress, int energyStored, int energyCapacity);
}