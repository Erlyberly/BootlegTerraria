package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.living.Player;
import no.erlyberly.bootlegterraria.inventory.Inventory;
import no.erlyberly.bootlegterraria.util.Vector2Int;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import javax.annotation.Nonnull;

public abstract class Entity {

    protected GameMap gameMap = GameMain.inst().getGameMap();

    protected Vector2 pos;
    protected float velocityY;
    protected boolean onGround;
    protected boolean destroyed;
    protected float health;
    private int facing;

    private final Rectangle entRect;
    private Inventory inv;


    /**
     * Spawn an entity at ({@code x}, {@code y})
     *
     * @param x
     *     X coordinate to spawn the entity on
     * @param y
     *     Y coordinate to spawn the entity on
     */
    protected Entity(final float x, final float y) {
        float newY = y;
        if (this.gameMap.checkPlayerMapCollisionDamage(x, y, getWidth(), getHeight())) {
            newY = (int) ((this.gameMap.getSkylightAt((int) (x / this.gameMap.getTileHeight())) +
                           (this.getHeight() / this.gameMap.getTileHeight())) * this.gameMap.getTileWidth());
        }

        this.pos = new Vector2(x, newY);

        this.health = getMaxHealth();
        this.velocityY = 0;
        this.onGround = false;
        this.destroyed = false;
        this.facing = 1;
        this.entRect = new Rectangle(x, y, getWidth(), getHeight());
    }

    public void update() {
        this.velocityY -= Math.signum(GameMap.gravity) * TileType.TILE_SIZE * GameMap.gravity * GameMap.gravity *
                          Gdx.graphics.getDeltaTime();
        final float newY = this.pos.y + this.velocityY * Gdx.graphics.getDeltaTime();

        if (this instanceof Player) {
            this.gameMap.checkPlayerMapCollisionDamage(this.pos.x, newY, getWidth(), getHeight());
        }

        if (this.gameMap.checkMapCollision(this.pos.x, newY, getWidth(), getHeight())) {
            if (this.velocityY < 0) {
                this.pos.y = (float) Math.floor(this.pos.y);
                this.onGround = true;
            }
            this.velocityY = 0;
        }
        else {
            this.pos.y = newY;
            this.onGround = false;
        }
    }

    public void moveY(final float velocityY) {
        final float newY = this.pos.y + velocityY * Gdx.graphics.getDeltaTime();
        if (!this.gameMap.checkMapCollision(this.pos.x, newY, getWidth(), getHeight())) {
            this.pos.y = newY;
        }
    }


    public void moveX(final float velocityX) {
        final float newX = this.pos.x + velocityX * Gdx.graphics.getDeltaTime();
        if (!this.gameMap.checkMapCollision(newX, this.pos.y, getWidth(), getHeight())) {
            this.pos.x = newX;
        }
    }

    /**
     * Destroy this entity
     */
    public void destroy() {
        this.destroyed = true;
        destroyed();
    }

    public float getHealth() {
        return this.health;
    }


    public boolean isDestroyed() {
        return this.destroyed;
    }

    /**
     * @return Screen position of the player
     */
    public Vector2 getPos() {
        return this.pos;
    }

    /**
     * @return The tile position of the entity
     */
    public Vector2Int getMapPos() {
        return new Vector2Int((int) (this.pos.x / TileType.TILE_SIZE), (int) (this.pos.y / TileType.TILE_SIZE));
    }

    public float getX() {
        return this.pos.x;
    }

    public float getY() {
        return this.pos.y;
    }

    public float getVelocityY() {
        return this.velocityY;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Nonnull
    public GameMap getGameMap() {
        return this.gameMap;
    }

    public int getFacing() {
        return this.facing;
    }

    public boolean isFacingRight() {
        return this.facing == 1;
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
        int tpX = (int) (blockX * this.gameMap.getTileWidth());
        int tpY = (int) ((getHeight() - blockY) * this.gameMap.getTileHeight());

        tpX = (int) Math.max(0, Math.min(tpX, this.gameMap.getPixelWidth() - 1));
        tpY = (int) Math.max(0, Math.min(tpY, this.gameMap.getPixelHeight() - 1));

        if (this.gameMap.checkPlayerMapCollisionDamage(tpX, tpY, getWidth(), getHeight())) {
            tpY = (int) ((this.gameMap.getSkylightAt(blockX) + (this.getHeight() / this.gameMap.getTileHeight())) *
                         this.gameMap.getTileWidth());
        }

        this.pos.x = tpX;
        this.pos.y = tpY;
        return new Vector2(tpX / this.gameMap.getTileWidth(), tpY / this.gameMap.getTileHeight());
    }


    /**
     * @return An minimum rectangle that cover the player
     */
    @Nonnull
    public Rectangle toRect() {
        return this.entRect.setPosition(this.pos);
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

    public Inventory getInv() {
        return this.inv;
    }

    public void setInv(final Inventory inv) {
        Preconditions.checkNotNull(inv);
        Preconditions.checkArgument(inv.getHolder() == this, "The owner of the inventory must be this entity");
        this.inv = inv;
    }
}
