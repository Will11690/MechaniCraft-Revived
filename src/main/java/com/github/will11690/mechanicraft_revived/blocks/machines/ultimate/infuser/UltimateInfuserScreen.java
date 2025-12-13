package com.github.will11690.mechanicraft_revived.blocks.machines.ultimate.infuser;

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

public class UltimateInfuserScreen extends AbstractContainerScreen<UltimateInfuserContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MechaniCraftMain.MODID, "textures/gui/infuser/ultimate_metallic_infuser.png");

    // Progress arrow
    public static final int PROGRESS_METER_FROM_X = 176;
    public static final int PROGRESS_METER_FROM_Y = 0;
    public static final int PROGRESS_METER_WIDTH  = 24;
    public static final int PROGRESS_METER_HEIGHT = 17;
    public static final int PROGRESS_METER_TO_X   = 92;
    public static final int PROGRESS_METER_TO_Y   = 32;

    public static final int INFO_PROGRESS_METER_WIDTH  = 24;
    public static final int INFO_PROGRESS_METER_HEIGHT = 17;
    public static final int INFO_PROGRESS_METER_TO_X   = 92;
    public static final int INFO_PROGRESS_METER_TO_Y   = 32;

    // Energy bar texture
    public static final int ENERGY_BAR_FROM_X = 176;
    public static final int ENERGY_BAR_FROM_Y = 17;
    public static final int ENERGY_BAR_WIDTH  = 18;
    public static final int ENERGY_BAR_HEIGHT = 47;
    public static final int ENERGY_BAR_TO_X   = 7;
    public static final int ENERGY_BAR_TO_Y   = 54;

    public UltimateInfuserScreen(UltimateInfuserContainer menu, Inventory playerInventory, Component title) {

        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int posX = this.leftPos;
        int posY = this.topPos;

        guiGraphics.blit(GUI_TEXTURE, posX, posY, 0, 0, imageWidth, imageHeight);

        int energy    = this.menu.getEnergyStored();
        int energyMax = this.menu.getEnergyCapacity();
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

        if (this.menu.isCrafting()) {

            int cookProgress = this.menu.getProgressScaled(PROGRESS_METER_WIDTH);
            guiGraphics.blit(GUI_TEXTURE, posX + PROGRESS_METER_TO_X, posY + PROGRESS_METER_TO_Y, PROGRESS_METER_FROM_X, PROGRESS_METER_FROM_Y, cookProgress, PROGRESS_METER_HEIGHT);
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

        if (mouseX > (getGuiLeft() + INFO_PROGRESS_METER_TO_X) && mouseX < (getGuiLeft() + INFO_PROGRESS_METER_TO_X) + INFO_PROGRESS_METER_WIDTH &&
                mouseY > (getGuiTop() + INFO_PROGRESS_METER_TO_Y) && mouseY < (getGuiTop() + INFO_PROGRESS_METER_TO_Y) + INFO_PROGRESS_METER_HEIGHT) {

            if (Screen.hasShiftDown()) {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(List.of(
                        Component.translatable("com.github.will11690.mechanicraft_revived.screen.ultimate_metallic_infuser.cook_progress",
                                this.menu.getProgress(), this.menu.getMaxProgress()))), mouseX, mouseY);
            } else {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(Arrays.asList(
                        Component.translatable("com.github.will11690.mechanicraft_revived.screen.ultimate_metallic_infuser.cook_progress", Utils.withSuffixTime(this.menu.getProgress()), Utils.withSuffixTime(this.menu.getMaxProgress())),
                        Component.translatable("com.github.will11690.mechanicraft_revived.screen.gui_details"))), mouseX, mouseY);
            }
        }

        int energyX = ENERGY_BAR_TO_X;
        int energyY = ENERGY_BAR_TO_Y - ENERGY_BAR_HEIGHT;
        int energyW = ENERGY_BAR_WIDTH;
        int energyH = ENERGY_BAR_HEIGHT;

        if (isHovering(energyX, energyY, energyW, energyH, mouseX, mouseY)) {

            if (Screen.hasShiftDown()) {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(List.of(
                        Component.translatable("com.github.will11690.mechanicraft_revived.screen.ultimate_metallic_infuser.energy",
                                this.menu.getEnergyStored(), this.menu.getEnergyCapacity()))), mouseX, mouseY);
            } else {

                guiGraphics.renderTooltip(this.font, Language.getInstance().getVisualOrder(Arrays.asList(
                        Component.translatable("com.github.will11690.mechanicraft_revived.screen.ultimate_metallic_infuser.energy", Utils.withSuffixEnergy(this.menu.getEnergyStored()), Utils.withSuffixEnergy(this.menu.getEnergyCapacity())),
                        Component.translatable("com.github.will11690.mechanicraft_revived.screen.gui_details"))), mouseX, mouseY);
            }
        }
    }
}