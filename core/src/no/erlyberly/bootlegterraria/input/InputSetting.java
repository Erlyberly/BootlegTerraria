package no.erlyberly.bootlegterraria.input;

import com.badlogic.gdx.Gdx;
import no.erlyberly.bootlegterraria.input.event.EventType;
import no.erlyberly.bootlegterraria.util.Util;

import static com.badlogic.gdx.Input.Keys;

public enum InputSetting {

    MOVE_LEFT(EventType.KEY_PRESSED, Keys.A),
    MOVE_RIGHT(EventType.KEY_PRESSED, Keys.D),
    JUMP(EventType.KEY_PRESSED, Keys.W),
    //    FLY_UP(Keys.UP), same as jump
    FLY_DOWN(EventType.KEY_PRESSED, Keys.S),
    ATTACK(EventType.KEY_PRESSED, Keys.SPACE),
    DODGE(EventType.KEY_DOWN, Keys.C),
    BREAK_BLOCK(EventType.KEY_PRESSED, MouseInput.LEFT_MB),
    PLACE_BLOCK(EventType.KEY_PRESSED, MouseInput.RIGHT_MB),
    INVENTORY(EventType.KEY_DOWN, Keys.TAB)

    //
    ;

    private final EventType eventType;
    private Integer[] keys;

    InputSetting(final EventType eventType, final Integer... defaultKeys) {
        this.eventType = eventType;
        keys = defaultKeys;
    }

    public void setKeys(final Integer... newKeys) {
        keys = newKeys;
    }

    public Integer[] getKeys() {
        return keys;
    }

    public EventType getEventType() {
        return eventType;
    }

    /**
     * A replacement for {@code Gdx.input.isKeyPressed(int)}
     */
    public boolean isKeyPressed() {
        for (final int key : keys) {
            if (!Gdx.input.isKeyPressed(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * A replacement for {@code Gdx.input.isKeyJustPressed(int)}
     */
    public boolean isKeyJustPressed() {
        for (final int key : keys) {
            if (!Gdx.input.isKeyJustPressed(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return Util.toTitleCase(name().replace("_", " "));
    }
}
