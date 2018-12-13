package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.storage.IInventory;

/**
 * An entity that is alive, cannot be spawned inside a block and is affected by lighting
 *
 * <h1>Usage</h1>
 * In the {@link Entity#render(SpriteBatch)} method of the entity first call {@link #startShade(Batch)} then render the
 * entity with the same batch as given to the startShade method. When finished with rendering call {@link
 * #endShade(Batch)}
 *
 * @author kheba
 */
public abstract class LivingEntity extends Entity {

    private LightLevel ll;
    private Color oldColor;
    private boolean started;
    private IInventory inv;

    /**
     * Spawn an entity at ({@code x}, {@code y})
     *
     * @param x
     *     X coordinate to spawn the entity on
     */
    public LivingEntity(final float x, final float y) {
        super(x, y);
        float newY = y;
        if (gameMap.checkPlayerMapCollisionDamage(x, y, getWidth(), getHeight())) {
            newY = (int) (
                (gameMap.getSkylightAt((int) (x / gameMap.getTileHeight())) + (getHeight() / gameMap.getTileHeight())) *
                gameMap.getTileWidth());
        }
        pos.y = newY;
    }

    public void startShade(final Batch batch) {
        if (started) {
            throw new IllegalStateException("Shading is already started");
        }
        started = true;
        //make the player follow the lighting of the block they're standing on

        ll = gameMap.lightAt(getMapPos());
        oldColor = batch.getColor();

        if (ll != null) {
            final Color newColor = oldColor.cpy();

            newColor.r *= ll.getPercentage();
            newColor.g *= ll.getPercentage();
            newColor.b *= ll.getPercentage();

            batch.setColor(newColor);
        }
    }

    public void endShade(final Batch batch) {
        if (!started) {
            throw new IllegalStateException("Cannot end a batch when it is not started");
        }
        started = false;
        if (ll != null) {
            batch.setColor(oldColor);
        }
    }

    public IInventory getInv() {
        return inv;
    }

    public void setInv(final IInventory inv) {
        Preconditions.checkNotNull(inv);
        Preconditions.checkArgument(inv.isHolder(this), "The owner of the storage must be this entity, or null");
        this.inv = inv;
    }
}
