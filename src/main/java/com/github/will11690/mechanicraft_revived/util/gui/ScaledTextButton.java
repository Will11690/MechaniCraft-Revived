package com.github.will11690.mechanicraft_revived.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ScaledTextButton extends Button {

    private final Font font;

    public ScaledTextButton(int x, int y, int width, int height,
                            Component message, OnPress onPress, Font font) {

        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.font = font != null ? font : Minecraft.getInstance().font;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        // Draw vanilla button background, but suppress its default text
        Component original = this.getMessage();
        this.setMessage(Component.empty());
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        this.setMessage(original);

        if (original == null) return;

        String text = original.getString();
        if (text == null || text.isEmpty()) return;

        int textWidth = font.width(text);
        int maxWidth  = this.getWidth() - 4;
        float scale   = 1.0F;
        if (textWidth > maxWidth) {
            scale = (float) maxWidth / (float) textWidth;
        }

        // Also ensure text height fits inside button
        int lineHeight      = font.lineHeight;
        float maxHeightScale = (this.getHeight() - 4) / (float) lineHeight;
        if (scale > maxHeightScale) {
            scale = maxHeightScale;
        }

        guiGraphics.pose().pushPose();

        float centerX = this.getX() + this.getWidth() / 2.0F;
        float centerY = this.getY() + (this.getHeight() - lineHeight * scale) / 2.0F;

        guiGraphics.pose().translate(centerX, centerY, 0);
        guiGraphics.pose().scale(scale, scale, 1.0F);

        guiGraphics.drawCenteredString(
                font,
                original,
                0,
                0,
                this.getFGColor()
        );

        guiGraphics.pose().popPose();
    }
}