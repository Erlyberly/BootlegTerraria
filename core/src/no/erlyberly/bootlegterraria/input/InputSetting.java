package no.erlyberly.bootlegterraria.input;

import static com.badlogic.gdx.Input.Buttons;
import static com.badlogic.gdx.Input.Keys;

public enum InputSetting {

    MOVE_LEFT(Keys.RIGHT),
    MOVE_RIGHT(Keys.LEFT),
    JUMP(Keys.UP),
    ATTACK(Keys.E),
    ROLL(Keys.SHIFT_LEFT),
    BREAK_BLOCK(MouseKeys.LEFT_MOUSE_BUTTON),
    PLACE_BLOCK(MouseKeys.RIGHT_MOUSE_BUTTON)
    //
    ;

    public class MouseKeys {

        public static final int LEFT_MOUSE_BUTTON = -Buttons.LEFT;
        public static final int RIGHT_MOUSE_BUTTON = -Buttons.RIGHT;
        public static final int MIDDLE_MOUSE_BUTTON = -Buttons.MIDDLE;
        public static final int FORWARD_MOUSE_BUTTON = -Buttons.FORWARD;
        public static final int BACKWARD_MOUSE_BUTTON = -Buttons.BACK;
    }

    private int[] keys;

    InputSetting(int... defaultKey) {
        this.keys = defaultKey;
    }

    public void setKeys(int... newKeys) {
        this.keys = newKeys;
    }

    public int[] getKeys() {
        return this.keys;
    }

    @Override
    public String toString() {
        return name().replace("_", " ").toLowerCase();
    }
}
