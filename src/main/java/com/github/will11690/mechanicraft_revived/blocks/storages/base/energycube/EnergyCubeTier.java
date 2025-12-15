package com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public enum EnergyCubeTier {

    BASIC(
            38_400, 128,
            1, 1, 0,
            tex("textures/gui/energy_cube/basic_energy_cube.png"),
            // charge slots
            List.of(pos(49, 33)),
            // discharge slots
            List.of(pos(105, 33)),
            // upgrade slots
            List.of()
    ),

    ENHANCED(
            0, 0,
            2, 2, 0,
            tex("textures/gui/energy_cube/enhanced_energy_cube.png"),
            List.of(pos(32, 33), pos(51, 33)),
            List.of(pos(105, 33), pos(124, 33)),
            List.of()
    ),

    ADVANCED(
            0, 0,
            3, 3, 1, // per your design note (start upgrades here). adjust later if needed.
            tex("textures/gui/energy_cube/advanced_energy_cube.png"),
            List.of(pos(51, 14), pos(51, 33), pos(51, 52)),
            List.of(pos(105, 14), pos(105, 33), pos(105, 52)),
            List.of(pos(151, 34))
    ),

    ELITE(
            0, 0,
            4, 4, 2,
            tex("textures/gui/energy_cube/elite_energy_cube.png"),
            List.of(pos(51, 14), pos(51, 33), pos(51, 52), pos(32, 33)),
            List.of(pos(105, 14), pos(105, 33), pos(105, 52), pos(124, 33)),
            List.of(pos(151, 24), pos(151, 44))
    ),

    SUPERIOR(
            0, 0,
            5, 5, 3,
            tex("textures/gui/energy_cube/superior_energy_cube.png"),
            List.of(pos(51, 14), pos(51, 33), pos(51, 52), pos(32, 33), pos(32, 52)),
            List.of(pos(105, 14), pos(105, 33), pos(105, 52), pos(124, 33), pos(124, 52)),
            List.of(pos(151, 14), pos(151, 34), pos(151, 54))
    ),

    ULTIMATE(
            0, 0,
            6, 6, 4,
            tex("textures/gui/energy_cube/ultimate_energy_cube.png"),
            List.of(pos(32, 14), pos(51, 14), pos(32, 33), pos(51, 33), pos(32, 52), pos(51, 52)),
            List.of(pos(105, 14), pos(124, 14), pos(105, 33), pos(124, 33), pos(105, 52), pos(124, 52)),
            List.of(pos(151, 4), pos(151, 24), pos(151, 44), pos(151, 64))
    );

    public final int baseCapacity;
    public final int baseTransfer;

    public final int chargeSlots;
    public final int dischargeSlots;
    public final int upgradeSlots;

    public final ResourceLocation guiTexture;

    public final List<int[]> chargePositions;
    public final List<int[]> dischargePositions;
    public final List<int[]> upgradePositions;

    EnergyCubeTier(int baseCapacity,
                   int baseTransfer,
                   int chargeSlots,
                   int dischargeSlots,
                   int upgradeSlots,
                   ResourceLocation guiTexture,
                   List<int[]> chargePositions,
                   List<int[]> dischargePositions,
                   List<int[]> upgradePositions) {

        this.baseCapacity = baseCapacity;
        this.baseTransfer = baseTransfer;
        this.chargeSlots = chargeSlots;
        this.dischargeSlots = dischargeSlots;
        this.upgradeSlots = upgradeSlots;
        this.guiTexture = guiTexture;

        this.chargePositions = chargePositions;
        this.dischargePositions = dischargePositions;
        this.upgradePositions = upgradePositions;
    }

    private static int[] pos(int x, int y) {
        return new int[]{x, y};
    }

    private static ResourceLocation tex(String path) {
        return new ResourceLocation(MechaniCraftMain.MODID, path);
    }
}