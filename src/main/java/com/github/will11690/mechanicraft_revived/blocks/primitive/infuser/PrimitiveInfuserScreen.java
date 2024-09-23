package com.github.will11690.mechanicraft_revived.blocks.primitive.infuser;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PrimitiveInfuserScreen extends AbstractContainerScreen<PrimitiveInfuserContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MechaniCraftMain.MODID, "textures/gui/infuser/basic_metallic_infuser.png");

    //TODO add in hover over(INFO) tool tips to the progress and fuel bars

    //Burn Time Texture
    public static final int BURN_METER_FROM_X = 176;
    public static final int BURN_METER_FROM_Y = 0;
    public static final int BURN_METER_WIDTH = 14;
    public static final int BURN_METER_HEIGHT = 14;
    public static final int BURN_METER_TO_X = 46;
    public static final int BURN_METER_TO_Y = 37;

    //Burn Time INFO
    public static final int INFO_BURN_METER_WIDTH = 14;
    public static final int INFO_BURN_METER_HEIGHT = 14;
    public static final int INFO_BURN_METER_TO_X = 45;
    public static final int INFO_BURN_METER_TO_Y = 37;

    //Progress Texture
    public static final int PROGRESS_METER_FROM_X = 176;
    public static final int PROGRESS_METER_FROM_Y = 14;
    public static final int PROGRESS_METER_WIDTH = 24;
    public static final int PROGRESS_METER_HEIGHT = 17;
    public static final int PROGRESS_METER_TO_X = 84;
    public static final int PROGRESS_METER_TO_Y = 36;

    //Progress INFO
    public static final int INFO_PROGRESS_METER_WIDTH = 24;
    public static final int INFO_PROGRESS_METER_HEIGHT = 17;
    public static final int INFO_PROGRESS_METER_TO_X = 84;
    public static final int INFO_PROGRESS_METER_TO_Y = 35;

    public PrimitiveInfuserScreen(PrimitiveInfuserContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int posX = (width - imageWidth) / 2;
        int posY = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, posX, posY, 0, 0, imageWidth, imageHeight);


        if(this.menu.isBurning()) {

            int burnRemaining = this.menu.getBurnScaled(BURN_METER_HEIGHT);/*(this.menu.getBurnTime() * maxHeight) / this.menu.getTotalBurnTime();*/
            guiGraphics.blit(GUI_TEXTURE, posX + BURN_METER_TO_X, posY + BURN_METER_TO_Y + BURN_METER_HEIGHT - burnRemaining, BURN_METER_FROM_X, BURN_METER_HEIGHT - burnRemaining, BURN_METER_WIDTH, burnRemaining);
        }
        if(this.menu.isCrafting()) {

            int cookProgress = this.menu.getProgressScaled(PROGRESS_METER_WIDTH)/* + 1*/;
            guiGraphics.blit(GUI_TEXTURE, posX + PROGRESS_METER_TO_X, posY + PROGRESS_METER_TO_Y, PROGRESS_METER_FROM_X, PROGRESS_METER_FROM_Y, cookProgress, PROGRESS_METER_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
