package no.erlyberly.bootlegterraria.input;

import com.badlogic.gdx.Input;

/**
 * @author kheba
 */
@SuppressWarnings("WeakerAccess")
public class MouseInput {

    public static final int LEFT = 256;
    public static final int RIGHT = 257;
    public static final int MIDDLE = 258;
    public static final int BACK = 259;
    public static final int FORWARD = 260;

    /**
     * Convert a keycode from {@link com.badlogic.gdx.Input.Buttons} to this
     */
    public static int fromGdxButton(final int keycode) {
        switch (keycode) {
            case Input.Buttons.LEFT:
                return LEFT;
            case Input.Buttons.RIGHT:
                return RIGHT;
            case Input.Buttons.MIDDLE:
                return MIDDLE;
            case Input.Buttons.BACK:
                return BACK;
            case Input.Buttons.FORWARD:
                return FORWARD;
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
                return "Left";
            case 257:
                return "Right";
            case 258:
                return "Middle";
            case 259:
                return "Back";
            case 260:
                return "Forward";
            default:
                //key not found
                return null;
        }
    }


}
