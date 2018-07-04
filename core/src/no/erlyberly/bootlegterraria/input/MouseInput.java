package no.erlyberly.bootlegterraria.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ObjectIntMap;

/**
 * @author kheba
 */
@SuppressWarnings("WeakerAccess")
public class MouseInput {

    public static final int LEFT_MB = 256;
    public static final int RIGHT_MB = 257;
    public static final int MIDDLE_MB = 258;
    public static final int BACK_MB = 259;
    public static final int FORWARD_MB = 260;

    private static final ObjectIntMap<String> buttonNames;

    static {
        buttonNames = new ObjectIntMap<>(8);
        for (int i = LEFT_MB; i <= FORWARD_MB; i++) {
            final String name = toString(i);
            if (name != null) { buttonNames.put(name, i); }
        }
    }

    /**
     * Convert a keycode from {@link com.badlogic.gdx.Input.Buttons} to this
     */
    public static int fromGdxButton(final int keycode) {
        switch (keycode) {
            case Input.Buttons.LEFT:
                return LEFT_MB;
            case Input.Buttons.RIGHT:
                return RIGHT_MB;
            case Input.Buttons.MIDDLE:
                return MIDDLE_MB;
            case Input.Buttons.BACK:
                return BACK_MB;
            case Input.Buttons.FORWARD:
                return FORWARD_MB;
            default:
                throw new IllegalArgumentException(
                    "Given keycode does not match any in Input.Buttons, keycode: " + keycode);
        }
    }

    public static String toString(final int keycode) {
        if (keycode < 256) {
            throw new IllegalArgumentException("keycode cannot be less than 255, keycode: " + keycode);
        }
        if (keycode > 260) {
            throw new IllegalArgumentException("keycode cannot be greater than 260, keycode: " + keycode);
        }
        switch (keycode) {
            case 256:
                return "LMB";
            case 257:
                return "RMB";
            case 258:
                return "MMB";
            case 259:
                return "BMB";
            case 260:
                return "FMB";
            default:
                //key not found
                return null;
        }
    }

    public static int valueOf(final String button) {
        return buttonNames.get(button, -1);
    }
}
