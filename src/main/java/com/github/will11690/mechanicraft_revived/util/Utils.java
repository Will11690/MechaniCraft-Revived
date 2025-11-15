package com.github.will11690.mechanicraft_revived.util;

public class Utils {

    /* TEXT COMPONENT SUFFIXES */

    public static String withSuffixEnergy(int energy) {

        if (energy < 1000) return energy + "";

        int exp = (int) (Math.log(energy) / Math.log(1000));

        return String.format("%.1f%c", energy / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public static String withSuffixTime(int ticks) {

        if (ticks < 20) return "0." + ticks + "s";

        int exp = (int) (Math.log(ticks) / Math.log(20));

        return String.format("%.1f%c", ticks / Math.pow(20, exp), "smhDWY".charAt(exp - 1));
    }
}