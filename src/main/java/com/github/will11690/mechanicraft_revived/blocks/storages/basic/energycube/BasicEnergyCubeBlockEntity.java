package com.github.will11690.mechanicraft_revived.blocks.storages.basic.energycube;

import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.BaseEnergyCubeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.EnergyCubeTier;
import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.gui.EnergyCubeContainer;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlockEntities;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicEnergyCubeBlockEntity extends BaseEnergyCubeBlockEntity {

    public BasicEnergyCubeBlockEntity(BlockPos pos, BlockState state) {
        super(MechaniCraftBlockEntities.BasicEnergyCubeBE.get(), pos, state, EnergyCubeTier.BASIC);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return MechaniCraftBlocks.BasicEnergyCube.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new EnergyCubeContainer(id, inv, this);
    }
}