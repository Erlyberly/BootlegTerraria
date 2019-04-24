package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.living.Player;
import no.erlyberly.bootlegterraria.util.Vector2Int;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import javax.annotation.Nonnull;

public abstract class Entity {

    protected GameMap gameMap = GameMain.map;

    protected Vector2 pos;
    protected float velocityY;
    protected boolean onGround;
    protected boolean destroyed;
    protected float health;
    public boolean collision;
    private int facing;

    private final Rectangle entRect;

    /**
     * Spawn an entity at ({@code x}, {@code y})
     *
     * @param x
     *     X coordinate to spawn the entity on
     * @param y
     *     Y coordinate to spawn the entity on
     */
    protected Entity(final float x, final float y) {

        pos = new Vector2(x, y);

        health = getMaxHealth();
        velocityY = 0;
        onGround = false;
        destroyed = false;
        collision = true;
        facing = 1;
        entRect = new Rectangle(x, y, getWidth(), getHeight());
    }

    public void update() {
        if (!collision) { return; }
        velocityY -= Math.signum(GameMap.gravity) * TileType.TILE_SIZE * GameMap.gravity * GameMap.gravity *
                     Gdx.graphics.getDeltaTime();
        final float newY = pos.y + velocityY * Gdx.graphics.getDeltaTime();


        //check for damage from tiles
        if (this instanceof Player) {
            gameMap.checkPlayerMapCollisionDamage(pos.x, newY, getWidth(), getHeight());
        }

        //check for collision with the map
        if (gameMap.checkMapCollision(pos.x, newY, getWidth(), getHeight())) {

            if (velocityY < 0) {
                pos.y = Math.max((float) Math.floor(pos.y), 0);
                onGround = true;
            }
            velocityY = 0;
        }
        else {
            pos.y = newY;
            onGround = false;
        }
    }

    /**
     * Move the entity vertically
     *
     * @param velocityY
     *     How much to move vertically
     */
    public void moveY(final float velocityY) {
        final float newY = pos.y + velocityY * Gdx.graphics.getDeltaTime();
        if ((!collision && !gameMap.checkMapCollision(pos.x, newY)) ||
            !gameMap.checkMapCollision(pos.x, newY, getWidth(), getHeight())) {
            pos.y = newY;
        }
    }

    /**
     * Move the entity horizontally
     *
     * @param velocityX
     *     How much to move horizontally
     */
    public void moveX(final float velocityX) {
        final float newX = pos.x + velocityX * Gdx.graphics.getDeltaTime();
        if ((!collision && !gameMap.checkMapCollision(newX, pos.y)) ||
            !gameMap.checkMapCollision(newX, pos.y, getWidth(), getHeight())) {
            pos.x = newX;
        }
    }

    /**
     * Destroy this entity
     */
    public void destroy() {
        destroyed = true;
        destroyed();
    }

    public float getHealth() {
        return health;
    }


    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * @return Screen position of the player
     */
    public Vector2 getPos() {
        return pos;
    }

    /**
     * @return The tile position of the entity
     */
    public Vector2Int getMapPos() {
        return new Vector2Int((int) (pos.x / TileType.TILE_SIZE), (int) (pos.y / TileType.TILE_SIZE));
    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public float getVelocityY() {
        return velocityY;
    }


    public boolean isOnGround() {
        return onGround;
    }

    @Nonnull
    public GameMap getGameMap() {
        return gameMap;
    }

    public int getFacing() {
        return facing;
    }

    public boolean isFacingRight() {
        return facing == 1;
    }

    /**
     * Set the facing of the entity, 1 for
     *
     * @param facing
     *     Either 1, for looking right, or -1, for looking left
     *
     * @throws IllegalArgumentException
     *     If {@code facing} is not {@code 1} or {@code -1}
     */
    protected void setFacing(final int facing) {
        Preconditions.checkArgument(facing == 1 || facing == -1, "Facing must be either -1 or 1");
        this.facing = facing;
    }

    /**
     * Teleport the entity to a given location. If the destination of the teleportation is out of bounds the entity is
     * moved to the closest point within the map
     */
    public Vector2 teleport(final int blockX, final int blockY) {
        //TODO just ignore calls to tp out of bounds
        int tpX = (int) (blockX * gameMap.getTileWidth());
        int tpY = (int) ((getHeight() - blockY) * gameMap.getTileHeight());

        tpX = (int) Math.max(0, Math.min(tpX, gameMap.getPixelWidth() - 1));
        tpY = (int) Math.max(0, Math.min(tpY, gameMap.getPixelHeight() - 1));

        if (gameMap.checkPlayerMapCollisionDamage(tpX, tpY, getWidth(), getHeight())) {
            tpY = (int) ((gameMap.getSkylightAt(blockX) + (getHeight() / gameMap.getTileHeight())) *
                         gameMap.getTileWidth());
        }

        pos.x = tpX;
        pos.y = tpY;
        return new Vector2(tpX / gameMap.getTileWidth(), tpY / gameMap.getTileHeight());
    }


    /**
     * @return An minimum rectangle that cover the player
     */
    @Nonnull
    public Rectangle toRect() {
        return entRect.setPosition(pos);
    }

    /**
     * How to render the entity
     *
     * @param batch
     *     The batch that this entity is rendered in
     */
    public abstract void render(SpriteBatch batch);

    /**
     * @param amount
     *     How much to heal the entity
     */
    public abstract void modifyHp(float amount);

    /**
     * Handle what's happening when the entity is destroyed
     */
    protected void destroyed() {}

    /**
     * @return How wide the entity is
     */
    public abstract float getWidth();

    /**
     * @return How high the entity is
     */
    public abstract float getHeight();

    /**
     * @return How fast the entity moves
     */
    public abstract float getHorizontalSpeed();

    /**
     * @return The maximum health the entity can have
     */
    public abstract float getMaxHealth();

    /**
     * @return How hard the entity hits
     */
    public abstract float getDamage();

    public abstract boolean isFlying();
}
