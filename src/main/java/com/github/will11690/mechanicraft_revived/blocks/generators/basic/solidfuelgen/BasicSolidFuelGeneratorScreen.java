package com.github.will11690.mechanicraft_revived.blocks.generators.basic.solidfuelgen;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.util.Utils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BasicSolidFuelGeneratorScreen extends AbstractContainerScreen<BasicSolidFuelGeneratorContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MechaniCraftMain.MODID, "textures/gui/generator/basic_solid_fuel_generator.png");

    // Burn time (flame) texture
    public static final int BURN_METER_FROM_X = 176;
    public static final int BURN_METER_FROM_Y = 0;
    public static final int BURN_METER_WIDTH  = 14;
    public static final int BURN_METER_HEIGHT = 14;
    public static final int BURN_METER_TO_X   = 81;
    public static final int BURN_METER_TO_Y   = 69;

    // Energy bar texture
    public static final int ENERGY_BAR_FROM_X = 176;
    public static final int ENERGY_BAR_FROM_Y = 14;
    public static final int ENERGY_BAR_WIDTH  = 18;
    public static final int ENERGY_BAR_HEIGHT = 47;
    public static final int ENERGY_BAR_TO_X   = 7;
    public static final int ENERGY_BAR_TO_Y   = 54;

    public BasicSolidFuelGeneratorScreen(BasicSolidFuelGeneratorContainer menu, Inventory inv, Component title) {

        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int posX = this.leftPos;
        int posY = this.topPos;

        guiGraphics.blit(GUI_TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight);

        int burn      = this.menu.getBurnTime();
        int burnTotal = this.menu.getBurnTimeTotal();
        if (burn > 0 && burnTotal > 0) {

            int height = burn * BURN_METER_HEIGHT / burnTotal;
            if (height > 0) {

                int destX = posX + BURN_METER_TO_X;
                int destY = posY + BURN_METER_TO_Y - height;
                int srcX  = BURN_METER_FROM_X;
                int srcY  = BURN_METER_FROM_Y + (BURN_METER_HEIGHT - height);

                guiGraphics.blit(GUI_TEXTURE, destX, destY, srcX, srcY, BURN_METER_WIDTH, height);
            }
        }

        int energy    = this.menu.getEnergy();
        int energyMax = this.menu.getEnergyMax();
        if (energy > 0 && energyMax > 0) {

            int height = energy * ENERGY_BAR_HEIGHT / energyMax;
            if (height > 0) {

                int destX = posX + ENERGY_BAR_TO_X;
                int destY = posY + ENERGY_BAR_TO_Y - height;
                int srcX  = ENERGY_BAR_FROM_X;
                int srcY  = ENERGY_BAR_FROM_Y + (ENERGY_BAR_HEIGHT - height);

                guiGraphics.blit(GUI_TEXTURE, destX, destY, srcX, srcY, ENERGY_BAR_WIDTH, height);
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {

        guiGraphics.drawString(this.font, this.title, 28, 6, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 4, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int burnX = BURN_METER_TO_X;
        int burnY = BURN_METER_TO_Y - BURN_METER_HEIGHT;
        int burnW = BURN_METER_WIDTH;
        int burnH = BURN_METER_HEIGHT;

        if (isHovering(burnX, burnY, burnW, burnH, mouseX, mouseY)) {

            if (Screen.hasShiftDown()) {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(List.of(
                Component.translatable("com.github.will11690.mechanicraft_revived.screen.basic_solid_fuel_generator.burn_time", this.menu.getBurnTime(), this.menu.getBurnTimeTotal()))), mouseX, mouseY);
            } else {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(Arrays.asList(
                Component.translatable("com.github.will11690.mechanicraft_revived.screen.basic_solid_fuel_generator.burn_time", Utils.withSuffixTime(this.menu.getBurnTime()), Utils.withSuffixTime(this.menu.getBurnTimeTotal())),
                Component.translatable("com.github.will11690.mechanicraft_revived.screen.gui_details"))), mouseX, mouseY);
            }
            return;
        }

        int energyX = ENERGY_BAR_TO_X;
        int energyY = ENERGY_BAR_TO_Y - ENERGY_BAR_HEIGHT;
        int energyW = ENERGY_BAR_WIDTH;
        int energyH = ENERGY_BAR_HEIGHT;

        if (isHovering(energyX, energyY, energyW, energyH, mouseX, mouseY)) {

            if (Screen.hasShiftDown()) {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(List.of(
                Component.translatable("com.github.will11690.mechanicraft_revived.screen.basic_solid_fuel_generator.energy", this.menu.getEnergy(), this.menu.getEnergyMax()))), mouseX, mouseY);
            } else {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(Arrays.asList(
                Component.translatable("com.github.will11690.mechanicraft_revived.screen.basic_solid_fuel_generator.energy", Utils.withSuffixEnergy(this.menu.getEnergy()), Utils.withSuffixEnergy(this.menu.getEnergyMax())),
                Component.translatable("com.github.will11690.mechanicraft_revived.screen.gui_details"))), mouseX, mouseY);
            }
        }
    }
}