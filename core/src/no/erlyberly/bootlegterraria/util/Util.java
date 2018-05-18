package no.erlyberly.bootlegterraria.util;

import com.badlogic.gdx.graphics.Color;

public class Util {

    /**
     * Based on https://gist.github.com/steen919/8a079f4dadf88d4197bb/d732449eb74321207b4b189a3bcbf47a83c5db65
     * Converts the given hex color in 0xAARRGGBB format to a {@link Color} that can be used in a LibGdx application
     */
    public static Color convert(String str) {
        long hex = Long.decode(str);
        float a = (hex & 0xFF000000L) >> 24;
        float r = (hex & 0x00FF0000L) >> 16;
        float g = (hex & 0x0000FF00L) >> 8;
        float b = (hex & 0x000000FFL);
        return new Color(r / 255F, g / 255F, b / 255F, a / 255F);
    }
}
