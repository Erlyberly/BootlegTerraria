package no.erlyberly.bootlegterraria.input.event;

import no.erlyberly.bootlegterraria.input.event.metadata.*;

/**
 * @author kheba
 */
public enum EventType {
    //custom events
    /**
     * Triggered every frame when the given key combination is pressed
     */
    KEY_PRESSED(GenericMetadata.class),
    /**
     * Triggered every frame when the given key combination is pressed
     */
    MOUSE_TOUCHED(GenericMetadata.class),
    // Events from InputProcessor
    /**
     * @see com.badlogic.gdx.InputProcessor#keyDown(int)
     */
    KEY_DOWN(KeyMetadata.class),
    /**
     * @see com.badlogic.gdx.InputProcessor#keyUp(int)
     */
    KEY_UP(KeyMetadata.class),
    /**
     * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
     */
    KEY_TYPED(TypedMetadata.class),
    /**
     * @see com.badlogic.gdx.InputProcessor#scrolled(int)
     */
    MOUSE_SCROLLED(ScrolledMetadata.class),
    /**
     * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
     */
    MOUSE_DOWN(MouseMetadata.class),
    /**
     * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
     */
    MOUSE_UP(MouseMetadata.class),
    /**
     * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
     */
    MOUSE_DRAGGED(MouseMetadata.class),
    /**
     * @see com.badlogic.gdx.InputProcessor#mouseMoved(int, int)
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
        return metadata.getClass() == this.metadataClass;
    }

    public String getMetadataName() {
        return this.metadataClass.getSimpleName();
    }
}
