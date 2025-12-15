package com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.gui;

import com.github.will11690.mechanicraft_revived.network.MechaniCraftNetwork;
import com.github.will11690.mechanicraft_revived.network.packet.server.EnergyCubeConfigPacket;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import com.github.will11690.mechanicraft_revived.util.block.RedstoneMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnergyCubeScreen extends AbstractContainerScreen<EnergyCubeContainer> {

    private Direction selectedSide = Direction.NORTH;

    private Button ioButton;
    private Button rsButton;
    private EditBox limitBox;
    private Button applyLimitButton;

    private final Button[] sideButtons = new Button[6];

    public EnergyCubeScreen(EnergyCubeContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private ResourceLocation tex() {
        return menu.getTier().guiTexture;
    }

    @Override
    protected void init() {
        super.init();

        int left = leftPos;
        int top = topPos;

        // Side select buttons (temporary layout; we’ll align to your art next)
        int sx = left + 8;
        int sy = top + 18;

        Direction[] dirs = Direction.values();
        for (int i = 0; i < dirs.length; i++) {
            Direction d = dirs[i];

            int col = i % 3;
            int row = i / 3;

            int bx = sx + col * 18;
            int by = sy + row * 18;

            sideButtons[i] = addRenderableWidget(
                    Button.builder(Component.literal(shortDir(d)), btn -> {
                        selectedSide = d;
                        refreshControls();
                    }).bounds(bx, by, 16, 16).build()
            );
        }

        ioButton = addRenderableWidget(
                Button.builder(Component.literal("IO"), btn -> cycleIO())
                        .bounds(left + 85, top + 18, 80, 18).build()
        );

        rsButton = addRenderableWidget(
                Button.builder(Component.literal("RS"), btn -> cycleRS())
                        .bounds(left + 85, top + 38, 80, 18).build()
        );

        limitBox = new EditBox(font, left + 85, top + 58, 50, 16, Component.literal("Limit"));
        limitBox.setMaxLength(9);
        addRenderableWidget(limitBox);

        applyLimitButton = addRenderableWidget(
                Button.builder(Component.literal("Set"), btn -> applyLimit())
                        .bounds(left + 139, top + 58, 26, 16).build()
        );

        refreshControls();
    }

    private void refreshControls() {
        var cube = menu.cubeBE;

        IOMode io = cube.getSideIOMode(selectedSide);
        RedstoneMode rs = cube.getSideRedstoneMode(selectedSide);
        int limit = cube.getSideTransferLimit(selectedSide);

        ioButton.setMessage(Component.literal("IO: " + io.name()));
        rsButton.setMessage(Component.literal("RS: " + rs.name()));
        limitBox.setValue(Integer.toString(limit));
    }

    private void cycleIO() {
        var cube = menu.cubeBE;
        IOMode cur = cube.getSideIOMode(selectedSide);
        IOMode next = nextIOMode(cur);

        MechaniCraftNetwork.sendToServer(new EnergyCubeConfigPacket(
                cube.getBlockPos(), selectedSide, EnergyCubeConfigPacket.ChangeType.IO, next.ordinal()
        ));
    }

    private void cycleRS() {
        var cube = menu.cubeBE;
        RedstoneMode cur = cube.getSideRedstoneMode(selectedSide);
        RedstoneMode next = nextRSMode(cur);

        MechaniCraftNetwork.sendToServer(new EnergyCubeConfigPacket(
                cube.getBlockPos(), selectedSide, EnergyCubeConfigPacket.ChangeType.RS, next.ordinal()
        ));
    }

    private void applyLimit() {
        var cube = menu.cubeBE;

        int v;
        try {
            v = Integer.parseInt(limitBox.getValue());
        } catch (NumberFormatException e) {
            v = 0;
        }

        MechaniCraftNetwork.sendToServer(new EnergyCubeConfigPacket(
                cube.getBlockPos(), selectedSide, EnergyCubeConfigPacket.ChangeType.LIMIT, v
        ));
    }

    private static IOMode nextIOMode(IOMode cur) {
        IOMode[] v = IOMode.values();
        return v[(cur.ordinal() + 1) % v.length];
    }

    private static RedstoneMode nextRSMode(RedstoneMode cur) {
        RedstoneMode[] v = RedstoneMode.values();
        return v[(cur.ordinal() + 1) % v.length];
    }

    private static String shortDir(Direction d) {
        return switch (d) {
            case NORTH -> "N";
            case SOUTH -> "S";
            case EAST -> "E";
            case WEST -> "W";
            case UP -> "U";
            case DOWN -> "D";
        };
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partialTick, int mouseX, int mouseY) {
        gg.blit(tex(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Energy fill overlay (matches the vertical bar area in your art)
        int stored = menu.getEnergyStored();
        int cap = Math.max(1, menu.getEnergyCapacity());

        int barX = leftPos + 79;
        int barY = topPos + 14;
        int barH = 54;

        int filled = (int) ((stored / (double) cap) * barH);
        gg.fill(barX, barY + (barH - filled), barX + 12, barY + barH, 0xFF00FF00);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        renderBackground(gg);
        super.render(gg, mouseX, mouseY, partialTick);

        int stored = menu.getEnergyStored();
        int cap = menu.getEnergyCapacity();

        gg.drawString(font, title, leftPos + 8, topPos + 6, 0x404040, false);
        gg.drawString(font, "Energy: " + stored + " / " + cap, leftPos + 8, topPos + 72, 0x404040, false);

        renderTooltip(gg, mouseX, mouseY);
    }
}