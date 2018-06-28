package no.erlyberly.bootlegterraria.input.event.metadata;

import no.erlyberly.bootlegterraria.GameMain;

/**
 * @author kheba
 */
public class MouseMetadata implements EventMetadata {

    public final int screenX;
    public final int screenY;

    public final int blockX;
    public final int blockY;

    public MouseMetadata(final int screenX, final int screenY) {

        this.screenX = screenX;
        this.screenY = screenY;

        this.blockX = (int) (screenX / GameMain.inst().getGameMap().getTileWidth());
        this.blockY = (int) (screenY / GameMain.inst().getGameMap().getTileHeight());
    }
}
