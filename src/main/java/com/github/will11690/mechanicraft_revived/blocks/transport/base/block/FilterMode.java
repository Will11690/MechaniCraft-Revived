package com.github.will11690.mechanicraft_revived.blocks.transport.base.block;

public enum FilterMode {
    BLACKLIST,
    WHITELIST;

    public static FilterMode fromOrdinal(int ordinal) {
        FilterMode[] values = values();
        if (ordinal < 0 || ordinal >= values.length) return BLACKLIST;
        return values[ordinal];
    }
}