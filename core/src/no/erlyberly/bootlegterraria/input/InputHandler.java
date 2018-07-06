package no.erlyberly.bootlegterraria.input;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputProcessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.input.event.EventRunnable;
import no.erlyberly.bootlegterraria.input.event.EventType;
import no.erlyberly.bootlegterraria.input.event.metadata.*;
import no.erlyberly.bootlegterraria.util.Util;

import java.util.*;

/**
 * Handle input from keyboard and
 *
 * @author kheba
 */
public class InputHandler implements InputProcessor {

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

        GameMain.inputMultiplexer.addProcessor(this);
    }

    /**
     * @param eventType
     *     The event type to replace this of
     * @param oldKeys
     *     The old keys
     * @param newKeys
     *     The new keys
     */
    public boolean rebindListener(final EventType eventType, final Integer[] oldKeys, final Integer[] newKeys) {
        Preconditions.checkNotNull(eventType);
        final Map<Set<Integer>, EventRunnable> eventMap = this.actionMap.get(eventType);
        final EventRunnable er = eventMap.remove(ImmutableSet.copyOf(oldKeys));
        if (er != null) {
            if (eventMap.containsKey(ImmutableSet.copyOf(newKeys))) {
                GameMain.console
                    .logf("Multiple actions are mapped to the same eventType(%s) and keys(%s), no rebind done",
                          LogLevel.ERROR, eventType.name(), Util.keysToString(newKeys));
                return false;
            }
            eventMap.put(ImmutableSet.copyOf(newKeys), er);
        }
        else {
            GameMain.console.logf("Failed to re-bind listener, eventType: %s keys: %s", LogLevel.ERROR, eventType,
                                  Arrays.toString(oldKeys));
        }
        return true;
    }

    /**
     * Unregister a listener with the given arguments
     */
    public EventRunnable unregisterListener(final EventType eventType, final Integer... keys) {
        Preconditions.checkNotNull(eventType);
        final Map<Set<Integer>, EventRunnable> eventMap = this.actionMap.get(eventType);
        final Set<Integer> setKeys = ImmutableSet.copyOf(keys);

        //give a warning to make it easier to track down these kind of bugs
        if (!eventMap.containsKey(setKeys)) {

            GameMain.console
                .logf("Could not unregister events with the keys %s on th event %s, as there is nothing registered",
                      LogLevel.ERROR, Util.keysToString(keys), eventType.name());
        }
        else {
            return eventMap.remove(setKeys);
        }
        return null;
    }

    public void registerListener(final EventRunnable action, final InputSetting setting) {
        Preconditions.checkNotNull(setting);
        registerListener(action, setting.getEventType(), setting.getKeys());
    }

    /**
     * Register a listener that will run when the given key with(out) modifiers is set. If another action is
     * registered
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

        if (eventMap.containsKey(setKeys)) {
            GameMain.console
                .logf("Failed to register listener as another action is mapped to the same eventType(%s) and keys(%s)",
                      LogLevel.ERROR, eventType.name(), Util.keysToString(keys));
            return;
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
     *
     * @throws IllegalArgumentException
     *     if the {@code eventMetadata} is of the wrong type with respect to the {@code eventType}
     * @throws NullPointerException
     *     If either {@code eventType} or {@code eventMetadata} is null
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

    /**
     * Run all active events of the type {@link EventType#KEY_PRESSED}
     * <p>
     * this method is intended to be ran once every frame, preferably in {@link ApplicationListener#render()}
     * before all
     * other update methods
     */
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
        this.pressed.add(MouseInput.fromGdxButton(button));

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
        this.pressed.remove(MouseInput.fromGdxButton(button));
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





