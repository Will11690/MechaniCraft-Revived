package com.github.will11690.mechanicraft_revived.blocks.basic.solidfuelgen;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BasicSolidFuelGeneratorScreen extends AbstractContainerScreen<BasicSolidFuelGeneratorContainer> {

    private static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(MechaniCraftMain.MODID, "textures/gui/generator/basic_solid_fuel_generator.png");

    // Burn time (flame) texture
    public static final int BURN_METER_FROM_X     = 176;
    public static final int BURN_METER_FROM_Y     = 0;
    public static final int BURN_METER_WIDTH      = 14;
    public static final int BURN_METER_HEIGHT     = 14;
    // Anchor location in GUI space (bottom-left of flame)
    public static final int BURN_METER_TO_X       = 81;
    public static final int BURN_METER_TO_Y       = 69;

    // Energy bar texture
    public static final int ENERGY_BAR_FROM_X     = 176;
    public static final int ENERGY_BAR_FROM_Y     = 14;
    public static final int ENERGY_BAR_WIDTH      = 18;
    public static final int ENERGY_BAR_HEIGHT     = 47;
    // Anchor location in GUI space (bottom-left of bar)
    public static final int ENERGY_BAR_TO_X       = 7;
    public static final int ENERGY_BAR_TO_Y       = 54;

    public BasicSolidFuelGeneratorScreen(BasicSolidFuelGeneratorContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        int posX = this.leftPos;
        int posY = this.topPos;

        // Draw the base GUI
        guiGraphics.blit(GUI_TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight);

        // ----- Burn / flame meter -----
        int burn      = this.menu.getBurnTime();
        int burnTotal = this.menu.getBurnTimeTotal();
        if (burn > 0 && burnTotal > 0) {
            int height = burn * BURN_METER_HEIGHT / burnTotal;   // scaled height (0..14)
            if (height > 0) {
                // Bottom-aligned to (BURN_METER_TO_X, BURN_METER_TO_Y)
                int destX = posX + BURN_METER_TO_X;
                int destY = posY + BURN_METER_TO_Y - height;
                int srcX  = BURN_METER_FROM_X;
                int srcY  = BURN_METER_FROM_Y + (BURN_METER_HEIGHT - height);

                guiGraphics.blit(
                        GUI_TEXTURE,
                        destX,
                        destY,
                        srcX,
                        srcY,
                        BURN_METER_WIDTH,
                        height
                );
            }
        }

        // ----- Energy bar -----
        int energy    = this.menu.getEnergy();
        int energyMax = this.menu.getEnergyMax();
        if (energy > 0 && energyMax > 0) {
            int height = energy * ENERGY_BAR_HEIGHT / energyMax; // scaled height (0..47)
            if (height > 0) {
                // Bottom-aligned to (ENERGY_BAR_TO_X, ENERGY_BAR_TO_Y)
                int destX = posX + ENERGY_BAR_TO_X;
                int destY = posY + ENERGY_BAR_TO_Y - height;
                int srcX  = ENERGY_BAR_FROM_X;
                int srcY  = ENERGY_BAR_FROM_Y + (ENERGY_BAR_HEIGHT - height);

                guiGraphics.blit(
                        GUI_TEXTURE,
                        destX,
                        destY,
                        srcX,
                        srcY,
                        ENERGY_BAR_WIDTH,
                        height
                );
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Shift title to the right so the energy bar column is clear
        guiGraphics.drawString(this.font, this.title, 28, 6, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                8, this.imageHeight - 96 + 4, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY); // slot/item tooltips
        renderExtraTooltips(guiGraphics, mouseX, mouseY); // energy/fuel bars
    }

    private void renderExtraTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        // Hover rects are defined in GUI-local coordinates (relative to leftPos / topPos)

        // Energy bar hover area (full bar, not just current fill)
        int energyX = ENERGY_BAR_TO_X;
        int energyY = ENERGY_BAR_TO_Y - ENERGY_BAR_HEIGHT;
        int energyW = ENERGY_BAR_WIDTH;
        int energyH = ENERGY_BAR_HEIGHT;

        // Fuel (burn) hover area
        int burnX = BURN_METER_TO_X;
        int burnY = BURN_METER_TO_Y - BURN_METER_HEIGHT;
        int burnW = BURN_METER_WIDTH;
        int burnH = BURN_METER_HEIGHT;

        // Energy bar tooltip
        if (isHovering(energyX, energyY, energyW, energyH, mouseX, mouseY)) {

            int energy    = this.menu.getEnergy();
            int energyMax = this.menu.getEnergyMax();

            List<Component> list = new ArrayList<>();

            if (hasShiftDown()) {
                // Exact FE
                list.add(Component.literal(
                        "Energy: " + energy + " / " + energyMax + " FE"
                ));
            } else {
                // Shortened with suffix (k, M, etc.)
                list.add(Component.literal(
                        "Energy: " +
                                Utils.withSuffixEnergy(energy) + " / " +
                                Utils.withSuffixEnergy(energyMax) + " FE"
                ));
                list.add(Component.literal("Hold \u00A7eShift\u00A7r for more info"));
            }

            guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
            return; // don’t show burn tooltip at the same time
        }

        // Fuel / burn tooltip
        if (isHovering(burnX, burnY, burnW, burnH, mouseX, mouseY)) {

            int burn      = this.menu.getBurnTime();
            int burnTotal = this.menu.getBurnTimeTotal();

            List<Component> list = new ArrayList<>();

            if (hasShiftDown()) {
                // Exact ticks
                list.add(Component.literal(
                        "Fuel: " + burn + " / " + burnTotal + " ticks"
                ));
            } else {
                // Seconds (and larger units) via Utils.withSuffixTime
                list.add(Component.literal(
                        "Fuel: " +
                                Utils.withSuffixTime(burn) + " / " +
                                Utils.withSuffixTime(burnTotal)
                ));
                list.add(Component.literal("Hold \u00A7eShift\u00A7r for more info"));
            }

            guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
        }
    }
}