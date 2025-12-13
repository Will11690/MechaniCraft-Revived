package com.github.will11690.mechanicraft_revived.util;

public class Utils {

    /* TEXT COMPONENT SUFFIXES */

    public static String withSuffixEnergy(int energy) {

        if (energy < 1000) return energy + "";

        int exp = (int) (Math.log(energy) / Math.log(1000));

        return String.format("%.1f%c", energy / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public static String withSuffixTime(int ticks) {
        if (ticks <= 0) return "0s";

        if (ticks < 20) {
            double seconds = ticks / 20.0;
            return String.format("%.2fs", seconds);
        }

        double seconds = ticks / 20.0;

        final double MINUTE = 60.0;
        final double HOUR = 60.0 * MINUTE;
        final double DAY = 24.0 * HOUR;
        final double WEEK = 7.0 * DAY;
        final double YEAR = 365.0 * DAY;

        double value;
        char suffix;

        if (seconds < MINUTE) {
            value = seconds;
            suffix = 's';
        } else if (seconds < HOUR) {
            value = seconds / MINUTE;
            suffix = 'm';
        } else if (seconds < DAY) {
            value = seconds / HOUR;
            suffix = 'h';
        } else if (seconds < WEEK) {
            value = seconds / DAY;
            suffix = 'D';
        } else if (seconds < YEAR) {
            value = seconds / WEEK;
            suffix = 'W';
        } else {
            value = seconds / YEAR;
            suffix = 'Y';
        }

        return String.format(value < 10 ? "%.1f%c" : "%.0f%c", value, suffix);
    }
}