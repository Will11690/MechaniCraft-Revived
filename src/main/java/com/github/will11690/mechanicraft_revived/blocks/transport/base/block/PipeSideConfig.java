package com.github.will11690.mechanicraft_revived.blocks.transport.base.block;

import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import com.github.will11690.mechanicraft_revived.util.block.RedstoneMode;

public class PipeSideConfig {

    public IOMode ioMode = IOMode.BOTH;
    public RedstoneMode redstoneMode = RedstoneMode.IGNORED;
    public PipeLogicMode logicMode = PipeLogicMode.NEAREST_FIRST;

    // Per-direction channels
    public int extractChannel = 0;
    public int insertChannel  = 0;

    // Per-direction priorities
    public int extractPriority = 0;
    public int insertPriority  = 0;

    // Per-direction transfer limits (0 = disabled)
    public int extractTransferLimit = 0;
    public int insertTransferLimit  = 0;

    // NEW: per-direction filter modes
    // BLACKLIST: empty = allow all; entries = block matches
    // WHITELIST: empty = block all; entries = allow matches
    public FilterMode extractFilterMode = FilterMode.BLACKLIST;
    public FilterMode insertFilterMode  = FilterMode.BLACKLIST;
}
