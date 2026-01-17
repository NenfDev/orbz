package dev.nenf.orbz.data;

public enum BoosterType {
    EXP,
    SELL,
    SHARDS,
    BITS;

    public static BoosterType parse(String s) {
        if (s == null) return null;
        try {
            return BoosterType.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
