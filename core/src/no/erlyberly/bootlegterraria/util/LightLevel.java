package no.erlyberly.bootlegterraria.util;

public enum LightLevel {
    LVL_7(7),
    LVL_6(6),
    LVL_5(5),
    LVL_4(4),
    LVL_3(3),
    LVL_2(2),
    LVL_1(1),
    LVL_0(0);

    public static final int LIGHT_LEVELS = 8;

    final int lvl;
    final float percentage;

    LightLevel(int lvl) {
        this.lvl = lvl;
        this.percentage = (lvl / ((float) LIGHT_LEVELS - 1));
    }

    public int getLvl() {
        return this.lvl;
    }

    public float getPercentage() {
        return this.percentage;
    }

    /**
     * Safely get the light level from an integer, if the {@code lvl} is less than 0, {@code LVL_0} is returned. If
     * {@code lvl} is greater than or equal to {@link #LIGHT_LEVELS} {@code LVL_7} is returned
     */
    public static LightLevel valueOf(int lvl) {
        if (lvl < 0) { return LVL_0; }
        else if (lvl >= LIGHT_LEVELS) { return LVL_7; }
        return values()[7 - lvl];
    }

    public LightLevel dimmer() {
        return valueOf(this.lvl - 1);
    }

    public LightLevel brighter() {
        return valueOf(this.lvl + 1);
    }

    @Override
    public String toString() {
        return name() + "(" + this.percentage + ")";
    }
}
