package no.erlyberly.bootlegterraria.input.event;

import no.erlyberly.bootlegterraria.input.event.metadata.EventMetadata;

/**
 * @author kheba
 */
public interface EventRunnable {

    /**
     * @param metadata
     *     Metadata of this event
     */
    void run(EventMetadata metadata);
}
