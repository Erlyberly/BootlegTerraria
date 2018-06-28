package no.erlyberly.bootlegterraria.input.event.metadata;

/**
 * @author kheba
 */
public class KeyMetadata implements EventMetadata {

    public final int keycode;

    public KeyMetadata(final int keycode) {

        this.keycode = keycode;
    }
}
