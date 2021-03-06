package no.erlyberly.bootlegterraria.render.light;

public enum LightLevel {
    LVL_0,
    LVL_1,
    LVL_2,
    LVL_3,
    LVL_4,
    LVL_5,
    LVL_6,
    LVL_7,
    LVL_8;

    public static final int LIGHT_LEVELS = 9;

    /**
     * Default light level for tiles to shine
     */
    public static final LightLevel DEFAULT_LIGHT_LEVEL = LightLevel.LVL_0;
    /**
     * The brightness of which the skylight will be shining
     */
    public static final LightLevel SKY_LIGHT = LightLevel.LVL_8;

    final int lvl;
    final float percentage;

    private static final LightLevel[] values;

    static {
        values = new LightLevel[values().length];
        for (final LightLevel ll : values()) {
            if (values[ll.getLvl()] != null) {
                throw new ExceptionInInitializerError(
                    "There are more than one LightLevel declared as the level '" + ll.getLvl() + "': " + ll + " and " +
                    values[ll.getLvl()]);
            }
            values[ll.getLvl()] = ll;
        }
        if (LIGHT_LEVELS != values.length) {
            throw new ExceptionInInitializerError(
                "Mismatch between LIGHT_LEVELS (" + LIGHT_LEVELS + ") and values.length (" + values.length + ")");
        }
    }

    LightLevel() {
        lvl = ordinal();
        percentage = (lvl / ((float) LIGHT_LEVELS - 1));
    }

    public int getLvl() {
        return lvl;
    }

    public float getPercentage() {
        return percentage;
    }

    public LightLevel dimmer() {
        return valueOf(lvl - 1);
    }

    public LightLevel brighter() {
        return valueOf(lvl + 1);
    }

    public static LightLevel valueOf(final float lvl) {
        return valueOf(Math.round(lvl));
    }

    public static LightLevel valueOf(final double lvl) {
        return valueOf((int) Math.round(lvl));
    }

    /**
     * Safely get the light level from an integer, if the {@code lvl} is less than 0, {@code LVL_0} is returned. If
     * {@code lvl} is greater than or equal to {@link #LIGHT_LEVELS} {@code LVL_7} is returned
     */
    public static LightLevel valueOf(final int lvl) {
        if (lvl <= 0) { return LVL_0; }
        if (lvl >= LIGHT_LEVELS) { return LVL_8; }
        return values[lvl];
    }

    /**
     * @return The brightest possible LightLevel
     */
    public static LightLevel max() {
        return LightLevel.valueOf(Integer.MAX_VALUE);
    }

    /**
     * @return The dimmest possible LightLevel
     */
    public static LightLevel min() {
        return LightLevel.valueOf(Integer.MIN_VALUE);
    }

    @Override
    public String toString() {
        return name() + "(" + percentage + ")";
    }


}

