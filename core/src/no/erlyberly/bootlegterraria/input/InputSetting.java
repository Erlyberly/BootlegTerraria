package no.erlyberly.bootlegterraria.input;

import static com.badlogic.gdx.Input.Keys;

public enum InputSetting {

    MOVE_LEFT(Keys.LEFT),
    MOVE_RIGHT(Keys.RIGHT),
    JUMP(Keys.UP),
    //    FLY_UP(Keys.UP), same as jump
    FLY_DOWN(Keys.DOWN),
    ATTACK(Keys.E),
    DODGE(Keys.SHIFT_LEFT),
    BREAK_BLOCK(MouseInput.LEFT),
    PLACE_BLOCK(MouseInput.RIGHT)
    //
    ;

    private Integer[] keys;

    InputSetting(final Integer... defaultKeys) {
        this.keys = defaultKeys;
    }

    public void setKeys(final Integer... newKeys) {
        this.keys = newKeys;
    }

    public Integer[] getKeys() {
        return this.keys;
    }

    @Override
    public String toString() {
        return name().replace("_", " ").toLowerCase();
    }
}
