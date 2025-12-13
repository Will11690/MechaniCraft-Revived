package com.github.will11690.mechanicraft_revived.blocks.transport.base.gui;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.network.MechaniCraftNetwork;
import com.github.will11690.mechanicraft_revived.network.packet.server.OpenPipeConfigPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PipeFilterScreen extends AbstractContainerScreen<PipeFilterContainer> {

    private static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(MechaniCraftMain.MODID, "textures/gui/pipe/pipe_filter.png");

    public PipeFilterScreen(PipeFilterContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        // small "Back" button in the top-right
        int x = leftPos + imageWidth - 20;
        int y = topPos + 6;

        Button back = Button.builder(Component.literal("X"), btn -> {
            if (this.minecraft == null || this.minecraft.player == null) return;
            MechaniCraftNetwork.sendToServer(new OpenPipeConfigPacket(menu.pipeBE.getBlockPos()));
        }).bounds(x, y, 12, 12).build();

        addRenderableWidget(back);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick,
                            int mouseX, int mouseY) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        guiGraphics.blit(GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                8, this.imageHeight - 96 + 4, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}