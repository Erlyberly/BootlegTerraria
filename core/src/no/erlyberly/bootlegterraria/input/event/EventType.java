package no.erlyberly.bootlegterraria.input.event;

import com.badlogic.gdx.InputProcessor;
import no.erlyberly.bootlegterraria.input.event.metadata.*;


/**
 * Hold all the different types of events that can be triggered by {@link InputProcessor}
 * and {@link #KEY_PRESSED} which is triggered every frame the given key combination is pressed
 *
 * @author kheba
 */
public enum EventType {
    //custom events
    /**
     * Triggered every frame when the given key combination is pressed
     */
    KEY_PRESSED(GenericMetadata.class),
    // Events from InputProcessor
    /**
     * @see InputProcessor#keyDown(int)
     */
    KEY_DOWN(KeyMetadata.class),
    /**
     * @see InputProcessor#keyUp(int)
     */
    KEY_UP(KeyMetadata.class),
    /**
     * @see InputProcessor#keyTyped(char)
     */
    KEY_TYPED(TypedMetadata.class),
    /**
     * @see InputProcessor#scrolled(int)
     */
    SCROLLED(ScrolledMetadata.class),
    /**
     * @see InputProcessor#touchDown(int, int, int, int)
     */
    TOUCH_DOWN(MouseMetadata.class),
    /**
     * @see InputProcessor#touchUp(int, int, int, int)
     */
    TOUCH_UP(MouseMetadata.class),
    /**
     * @see InputProcessor#touchDragged(int, int, int)
     */
    TOUCH_DRAGGED(MouseMetadata.class),
    /**
     * @see InputProcessor#mouseMoved(int, int)
     */
    MOUSE_MOVED(MouseMetadata.class),
    //
    ;

    private final Class<? extends EventMetadata> metadataClass;

    EventType(final Class<? extends EventMetadata> metadataClass) {

        this.metadataClass = metadataClass;
    }

    /**
     * @return If the given instance of eventMetadata is correct for this type of event
     */
    public boolean isCorrectMetadata(final EventMetadata metadata) {
        return metadata.getClass() == metadataClass;
    }

    public String getMetadataName() {
        return metadataClass.getSimpleName();
    }}