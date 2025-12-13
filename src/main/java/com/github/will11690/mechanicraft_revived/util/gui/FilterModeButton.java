package com.github.will11690.mechanicraft_revived.util.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public class FilterModeButton extends ScaledTextButton {

    private final BooleanSupplier isWhitelistSupplier;

    public FilterModeButton(int x, int y, int width, int height,
                            BooleanSupplier isWhitelistSupplier,
                            OnPress onPress,
                            Font font) {

        // Empty label – we'll draw the square ourselves
        super(x, y, width, height, Component.empty(), onPress, font);
        this.isWhitelistSupplier = isWhitelistSupplier;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        // Draw the normal Minecraft button (via ScaledTextButton → Button)
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        // Then overlay our square in the center
        boolean whitelist = isWhitelistSupplier.getAsBoolean();

        int available = Math.min(getWidth(), getHeight()) - 6;
        if (available < 4) {
            available = Math.min(getWidth(), getHeight()) - 2;
        }
        int half = available / 2;

        int cx = getX() + getWidth()  / 2;
        int cy = getY() + getHeight() / 2;

        int sx0 = cx - half;
        int sy0 = cy - half;
        int sx1 = cx + half;
        int sy1 = cy + half;

        int fillColor = whitelist ? 0xFFFFFFFF : 0xFF000000;
        guiGraphics.fill(sx0, sy0, sx1, sy1, fillColor);

        // Subtle border so white square stands out
        int borderColor = whitelist ? 0xFF404040 : 0xFFBBBBBB;
        guiGraphics.hLine(sx0, sx1 - 1, sy0,     borderColor);
        guiGraphics.hLine(sx0, sx1 - 1, sy1 - 1, borderColor);
        guiGraphics.vLine(sx0,     sy0, sy1 - 1, borderColor);
        guiGraphics.vLine(sx1 - 1, sy0, sy1 - 1, borderColor);
    }
}