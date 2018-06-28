package no.erlyberly.bootlegterraria.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.input.event.EventRunnable;
import no.erlyberly.bootlegterraria.input.event.EventType;
import no.erlyberly.bootlegterraria.input.event.metadata.*;

import java.util.*;

/**
 * @author kheba
 */
public class InputHandler implements InputProcessor {

    private static final boolean DEBUG_INPUT = true;

    //holds all actions for keyboard/mouse
    private final Map<EventType, Map<Set<Integer>, EventRunnable>> actionMap;

    //hold all keys that are currently pressed
    private final Set<Integer> pressed;

    private final Map<Set<Integer>, EventRunnable> activeKeysPressed;

    private final static GenericMetadata DEFAULT_METADATA = new GenericMetadata();

    public InputHandler() {
        this.pressed = new HashSet<>();
        this.activeKeysPressed = new HashMap<>();
        this.actionMap = new EnumMap<>(EventType.class);
        for (final EventType eventType : EventType.values()) {
            this.actionMap.put(eventType, new HashMap<>());
        }

        Gdx.input.setInputProcessor(this);
    }

    public void registerListener(final EventRunnable action, final EventType eventType, final InputSetting setting) {
        Preconditions.checkNotNull(setting);
        registerListener(action, eventType, setting.getKeys());
    }

    /**
     * Register a listener that will run when the given key with(out) modifiers is set. If another action is registered
     * to the same key combination it will be overwritten.
     * <p>
     * If no keys are specified then {@code action} will be triggered on all events
     *
     * @param action
     *     What to do
     * @param keys
     *     The keys needed to be pressed to activate the event
     */
    public void registerListener(final EventRunnable action, final EventType eventType, final Integer... keys) {
        Preconditions.checkNotNull(action);
        Preconditions.checkNotNull(eventType);
        final Map<Set<Integer>, EventRunnable> eventMap = this.actionMap.get(eventType);
        final Set<Integer> setKeys = ImmutableSet.copyOf(keys);

        //give a warning to make it easier to track down these kind of bugs
        if (eventMap.containsKey(setKeys)) {
            final StringBuilder keyName = new StringBuilder("[ ");

            setKeys.forEach(keycode -> {
                final String name;
                switch (keycode) {
                    case 0:
                        name = "LEFT"; //Input.Buttons.LEFT
                        break;
                    case 1:
                        name = "RIGHT"; //Input.Buttons.LEFT
                        break;
                    case 2:
                        name = "MIDDLE"; //Input.Buttons.MIDDLE
                        break;
                    case 3:
                        name = "BACK"; //Input.Buttons.BACK
                        break;
                    case 4:
                        name = "FORWARD"; //Input.Buttons.FORWARD
                        break;
                    default:
                        name = Input.Keys.toString(keycode);
                }

                keyName.append(name);
                keyName.append("(").append(keycode).append(") ");
            });

            keyName.append("]");
            GameMain.consHldr()
                    .logf("Multiple actions are mapped to the same eventType(%s) and keys(%s)", LogLevel.ERROR,
                          eventType.name(), keyName.toString());
        }
        eventMap.put(setKeys, action);

    }

    /**
     * Fire an event of the given type with some metadata
     *
     * @param eventType
     *     The type of event to fire
     * @param eventMetadata
     *     The metadata of the event
     */
    public void fireEvent(final EventType eventType, final EventMetadata eventMetadata) {
        Preconditions.checkNotNull(eventType, "The event type to fire cannot be null");
        Preconditions.checkNotNull(eventMetadata, "Metadata must exist");
        Preconditions.checkArgument(eventType.isCorrectMetadata(eventMetadata),
                                    "Wrong metadata class given for the event, expected " +
                                    eventType.getMetadataName() + " but got " +
                                    eventMetadata.getClass().getSimpleName());

        //run all runnables if the key combination is held
        this.actionMap.get(eventType).forEach((keys, runnable) -> {
            if (this.pressed.containsAll(keys)) { runnable.run(eventMetadata); }
        });
    }

    public void update() {
        this.activeKeysPressed.values().forEach(runnable -> runnable.run(DEFAULT_METADATA));
    }

    @Override
    public boolean scrolled(final int amount) {
        fireEvent(EventType.SCROLLED, new ScrolledMetadata(amount));
        return false;
    }

    @Override
    public boolean keyDown(final int keycode) {
        this.pressed.add(keycode);
        fireEvent(EventType.KEY_DOWN, new KeyMetadata(keycode));

        this.actionMap.get(EventType.KEY_PRESSED).forEach((keys, runnable) -> {
            if (this.pressed.containsAll(keys)) { this.activeKeysPressed.put(keys, runnable); }
        });

        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        this.pressed.remove(keycode);
        fireEvent(EventType.KEY_UP, new KeyMetadata(keycode));

        this.activeKeysPressed.entrySet().removeIf(entry -> !this.pressed.containsAll(entry.getKey()));
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {
        fireEvent(EventType.KEY_TYPED, new TypedMetadata(character));
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        this.pressed.add(button);
        final MouseMetadata mm = new MouseMetadata(screenX, screenY);
        fireEvent(EventType.TOUCH_DOWN, mm);

        this.actionMap.get(EventType.KEY_PRESSED).forEach((keys, runnable) -> {
            if (this.pressed.containsAll(keys)) {
                this.activeKeysPressed.put(keys, runnable);
            }
        });
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        this.pressed.remove(button);
        fireEvent(EventType.TOUCH_UP, new MouseMetadata(screenX, screenY));
        this.activeKeysPressed.entrySet().removeIf(entry -> !this.pressed.containsAll(entry.getKey()));
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        fireEvent(EventType.TOUCH_DRAGGED, new MouseMetadata(screenX, screenY));
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        fireEvent(EventType.MOUSE_MOVED, new MouseMetadata(screenX, screenY));
        return false;
    }
}





