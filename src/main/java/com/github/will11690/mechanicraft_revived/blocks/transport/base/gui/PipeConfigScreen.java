package com.github.will11690.mechanicraft_revived.blocks.transport.base.gui;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.FilterMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeLogicMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeType;
import com.github.will11690.mechanicraft_revived.network.MechaniCraftNetwork;
import com.github.will11690.mechanicraft_revived.network.packet.server.OpenPipeFilterPacket;
import com.github.will11690.mechanicraft_revived.network.packet.server.UpdatePipeSideConfigPacket;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import com.github.will11690.mechanicraft_revived.util.block.RedstoneMode;
import com.github.will11690.mechanicraft_revived.util.gui.FilterModeButton;
import com.github.will11690.mechanicraft_revived.util.gui.ScaledTextButton;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class PipeConfigScreen extends AbstractContainerScreen<PipeConfigContainer> {

    private static final ResourceLocation GUI_TEXTURE_NARROW =
            new ResourceLocation(MechaniCraftMain.MODID, "textures/gui/pipe/pipe_config.png");
    private static final ResourceLocation GUI_TEXTURE_WIDE =
            new ResourceLocation(MechaniCraftMain.MODID, "textures/gui/pipe/pipe_config_both.png");

    private static final int MAX_CHANNEL  = 255;
    private static final int MAX_PRIORITY = 32;
    private static final int BOTH_COLUMN_WIDTH = 116;
    private static final String KEY_BASE = "com.github.will11690.mechanicraft_revived.screen.pipe.";

    private enum RowKind { CHANNEL, PRIORITY, LIMIT }

    private Direction selectedSide = Direction.NORTH;
    private final Map<Direction, Button> sideButtons = new EnumMap<>(Direction.class);

    private Button ioModeButton;
    private Button logicModeButton;
    private Button redstoneModeButton;

    private EditBox channelField;
    private EditBox priorityField;
    private EditBox limitField;

    private EditBox outChannelField;
    private EditBox outPriorityField;
    private EditBox outLimitField;

    private Button           filterButtonLeft;
    private FilterModeButton filterModeButtonLeft;
    private Button           filterButtonRight;
    private FilterModeButton filterModeButtonRight;

    private int channelRowY;
    private int priorityRowY;
    private int limitRowY;
    private int filterRowY; // shared for both layouts

    private boolean bothLayout = false;

    private final EnumMap<Direction, PipeSideConfig> clientConfigs = new EnumMap<>(Direction.class);

    public PipeConfigScreen(PipeConfigContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    /* ----------------- Translation helpers ----------------- */

    private Component ioButtonText(IOMode mode) {
        Component value = switch (mode) {
            case DISABLED -> Component.translatable(KEY_BASE + "io.disabled");
            case EXTRACT  -> Component.translatable(KEY_BASE + "io.extract");
            case INSERT   -> Component.translatable(KEY_BASE + "io.insert");
            case BOTH     -> Component.translatable(KEY_BASE + "io.both");
        };
        return Component.translatable(KEY_BASE + "io").append(" ").append(value);
    }

    private Component logicButtonText(PipeLogicMode mode) {
        Component value = switch (mode) {
            case NEAREST_FIRST  -> Component.translatable(KEY_BASE + "logic.nearest");
            case FURTHEST_FIRST -> Component.translatable(KEY_BASE + "logic.furthest");
            case ROUND_ROBIN    -> Component.translatable(KEY_BASE + "logic.round_robin");
        };
        return Component.translatable(KEY_BASE + "logic").append(" ").append(value);
    }

    private Component redstoneButtonText(RedstoneMode mode) {
        Component value = switch (mode) {
            case IGNORED -> Component.translatable(KEY_BASE + "redstone.ignored");
            case HIGH    -> Component.translatable(KEY_BASE + "redstone.high");
            case LOW     -> Component.translatable(KEY_BASE + "redstone.low");
            case OFF     -> Component.translatable(KEY_BASE + "redstone.off");
        };
        return Component.translatable(KEY_BASE + "redstone").append(" ").append(value);
    }

    private Component selectedPrefix() { return Component.translatable(KEY_BASE + "selected"); }

    private Component minusSingleText() { return Component.translatable(KEY_BASE + "increment.single"); }
    private Component plusSingleText()  { return Component.translatable(KEY_BASE + "decrement.single"); }

    private Component minusQuarterText(int v) { return Component.translatable(KEY_BASE + "increment.quarter", v); }
    private Component minusHalfText(int v)    { return Component.translatable(KEY_BASE + "increment.half", v); }
    private Component plusQuarterText(int v)  { return Component.translatable(KEY_BASE + "decrement.quarter", v); }
    private Component plusHalfText(int v)     { return Component.translatable(KEY_BASE + "decrement.half", v); }

    /* ----------------- Init / layout ----------------- */

    @Override
    protected void init() {
        if (clientConfigs.isEmpty()) {
            for (Direction dir : Direction.values()) {
                PipeSideConfig src = menu.pipeBE.getSideConfig(dir);
                PipeSideConfig copy = new PipeSideConfig();
                if (src != null) {
                    copy.ioMode               = src.ioMode;
                    copy.redstoneMode         = src.redstoneMode;
                    copy.logicMode            = src.logicMode;
                    copy.extractChannel       = src.extractChannel;
                    copy.extractPriority      = src.extractPriority;
                    copy.extractTransferLimit = src.extractTransferLimit;
                    copy.insertChannel        = src.insertChannel;
                    copy.insertPriority       = src.insertPriority;
                    copy.insertTransferLimit  = src.insertTransferLimit;
                    copy.extractFilterMode    = src.extractFilterMode;
                    copy.insertFilterMode     = src.insertFilterMode;
                }
                clientConfigs.put(dir, copy);
            }
        }

        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        bothLayout = (cfg.ioMode == IOMode.BOTH);
        this.imageWidth  = bothLayout ? 256 : 176;
        this.imageHeight = 166;

        super.init();
        this.clearWidgets();
        sideButtons.clear();

        channelField = priorityField = limitField = null;
        outChannelField = outPriorityField = outLimitField = null;
        filterButtonLeft = filterButtonRight = null;
        filterModeButtonLeft = filterModeButtonRight = null;

        if (bothLayout) {
            channelRowY  = 107;
            priorityRowY = 128;
            limitRowY    = 149;
        } else {
            channelRowY  = 98;
            priorityRowY = 122;
            limitRowY    = 146;
        }

        // Base filter position: above channel/priority/limit rows
        filterRowY = channelRowY - 25;

        // In BOTH layout, drop filter row by 2 px
        if (bothLayout) {
            filterRowY += 2;
        }

        if (bothLayout) initBothLayout();
        else initSingleLayout();

        updateModeButtonLabels();
        applyConfigToFields();
    }

    private void initSingleLayout() {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        IOMode mode = cfg.ioMode;
        boolean showFilters = menu.pipeBE.getPipeType() != PipeType.ENERGY;

        int cx = leftPos + 138;
        int cy = topPos + 66;
        int sideButtonSize = 12;
        addSideButton(Direction.NORTH, cx, cy - 15, sideButtonSize);
        addSideButton(Direction.SOUTH, cx, cy + 15, sideButtonSize);
        addSideButton(Direction.WEST,  cx - 15, cy, sideButtonSize);
        addSideButton(Direction.EAST,  cx + 15, cy, sideButtonSize);
        addSideButton(Direction.UP,    cx - 15, cy - 15, sideButtonSize);
        addSideButton(Direction.DOWN,  cx + 15, cy + 15, sideButtonSize);

        int buttonHeight = 14;

        // IO / RS / MODE rows
        int filterY = topPos + filterRowY;
        int rsY     = filterY - 15;
        int ioY     = rsY - 15;
        int logicY  = ioY - 15;

        // We want vertical order: IO (top), RS (middle), MODE (bottom)
        int topModeY    = logicY; // IO
        int middleModeY = ioY;    // RS
        int bottomModeY = rsY;    // MODE

        // If disabled, only show IO button so it can be re-enabled
        if (mode == IOMode.DISABLED) {
            ioModeButton = this.addRenderableWidget(new ScaledTextButton(
                    leftPos + 8, topModeY, 90, buttonHeight,
                    Component.literal(""),
                    btn -> {
                        cycleIOMode();
                        sendConfigToServer(false);
                        this.init();
                    },
                    this.font
            ));
            return;
        }

        if (showFilters) {
            // Filter row ABOVE channel/prio/limit (single layout)
            filterButtonLeft = this.addRenderableWidget(new ScaledTextButton(
                    leftPos + 8, filterY, 70, buttonHeight,
                    Component.translatable(KEY_BASE + "filter"),
                    btn -> openFilterForSelectedSide(false),
                    this.font
            ));

            filterModeButtonLeft = this.addRenderableWidget(new FilterModeButton(
                    leftPos + 82, filterY, 20, buttonHeight,
                    () -> {
                        PipeSideConfig c = getDisplayConfig(selectedSide);
                        FilterMode fm = (c.ioMode == IOMode.INSERT)
                                ? c.insertFilterMode
                                : c.extractFilterMode;
                        return fm == FilterMode.WHITELIST;
                    },
                    btn -> {
                        PipeSideConfig c = getDisplayConfig(selectedSide);
                        if (c.ioMode == IOMode.INSERT) {
                            c.insertFilterMode = (c.insertFilterMode == FilterMode.BLACKLIST)
                                    ? FilterMode.WHITELIST : FilterMode.BLACKLIST;
                        } else {
                            c.extractFilterMode = (c.extractFilterMode == FilterMode.BLACKLIST)
                                    ? FilterMode.WHITELIST : FilterMode.BLACKLIST;
                        }
                        sendConfigToServer(false);
                    },
                    this.font
            ));
        }

        // IO (top row)
        ioModeButton = this.addRenderableWidget(new ScaledTextButton(
                leftPos + 8, topModeY, 90, buttonHeight,
                Component.literal(""),
                btn -> {
                    cycleIOMode();
                    sendConfigToServer(false);
                    this.init();
                },
                this.font
        ));

        // RS (middle row)
        redstoneModeButton = this.addRenderableWidget(new ScaledTextButton(
                leftPos + 8, middleModeY, 90, buttonHeight,
                Component.literal(""),
                btn -> {
                    cycleRedstoneMode();
                    sendConfigToServer(false);
                },
                this.font
        ));

        // MODE (logic) bottom row, only in EXTRACT
        if (mode == IOMode.EXTRACT) {
            logicModeButton = this.addRenderableWidget(new ScaledTextButton(
                    leftPos + 8, bottomModeY, 90, buttonHeight,
                    Component.literal(""),
                    btn -> {
                        cycleLogicMode();
                        sendConfigToServer(false);
                    },
                    this.font
            ));
        }

        createRowSingle(RowKind.CHANNEL);
        createRowSingle(RowKind.PRIORITY);
        createRowSingle(RowKind.LIMIT);
    }

    private void initBothLayout() {
        int buttonHeight = 14;
        int sharedX = leftPos + 8;
        int ioY = topPos + 30;
        int logicY = topPos + 45;
        int rsY = topPos + 60;

        // For BOTH layout we want IO (top) -> RS (middle) -> MODE (bottom)
        int topModeY    = ioY;    // IO
        int middleModeY = logicY; // RS
        int bottomModeY = rsY;    // MODE

        // IO (top)
        ioModeButton = this.addRenderableWidget(new ScaledTextButton(
                sharedX, topModeY, 90, buttonHeight,
                Component.literal(""),
                btn -> {
                    cycleIOMode();
                    sendConfigToServer(false);
                    this.init();
                },
                this.font
        ));

        // RS (middle)
        redstoneModeButton = this.addRenderableWidget(new ScaledTextButton(
                sharedX, middleModeY, 90, buttonHeight,
                Component.literal(""),
                btn -> {
                    cycleRedstoneMode();
                    sendConfigToServer(false);
                },
                this.font
        ));

        // MODE (logic) bottom
        logicModeButton = this.addRenderableWidget(new ScaledTextButton(
                sharedX, bottomModeY, 90, buttonHeight,
                Component.literal(""),
                btn -> {
                    cycleLogicMode();
                    sendConfigToServer(false);
                },
                this.font
        ));

        int cx = leftPos + this.imageWidth - 40;
        int cy = topPos + 46;
        int sideButtonSize = 12;
        addSideButton(Direction.NORTH, cx, cy - 15, sideButtonSize);
        addSideButton(Direction.SOUTH, cx, cy + 15, sideButtonSize);
        addSideButton(Direction.WEST,  cx - 15, cy, sideButtonSize);
        addSideButton(Direction.EAST,  cx + 15, cy, sideButtonSize);
        addSideButton(Direction.UP,    cx - 15, cy - 15, sideButtonSize);
        addSideButton(Direction.DOWN,  cx + 15, cy + 15, sideButtonSize);

        int leftColumnX  = leftPos + 8;
        int rightColumnX = leftPos + this.imageWidth - 8 - BOTH_COLUMN_WIDTH;

        boolean showFilters = menu.pipeBE.getPipeType() != PipeType.ENERGY;
        if (showFilters) {
            int filterY = topPos + filterRowY;
            int filterButtonWidth = 70;
            int filterModeWidth = 16;
            int filterClusterWidth = filterButtonWidth + 2 + filterModeWidth;
            int leftFilterStartX = leftColumnX + (BOTH_COLUMN_WIDTH - filterClusterWidth) / 2;
            int rightFilterStartX = rightColumnX + (BOTH_COLUMN_WIDTH - filterClusterWidth) / 2;

            // LEFT = EXTRACT
            filterButtonLeft = this.addRenderableWidget(new ScaledTextButton(
                    leftFilterStartX, filterY, filterButtonWidth, buttonHeight,
                    Component.translatable(KEY_BASE + "filter"),
                    btn -> openFilterForSelectedSide(false),
                    this.font
            ));

            filterModeButtonLeft = this.addRenderableWidget(new FilterModeButton(
                    leftFilterStartX + filterButtonWidth + 2, filterY, filterModeWidth, buttonHeight,
                    () -> getDisplayConfig(selectedSide).extractFilterMode == FilterMode.WHITELIST,
                    btn -> {
                        PipeSideConfig cfg = getDisplayConfig(selectedSide);
                        cfg.extractFilterMode = (cfg.extractFilterMode == FilterMode.BLACKLIST)
                                ? FilterMode.WHITELIST : FilterMode.BLACKLIST;
                        sendConfigToServer(false);
                    },
                    this.font
            ));

            // RIGHT = INSERT
            filterButtonRight = this.addRenderableWidget(new ScaledTextButton(
                    rightFilterStartX, filterY, filterButtonWidth, buttonHeight,
                    Component.translatable(KEY_BASE + "filter"),
                    btn -> openFilterForSelectedSide(true),
                    this.font
            ));

            filterModeButtonRight = this.addRenderableWidget(new FilterModeButton(
                    rightFilterStartX + filterButtonWidth + 2, filterY, filterModeWidth, buttonHeight,
                    () -> getDisplayConfig(selectedSide).insertFilterMode == FilterMode.WHITELIST,
                    btn -> {
                        PipeSideConfig cfg = getDisplayConfig(selectedSide);
                        cfg.insertFilterMode = (cfg.insertFilterMode == FilterMode.BLACKLIST)
                                ? FilterMode.WHITELIST : FilterMode.BLACKLIST;
                        sendConfigToServer(false);
                    },
                    this.font
            ));
        }

        createRowBoth(RowKind.CHANNEL, leftColumnX, rightColumnX);
        createRowBoth(RowKind.PRIORITY, leftColumnX, rightColumnX);
        createRowBoth(RowKind.LIMIT, leftColumnX, rightColumnX);
    }

    /* ----------------- Row helpers ----------------- */

    private int rowY(RowKind kind) {
        return switch (kind) {
            case CHANNEL  -> channelRowY;
            case PRIORITY -> priorityRowY;
            case LIMIT    -> limitRowY;
        };
    }

    private void createRowSingle(RowKind kind) {
        int rowYScreen = topPos + rowY(kind);
        int rowXScreen = leftPos + 8;
        int buttonWidth = 18, buttonHeight = 12, step = buttonWidth + 2;
        int fieldWidth = 40, fieldHeight = 10;
        int fieldXScreen = rowXScreen + step * 3;
        int fieldYScreen = rowYScreen + 1;

        int maxLimit = menu.pipeBE.getTierMaxTransfer();
        int quarter, half;
        Component placeholder;

        switch (kind) {
            case CHANNEL -> {
                quarter = MAX_CHANNEL / 4;
                half    = MAX_CHANNEL / 2;
                placeholder = Component.translatable(KEY_BASE + "label.channel");

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen, rowYScreen, buttonWidth, buttonHeight,
                        minusHalfText(half),
                        btn -> adjustChannel(-half, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen + step, rowYScreen, buttonWidth, buttonHeight,
                        minusQuarterText(quarter),
                        btn -> adjustChannel(-quarter, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen + step * 2, rowYScreen, buttonWidth, buttonHeight,
                        minusSingleText(),
                        btn -> adjustChannel(-1, false),
                        this.font
                ));

                channelField = new EditBox(this.font, fieldXScreen, fieldYScreen, fieldWidth, fieldHeight, placeholder);
                channelField.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
                this.addRenderableWidget(channelField);

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2, rowYScreen, buttonWidth, buttonHeight,
                        plusSingleText(),
                        btn -> adjustChannel(1, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2 + step, rowYScreen, buttonWidth, buttonHeight,
                        plusQuarterText(quarter),
                        btn -> adjustChannel(quarter, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2 + step * 2, rowYScreen, buttonWidth, buttonHeight,
                        plusHalfText(half),
                        btn -> adjustChannel(half, false),
                        this.font
                ));
            }
            case PRIORITY -> {
                quarter = MAX_PRIORITY / 4;
                half    = MAX_PRIORITY / 2;
                placeholder = Component.translatable(KEY_BASE + "label.priority");

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen, rowYScreen, buttonWidth, buttonHeight,
                        minusHalfText(half),
                        btn -> adjustPriority(-half, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen + step, rowYScreen, buttonWidth, buttonHeight,
                        minusQuarterText(quarter),
                        btn -> adjustPriority(-quarter, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen + step * 2, rowYScreen, buttonWidth, buttonHeight,
                        minusSingleText(),
                        btn -> adjustPriority(-1, false),
                        this.font
                ));

                priorityField = new EditBox(this.font, fieldXScreen, fieldYScreen, fieldWidth, fieldHeight, placeholder);
                priorityField.setFilter(s -> s.isEmpty() || s.equals("-") ||
                        s.chars().allMatch(ch -> Character.isDigit(ch) || ch == '-'));
                this.addRenderableWidget(priorityField);

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2, rowYScreen, buttonWidth, buttonHeight,
                        plusSingleText(),
                        btn -> adjustPriority(1, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2 + step, rowYScreen, buttonWidth, buttonHeight,
                        plusQuarterText(quarter),
                        btn -> adjustPriority(quarter, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2 + step * 2, rowYScreen, buttonWidth, buttonHeight,
                        plusHalfText(half),
                        btn -> adjustPriority(half, false),
                        this.font
                ));
            }
            case LIMIT -> {
                quarter = Math.max(1, maxLimit / 4);
                half    = Math.max(1, maxLimit / 2);
                placeholder = Component.translatable(KEY_BASE + "label.transfer");

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen, rowYScreen, buttonWidth, buttonHeight,
                        minusHalfText(half),
                        btn -> adjustLimit(-half, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen + step, rowYScreen, buttonWidth, buttonHeight,
                        minusQuarterText(quarter),
                        btn -> adjustLimit(-quarter, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        rowXScreen + step * 2, rowYScreen, buttonWidth, buttonHeight,
                        minusSingleText(),
                        btn -> adjustLimit(-1, false),
                        this.font
                ));

                limitField = new EditBox(this.font, fieldXScreen, fieldYScreen, fieldWidth, fieldHeight, placeholder);
                limitField.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
                this.addRenderableWidget(limitField);

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2, rowYScreen, buttonWidth, buttonHeight,
                        plusSingleText(),
                        btn -> adjustLimit(1, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2 + step, rowYScreen, buttonWidth, buttonHeight,
                        plusQuarterText(quarter),
                        btn -> adjustLimit(quarter, false),
                        this.font
                ));

                this.addRenderableWidget(new ScaledTextButton(
                        fieldXScreen + fieldWidth + 2 + step * 2, rowYScreen, buttonWidth, buttonHeight,
                        plusHalfText(half),
                        btn -> adjustLimit(half, false),
                        this.font
                ));
            }
        }
    }

    private void createRowBoth(RowKind kind, int leftX, int rightX) {
        int rowYScreen = topPos + rowY(kind);
        int buttonWidth = 13, buttonHeight = 12, step = buttonWidth + 1;
        int fieldWidth = 32, fieldHeight = 10;
        int maxLimit = menu.pipeBE.getTierMaxTransfer();

        int quarter, half;
        Component placeholder;

        switch (kind) {
            case CHANNEL -> {
                quarter = MAX_CHANNEL / 4;
                half    = MAX_CHANNEL / 2;
                placeholder = Component.translatable(KEY_BASE + "label.channel");
            }
            case PRIORITY -> {
                quarter = MAX_PRIORITY / 4;
                half    = MAX_PRIORITY / 2;
                placeholder = Component.translatable(KEY_BASE + "label.priority");
            }
            case LIMIT -> {
                quarter = Math.max(1, maxLimit / 4);
                half    = Math.max(1, maxLimit / 2);
                placeholder = Component.translatable(KEY_BASE + "label.transfer");
            }
            default -> throw new IllegalStateException();
        }

        // LEFT (extract)
        int fieldXLeft = leftX + step * 3;
        int fieldYLeft = rowYScreen + 1;

        this.addRenderableWidget(new ScaledTextButton(
                leftX, rowYScreen, buttonWidth, buttonHeight,
                minusHalfText(half),
                btn -> applyRowDelta(kind, -half, false),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                leftX + step, rowYScreen, buttonWidth, buttonHeight,
                minusQuarterText(quarter),
                btn -> applyRowDelta(kind, -quarter, false),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                leftX + step * 2, rowYScreen, buttonWidth, buttonHeight,
                minusSingleText(),
                btn -> applyRowDelta(kind, -1, false),
                this.font
        ));

        EditBox leftField = new EditBox(this.font, fieldXLeft, fieldYLeft, fieldWidth, fieldHeight, placeholder);
        switch (kind) {
            case CHANNEL -> {
                channelField = leftField;
                channelField.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
            }
            case PRIORITY -> {
                priorityField = leftField;
                priorityField.setFilter(s -> s.isEmpty() || s.equals("-") ||
                        s.chars().allMatch(ch -> Character.isDigit(ch) || ch == '-'));
            }
            case LIMIT -> {
                limitField = leftField;
                limitField.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
            }
        }
        this.addRenderableWidget(leftField);

        this.addRenderableWidget(new ScaledTextButton(
                fieldXLeft + fieldWidth + 2, rowYScreen, buttonWidth, buttonHeight,
                plusSingleText(),
                btn -> applyRowDelta(kind, 1, false),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                fieldXLeft + fieldWidth + 2 + step, rowYScreen, buttonWidth, buttonHeight,
                plusQuarterText(quarter),
                btn -> applyRowDelta(kind, quarter, false),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                fieldXLeft + fieldWidth + 2 + step * 2, rowYScreen, buttonWidth, buttonHeight,
                plusHalfText(half),
                btn -> applyRowDelta(kind, half, false),
                this.font
        ));

        // RIGHT (insert)
        int fieldXRight = rightX + step * 3;
        int fieldYRight = rowYScreen + 1;

        this.addRenderableWidget(new ScaledTextButton(
                rightX, rowYScreen, buttonWidth, buttonHeight,
                minusHalfText(half),
                btn -> applyRowDelta(kind, -half, true),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                rightX + step, rowYScreen, buttonWidth, buttonHeight,
                minusQuarterText(quarter),
                btn -> applyRowDelta(kind, -quarter, true),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                rightX + step * 2, rowYScreen, buttonWidth, buttonHeight,
                minusSingleText(),
                btn -> applyRowDelta(kind, -1, true),
                this.font
        ));

        EditBox rightField = new EditBox(this.font, fieldXRight, fieldYRight, fieldWidth, fieldHeight, placeholder);
        switch (kind) {
            case CHANNEL -> {
                outChannelField = rightField;
                outChannelField.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
            }
            case PRIORITY -> {
                outPriorityField = rightField;
                outPriorityField.setFilter(s -> s.isEmpty() || s.equals("-") ||
                        s.chars().allMatch(ch -> Character.isDigit(ch) || ch == '-'));
            }
            case LIMIT -> {
                outLimitField = rightField;
                outLimitField.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
            }
        }
        this.addRenderableWidget(rightField);

        this.addRenderableWidget(new ScaledTextButton(
                fieldXRight + fieldWidth + 2, rowYScreen, buttonWidth, buttonHeight,
                plusSingleText(),
                btn -> applyRowDelta(kind, 1, true),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                fieldXRight + fieldWidth + 2 + step, rowYScreen, buttonWidth, buttonHeight,
                plusQuarterText(quarter),
                btn -> applyRowDelta(kind, quarter, true),
                this.font
        ));

        this.addRenderableWidget(new ScaledTextButton(
                fieldXRight + fieldWidth + 2 + step * 2, rowYScreen, buttonWidth, buttonHeight,
                plusHalfText(half),
                btn -> applyRowDelta(kind, half, true),
                this.font
        ));
    }

    private void applyRowDelta(RowKind kind, int delta, boolean rightColumn) {
        switch (kind) {
            case CHANNEL  -> adjustChannel(delta, rightColumn);
            case PRIORITY -> adjustPriority(delta, rightColumn);
            case LIMIT    -> adjustLimit(delta, rightColumn);
        }
    }

    /* ----------------- Config helpers ----------------- */

    private PipeSideConfig getDisplayConfig(Direction side) {
        PipeSideConfig cfg = clientConfigs.get(side);
        if (cfg == null) {
            cfg = new PipeSideConfig();
            clientConfigs.put(side, cfg);
        }
        return cfg;
    }

    private boolean isInsertSide(PipeSideConfig cfg, boolean rightColumn) {
        return bothLayout ? rightColumn : (cfg.ioMode == IOMode.INSERT);
    }

    private void adjustChannel(int delta, boolean rightColumn) {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        boolean insert = isInsertSide(cfg, rightColumn);
        int value = insert ? cfg.insertChannel : cfg.extractChannel;
        value = clampChannel(value + delta);
        if (insert) cfg.insertChannel = value; else cfg.extractChannel = value;
        applyConfigToFields();

        sendConfigToServer(false);
    }

    private void adjustPriority(int delta, boolean rightColumn) {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        boolean insert = isInsertSide(cfg, rightColumn);
        int value = insert ? cfg.insertPriority : cfg.extractPriority;
        value = clampPriority(value + delta);
        if (insert) cfg.insertPriority = value; else cfg.extractPriority = value;
        applyConfigToFields();

        sendConfigToServer(false);
    }

    private void adjustLimit(int delta, boolean rightColumn) {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        int max = menu.pipeBE.getTierMaxTransfer();
        boolean insert = isInsertSide(cfg, rightColumn);
        int value = insert ? cfg.insertTransferLimit : cfg.extractTransferLimit;
        value = clampLimit(value + delta, max);
        if (insert) cfg.insertTransferLimit = value; else cfg.extractTransferLimit = value;
        applyConfigToFields();

        sendConfigToServer(false);
    }

    private int clampChannel(int v)  { return v < 0 ? 0 : Math.min(v, MAX_CHANNEL); }
    private int clampPriority(int v) { return Math.max(-MAX_PRIORITY, Math.min(v, MAX_PRIORITY)); }
    private int clampLimit(int v, int max) { return v <= 0 ? 0 : Math.min(v, max); }

    private void applyConfigToFields() {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        int max = menu.pipeBE.getTierMaxTransfer();

        if (bothLayout) {
            int inChannel   = clampChannel(cfg.extractChannel);
            int inPriority  = clampPriority(cfg.extractPriority);
            int inLimit     = clampLimit(cfg.extractTransferLimit, max);
            int outChannel  = clampChannel(cfg.insertChannel);
            int outPriority = clampPriority(cfg.insertPriority);
            int outLimit    = clampLimit(cfg.insertTransferLimit, max);

            if (channelField != null)      channelField.setValue(Integer.toString(inChannel));
            if (priorityField != null)     priorityField.setValue(Integer.toString(inPriority));
            if (limitField != null)        limitField.setValue(Integer.toString(inLimit));
            if (outChannelField != null)   outChannelField.setValue(Integer.toString(outChannel));
            if (outPriorityField != null)  outPriorityField.setValue(Integer.toString(outPriority));
            if (outLimitField != null)     outLimitField.setValue(Integer.toString(outLimit));
        } else {
            boolean insert = (cfg.ioMode == IOMode.INSERT);
            int channel  = clampChannel(insert ? cfg.insertChannel : cfg.extractChannel);
            int priority = clampPriority(insert ? cfg.insertPriority : cfg.extractPriority);
            int limit    = clampLimit(insert ? cfg.insertTransferLimit : cfg.extractTransferLimit, max);

            if (channelField != null)  channelField.setValue(Integer.toString(channel));
            if (priorityField != null) priorityField.setValue(Integer.toString(priority));
            if (limitField != null)    limitField.setValue(Integer.toString(limit));
        }
    }

    private void applyFieldsToConfig() {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        int max = menu.pipeBE.getTierMaxTransfer();

        if (bothLayout) {
            int inChannel   = cfg.extractChannel;
            int inPriority  = cfg.extractPriority;
            int inLimit     = cfg.extractTransferLimit;
            int outChannel  = cfg.insertChannel;
            int outPriority = cfg.insertPriority;
            int outLimit    = cfg.insertTransferLimit;

            if (channelField != null)     inChannel   = parseIntSafe(channelField.getValue(), inChannel);
            if (priorityField != null)    inPriority  = parseIntSafe(priorityField.getValue(), inPriority);
            if (limitField != null)       inLimit     = parseIntSafe(limitField.getValue(), inLimit);
            if (outChannelField != null)  outChannel  = parseIntSafe(outChannelField.getValue(), outChannel);
            if (outPriorityField != null) outPriority = parseIntSafe(outPriorityField.getValue(), outPriority);
            if (outLimitField != null)    outLimit    = parseIntSafe(outLimitField.getValue(), outLimit);

            cfg.extractChannel       = clampChannel(inChannel);
            cfg.extractPriority      = clampPriority(inPriority);
            cfg.extractTransferLimit = clampLimit(inLimit, max);
            cfg.insertChannel        = clampChannel(outChannel);
            cfg.insertPriority       = clampPriority(outPriority);
            cfg.insertTransferLimit  = clampLimit(outLimit, max);
        } else {
            boolean insert = (cfg.ioMode == IOMode.INSERT);
            int channelVal  = insert ? cfg.insertChannel       : cfg.extractChannel;
            int priorityVal = insert ? cfg.insertPriority      : cfg.extractPriority;
            int limitVal    = insert ? cfg.insertTransferLimit : cfg.extractTransferLimit;

            if (channelField != null)  channelVal  = parseIntSafe(channelField.getValue(), channelVal);
            if (priorityField != null) priorityVal = parseIntSafe(priorityField.getValue(), priorityVal);
            if (limitField != null)    limitVal    = parseIntSafe(limitField.getValue(), limitVal);

            channelVal  = clampChannel(channelVal);
            priorityVal = clampPriority(priorityVal);
            limitVal    = clampLimit(limitVal, max);

            if (insert) {
                cfg.insertChannel       = channelVal;
                cfg.insertPriority      = priorityVal;
                cfg.insertTransferLimit = limitVal;
            } else {
                cfg.extractChannel       = channelVal;
                cfg.extractPriority      = priorityVal;
                cfg.extractTransferLimit = limitVal;
            }
        }

        applyConfigToFields();
    }

    private int parseIntSafe(String s, int fallback) {
        if (s == null || s.isEmpty() || s.equals("-")) return fallback;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return fallback; }
    }

    /* ----------------- Networking glue ----------------- */

    private void sendConfigToServer(boolean includeFieldValues) {
        if (includeFieldValues) applyFieldsToConfig();
        PipeSideConfig cfg = getDisplayConfig(selectedSide);

        MechaniCraftNetwork.sendToServer(new UpdatePipeSideConfigPacket(
                menu.pipeBE.getBlockPos(),
                selectedSide,
                cfg.ioMode,
                cfg.logicMode,
                cfg.redstoneMode, // now globalized on the server
                cfg.extractChannel,
                cfg.extractPriority,
                cfg.extractTransferLimit,
                cfg.insertChannel,
                cfg.insertPriority,
                cfg.insertTransferLimit,
                cfg.extractFilterMode,
                cfg.insertFilterMode
        ));
    }

    /* ----------------- Modes / side selection ----------------- */

    private void addSideButton(Direction side, int x, int y, int size) {
        Button button = new ScaledTextButton(
                x, y, size, size,
                Component.literal(side.getName().substring(0, 1).toUpperCase()),
                btn -> {
                    selectedSide = side;
                    updateModeButtonLabels();
                    applyConfigToFields();
                },
                this.font
        );
        sideButtons.put(side, button);
        this.addRenderableWidget(button);
    }

    private void cycleIOMode() {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        cfg.ioMode = switch (cfg.ioMode) {
            case DISABLED -> IOMode.EXTRACT;
            case EXTRACT  -> IOMode.INSERT;
            case INSERT   -> IOMode.BOTH;
            case BOTH     -> IOMode.DISABLED;
        };
    }

    private void cycleLogicMode() {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        cfg.logicMode = switch (cfg.logicMode) {
            case NEAREST_FIRST  -> PipeLogicMode.FURTHEST_FIRST;
            case FURTHEST_FIRST -> PipeLogicMode.ROUND_ROBIN;
            case ROUND_ROBIN    -> PipeLogicMode.NEAREST_FIRST;
        };
        updateModeButtonLabels();
    }

    /**
     * NEW: redstone mode is global for the entire pipe.
     * We compute the next mode from the selected side,
     * then apply it to ALL clientConfigs directions.
     */
    private void cycleRedstoneMode() {
        // Base from the currently-selected side
        PipeSideConfig currentCfg = getDisplayConfig(selectedSide);
        RedstoneMode current = currentCfg.redstoneMode;

        RedstoneMode next = switch (current) {
            case IGNORED -> RedstoneMode.HIGH;
            case HIGH    -> RedstoneMode.LOW;
            case LOW     -> RedstoneMode.OFF;
            case OFF     -> RedstoneMode.IGNORED;
        };

        // Apply globally on the client so all faces show the same RS state
        for (Direction dir : Direction.values()) {
            PipeSideConfig cfg = getDisplayConfig(dir);
            cfg.redstoneMode = next;
        }

        updateModeButtonLabels();
    }

    private void updateModeButtonLabels() {
        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        if (ioModeButton != null)       ioModeButton.setMessage(ioButtonText(cfg.ioMode));
        if (logicModeButton != null)    logicModeButton.setMessage(logicButtonText(cfg.logicMode));
        if (redstoneModeButton != null) redstoneModeButton.setMessage(redstoneButtonText(cfg.redstoneMode));
    }

    private void openFilterForSelectedSide(boolean insertSide) {
        // Apply + sync any pending field edits before leaving to filter screen
        sendConfigToServer(true);

        BlockPos pos = menu.pipeBE.getBlockPos();
        MechaniCraftNetwork.sendToServer(new OpenPipeFilterPacket(
                pos,
                (byte) selectedSide.get3DDataValue()
        ));
    }

    /* ----------------- Rendering ----------------- */

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        ResourceLocation tex = bothLayout ? GUI_TEXTURE_WIDE : GUI_TEXTURE_NARROW;
        RenderSystem.setShaderTexture(0, tex);
        guiGraphics.blit(tex, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int titleWidth = this.font.width(this.title);
        int titleX = (this.imageWidth - titleWidth) / 2;
        guiGraphics.drawString(this.font, this.title, titleX, 6, 0x404040, false);

        PipeSideConfig cfg = getDisplayConfig(selectedSide);
        IOMode mode = cfg.ioMode;
        int labelColor = 0x202020;

        String neighborName = null;
        Level level = menu.pipeBE.getLevel();
        if (level != null) {
            BlockPos neighborPos = menu.pipeBE.getBlockPos().relative(selectedSide);
            if (level.isLoaded(neighborPos)) {
                BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                if (neighborBE != null) {
                    boolean hasCap = switch (menu.pipeBE.getPipeType()) {
                        case ENERGY -> neighborBE.getCapability(ForgeCapabilities.ENERGY, selectedSide.getOpposite()).isPresent();
                        case ITEM   -> neighborBE.getCapability(ForgeCapabilities.ITEM_HANDLER, selectedSide.getOpposite()).isPresent();
                        case FLUID  -> neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, selectedSide.getOpposite()).isPresent();
                        default     -> false;
                    };
                    if (hasCap) {
                        BlockState neighborState = level.getBlockState(neighborPos);
                        neighborName = neighborState.getBlock().getName().getString();
                    }
                }
            }
        }

        String selectedText = selectedPrefix().getString() + " " + selectedSide.getName();
        if (neighborName != null && !neighborName.isEmpty()) selectedText += " - " + neighborName;

        int maxSelectedWidth = this.imageWidth - 16;
        int selectedWidth = this.font.width(selectedText);
        float scale = selectedWidth > maxSelectedWidth
                ? (float) maxSelectedWidth / (float) selectedWidth
                : 1.0F;

        float scaledWidth = selectedWidth * scale;
        float selectedX = (this.imageWidth - scaledWidth) / 2.0F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(selectedX, 18, 0);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(this.font, Component.literal(selectedText), 0, 0, labelColor, false);
        guiGraphics.pose().popPose();

        if (mode != IOMode.DISABLED) {
            int channelLabelY  = channelRowY - 8;
            int priorityLabelY = priorityRowY - 8;
            int limitLabelY    = limitRowY - 8;
            Component channelLabel  = Component.translatable(KEY_BASE + "label.channel");
            Component priorityLabel = Component.translatable(KEY_BASE + "label.priority");
            Component transferLabel = Component.translatable(KEY_BASE + "label.transfer");

            if (!bothLayout) {
                drawCenteredLabelOverField(guiGraphics, channelLabel,  channelField, channelLabelY, labelColor);
                drawCenteredLabelOverField(guiGraphics, priorityLabel, priorityField, priorityLabelY, labelColor);
                drawCenteredLabelOverField(guiGraphics, transferLabel, limitField,    limitLabelY,    labelColor);
            } else {
                drawCenteredLabelOverField(guiGraphics, channelLabel,  channelField,     channelLabelY, labelColor);
                drawCenteredLabelOverField(guiGraphics, channelLabel,  outChannelField,  channelLabelY, labelColor);
                drawCenteredLabelOverField(guiGraphics, priorityLabel, priorityField,    priorityLabelY, labelColor);
                drawCenteredLabelOverField(guiGraphics, priorityLabel, outPriorityField, priorityLabelY, labelColor);
                drawCenteredLabelOverField(guiGraphics, transferLabel, limitField,       limitLabelY,    labelColor);
                drawCenteredLabelOverField(guiGraphics, transferLabel, outLimitField,    limitLabelY,    labelColor);
            }
        }

        for (Map.Entry<Direction, Button> entry : sideButtons.entrySet()) {
            Direction side = entry.getKey();
            Button button  = entry.getValue();
            boolean connected = menu.pipeBE.isSideNetworkOpen(side) && menu.pipeBE.canConnectToBlock(side);
            int color = (side == selectedSide)
                    ? 0x66FFFF00
                    : (connected ? 0x6600FF00 : 0x66FF0000);
            int bx = button.getX() - leftPos;
            int by = button.getY() - topPos;
            guiGraphics.fill(bx - 1, by - 1, bx + button.getWidth() + 1, by + button.getHeight() + 1, color);
        }

        if (bothLayout) {
            // Extract / Insert headers above the filter row
            int filterLocalY = filterRowY;
            int labelY = filterLocalY - 8;
            int leftColumnLocalX  = 8;
            int rightColumnLocalX = this.imageWidth - 8 - BOTH_COLUMN_WIDTH;
            int leftCenterX  = leftColumnLocalX  + BOTH_COLUMN_WIDTH / 2;
            int rightCenterX = rightColumnLocalX + BOTH_COLUMN_WIDTH / 2;

            Component extractLabel = Component.translatable(KEY_BASE + "io.extract");
            int extractW = this.font.width(extractLabel);
            int extractX = leftCenterX - extractW / 2;
            guiGraphics.drawString(this.font, extractLabel, extractX, labelY, 0x404040, false);

            Component insertLabel = Component.translatable(KEY_BASE + "io.insert");
            int insertW = this.font.width(insertLabel);
            int insertX = rightCenterX - insertW / 2;
            guiGraphics.drawString(this.font, insertLabel, insertX, labelY, 0x404040, false);
        }
    }

    private void drawCenteredLabelOverField(GuiGraphics guiGraphics, Component text, EditBox field, int labelY, int color) {
        if (field == null) return;
        int fieldLocalX = field.getX() - leftPos;
        int centerX = fieldLocalX + field.getWidth() / 2;
        int textWidth = this.font.width(text);
        int labelX = centerX - textWidth / 2;
        guiGraphics.drawString(this.font, text, labelX, labelY, color, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /* ----------------- Keyboard / mouse (field syncing) ----------------- */

    private boolean keyForFields(int keyCode, int scanCode, int modifiers, EditBox... fields) {
        for (EditBox f : fields)
            if (f != null && f.keyPressed(keyCode, scanCode, modifiers)) return true;
        return false;
    }

    private boolean charForFields(char codePoint, int modifiers, EditBox... fields) {
        for (EditBox f : fields)
            if (f != null && f.charTyped(codePoint, modifiers)) return true;
        return false;
    }

    private boolean[] getFieldFocusStates() {
        return new boolean[]{
                channelField != null && channelField.isFocused(),
                priorityField != null && priorityField.isFocused(),
                limitField != null && limitField.isFocused(),
                outChannelField != null && outChannelField.isFocused(),
                outPriorityField != null && outPriorityField.isFocused(),
                outLimitField != null && outLimitField.isFocused()
        };
    }

    private boolean lostFocus(boolean[] before, boolean[] after) {
        int len = Math.min(before.length, after.length);
        for (int i = 0; i < len; i++) {
            if (before[i] && !after[i]) return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean[] before = getFieldFocusStates();
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        boolean[] after = getFieldFocusStates();

        if (lostFocus(before, after)) {
            applyFieldsToConfig();
            sendConfigToServer(false);
        }

        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyForFields(keyCode, scanCode, modifiers,
                channelField, priorityField, limitField,
                outChannelField, outPriorityField, outLimitField))
            return true;

        if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
            applyFieldsToConfig();
            sendConfigToServer(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (charForFields(codePoint, modifiers,
                channelField, priorityField, limitField,
                outChannelField, outPriorityField, outLimitField))
            return true;
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        // Apply any pending text edits and sync once more when closing
        sendConfigToServer(true);
        super.onClose();
    }
}