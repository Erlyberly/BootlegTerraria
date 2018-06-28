package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.living.Player;
import no.erlyberly.bootlegterraria.inventory.TileStack;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.render.light.api.LightMap;
import no.erlyberly.bootlegterraria.util.GameInfo;
import no.erlyberly.bootlegterraria.util.Vector2Int;

import java.util.ArrayList;

import static java.lang.Math.ceil;

public abstract class GameMap {

    public static float gravity = 9.81f;
    public static float cameraLerp = 2.5f;
    private final String fileName;

    private final ArrayList<Entity> enemies;
    private final ArrayList<Entity> addEnimies;
    private boolean addWaitingEnimies = false;
    private boolean removeWaitingEnimies = false;

    private final ArrayList<Entity> entities;
    private final ArrayList<Entity> addEntities;
    private boolean addWaitingEntities = false;
    private boolean removeWaitingEntities = false;

    private Player player;

    BitmapFont font;

    private Texture hpBar;
    private Texture barOutline;
    private Texture staminaBar;
    Texture loadingSplashScreen;

    GameMap(final String fileName, final boolean headless) {
        this.fileName = fileName;
        this.enemies = new ArrayList<>();
        this.addEnimies = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.addEntities = new ArrayList<>();


        if (!headless) {

            final FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/UbuntuMono-R.ttf"));
            final FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 20;
            parameter.minFilter = Texture.TextureFilter.Linear;
            this.font = generator.generateFont(parameter);
            generator.dispose();

            this.hpBar = new Texture("hp_fill.png");
            this.barOutline = new Texture("bar_outline.png");
            this.staminaBar = new Texture("stamina_fill.png");
            this.loadingSplashScreen = new Texture("goodlogic.png");
        }
    }

    public void addEnemy(final Entity entity) {
        this.addEnimies.add(entity);
        this.addWaitingEnimies = true;
    }

    public void removeEnemy() {
        this.removeWaitingEnimies = true;
    }

    public void addEntity(final Entity entity) {
        this.addEntities.add(entity);
        this.addWaitingEntities = true;
    }

    public void removeEntity() {
        this.removeWaitingEntities = true;
    }

    public void render(final OrthographicCamera camera, final OrthographicCamera hudCamera, final SpriteBatch batch) {
        batch.begin();

        this.player.render(batch);

        for (final Entity enimies : this.enemies) {
            enimies.render(batch);
        }

        for (final Entity entities : this.entities) {
            entities.render(batch);
        }

        final Vector3 position = camera.position;
        position.x +=
            (this.player.getX() - position.x) * cameraLerp * this.player.speedModifier * Gdx.graphics.getDeltaTime();
        position.y +=
            (this.player.getY() - position.y) * cameraLerp * this.player.speedModifier * Gdx.graphics.getDeltaTime();

//        camera.position.x = player.getX();
//        camera.position.y = player.getY();

        batch.setProjectionMatrix(hudCamera.combined);

        final float hpBarModifier = 1.03f;

        //hp bar
        batch.draw(this.hpBar, 6f, GameInfo.HEIGHT / hpBarModifier,
                   ((this.player.getHealth() / this.player.getMaxHealth()) * this.hpBar.getWidth()),
                   this.hpBar.getHeight());
        batch.draw(this.barOutline, 5f, GameInfo.HEIGHT / hpBarModifier);
        this.font.draw(batch, (int) this.player.getHealth() + " / " + (int) this.player.getMaxHealth(),
                       this.barOutline.getWidth() + 10f, 12f + GameInfo.HEIGHT / hpBarModifier);

        final float staminaBarModifier = 1.055f;

        //stamina bar
        final float staminaPercent = this.player.getStamina() / (float) this.player.getMaxStamina();

        batch.draw(this.staminaBar, 6f, GameInfo.HEIGHT / staminaBarModifier, staminaPercent * this.hpBar.getWidth(),
                   this.hpBar.getHeight());
        batch.draw(this.barOutline, 5f, GameInfo.HEIGHT / staminaBarModifier);
        this.font.draw(batch, (int) this.player.getStamina() + " / " + this.player.getMaxStamina(),
                       this.barOutline.getWidth() + 10f, 12f + GameInfo.HEIGHT / staminaBarModifier);

        final Vector3 mousePos =
            GameMain.inst().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        final int blockX = (int) (mousePos.x / TileType.TILE_SIZE);
        final int blockY = (int) (mousePos.y / TileType.TILE_SIZE);

        final Vector2Int pos = new Vector2Int(blockX, blockY);

        final String tile = getTileTypeByCoordinate(getBlockLayer(), blockX, blockY) + "";
        final String skylight = (blockX >= 0 && blockX < getWidth()) ? getSkylightAt(blockX) + "" : "??";

        final TileStack holding = this.player.getInv().holding();
        final String selBlk;
        if (holding == null) {
            selBlk = "None";
        }
        else {
            selBlk = String.format("%s %s[%s]", holding.getAmount(), holding.getTileType().getName(),
                                   holding.getTileType().getId());
        }

        final String[] msgs = {//
            "Equipped : " + this.player.getWeapon().getName(), //
            "FPS : " + Gdx.graphics.getFramesPerSecond(), //
            "Sel Blk : " + selBlk + " {sel=" + this.player.getInv().getSel() + "}", //
            //BC - block coordinates, MC - mouse coordinates, SL - SkyLight
            String.format("Mouse : BC (%d, %d) MC (%.1f, %.1f) SL %s", blockX, blockY, mousePos.x, mousePos.y,
                          skylight), //
            String.format("Blk @ Mus: Blk %s LI %s", tile, getLightMap().lightInfoAt(pos)), //
            String.format("World : Name %s width %.0f height %.0f", this.fileName, getWidth(), getHeight()), //
        };

        for (int i = 0; i < msgs.length; i++) {
            this.font.draw(batch, msgs[i], 7f, GameInfo.HEIGHT / (1.07f + 0.030f * i));
        }
        batch.end();
    }


    public void update() {

        checkEntityEnemyCollision();
        checkPlayerEnemyCollision();

        this.player.update();

        for (final Entity entity : this.enemies) {
            entity.update();
        }

        for (final Entity entity : this.entities) {
            entity.update();
        }

        if (this.addWaitingEnimies) {
            this.enemies.addAll(this.addEnimies);
            this.addEnimies.clear();
            this.addWaitingEnimies = false;
        }

        if (this.removeWaitingEnimies) {
            final ArrayList<Entity> removeEnemies = new ArrayList<>();
            for (final Entity i : this.enemies) {
                if (i.isDestroyed()) {
                    removeEnemies.add(i);
                }
            }
            for (final Entity remove : removeEnemies) {
                this.enemies.remove(remove);
            }
            this.removeWaitingEnimies = false;
        }

        if (this.addWaitingEntities) {
            this.entities.addAll(this.addEntities);
            this.addEntities.clear();
            this.addWaitingEntities = false;
        }

        if (this.removeWaitingEntities) {
            final ArrayList<Entity> removeEntities = new ArrayList<>();
            for (final Entity i : this.entities) {
                if (i.isDestroyed()) {
                    removeEntities.add(i);
                }
            }
            for (final Entity remove : removeEntities) {
                this.entities.remove(remove);
            }
            this.removeWaitingEntities = false;
        }
    }

    public abstract void dispose();

    public TileType getTileTypeByLocation(final MapLayer layer, final float x, final float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    public abstract TileType getTileTypeByCoordinate(MapLayer layer, int col, int row);

    public boolean checkMapCollision(final float x, final float y, final float width, final float height) {
        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE), rows = (int) ceil((y + height) / TileType.TILE_SIZE);
             row < rows; row++) {
            for (int col = (int) (x / TileType.TILE_SIZE), cols = (int) Math.ceil((x + width) / TileType.TILE_SIZE);
                 col < cols; col++) {
                final TileType type = getTileTypeByCoordinate(getBlockLayer(), col, row);
                if (type != null && type.isSolid()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return if the player takes damage from fall damage
     */
    public boolean checkPlayerMapCollisionDamage(final float x, final float y, final float width, final float height) {
        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE); row < ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) (x / TileType.TILE_SIZE); col < ceil((x + width) / TileType.TILE_SIZE); col++) {
                final TileType type = getTileTypeByCoordinate(getBlockLayer(), col, row);
                if (type != null && type.isSolid()) {
                    if (type.getDps() != 0 && !this.player.isInvincible()) {
                        this.player.modifyHp(-type.getDps() * Gdx.graphics.getDeltaTime());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if there is a collision between living and enemies
     */
    public void checkEntityEnemyCollision() {
        for (final Entity entity : this.entities) {
            final Rectangle entRect = entity.toRect();
            for (final Entity enemy : this.enemies) {
                if (Intersector.overlaps(entRect, enemy.toRect())) {
                    enemy.modifyHp(-entity.getDamage() * Gdx.graphics.getDeltaTime());
                    enemy.moveX(enemy.getHorizontalSpeed() * -enemy.getFacing());
                }
            }
        }
    }

    /**
     * @param blockX
     *     The x coordinate of the block to check
     * @param blockY
     *     The y coordinate of the block to check
     *
     * @return true if the block at ({@code x, y}) is outside of the maps bounds
     */
    public boolean isOutsideMap(final int blockX, final int blockY) {
        return isOutsideMap(blockX, blockY, (int) getWidth(), (int) getHeight());
    }

    public static boolean isOutsideMap(final int blockX, final int blockY, final int width, final int height) {
        return blockX < 0 || blockY < 0 || blockX >= width || blockY >= height;
    }

    public void checkPlayerEnemyCollision() {
        final Rectangle playerRect = this.player.toRect();

        for (final Entity enemy : this.enemies) {
            if (!this.player.isInvincible() && Intersector.overlaps(enemy.toRect(), playerRect)) {
                this.player.modifyHp(-enemy.getDamage() * Gdx.graphics.getDeltaTime());
            }
        }
    }

    public Vector2Int blockToPixel(final int blockX, final int BlockY) {
        int spawnX = (int) (blockX * getTileWidth());
        int spawnY = (int) ((getHeight() - BlockY) * getTileHeight());

        spawnX = (int) Math.max(0, Math.min(spawnX, getPixelWidth() - 1));
        spawnY = (int) Math.max(0, Math.min(spawnY, getPixelHeight() - 1));
        return new Vector2Int(spawnX, spawnY);
    }

    public float getPixelWidth() {
        return getWidth() * getTileWidth();
    }

    public float getPixelHeight() {
        return getHeight() * getTileHeight();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    /**
     * Change the block at ({@code x}, {@code y}) to {@code tt}
     *
     * @param blockX
     *     X coordinate of the block to change
     * @param blockY
     *     Y coordinate of the block to change
     * @param tt
     *     The new block
     */
    public abstract void setTile(int blockX, int blockY, TileType tt);

    /**
     * @return The TileType at given location
     */
    public TileType getTile(final int blockX, final int blockY) {
        return getTileTypeByCoordinate(getBlockLayer(), blockX, blockY);
    }

    /**
     * @return Number of blocks horizontally
     */
    public abstract float getWidth();

    /**
     * @return Number of blocks vertically
     */
    public abstract float getHeight();

    /**
     * @return Width of a tile
     */
    public abstract float getTileWidth();

    /**
     * @return Height of a tile
     */
    public abstract float getTileHeight();

    /**
     * @return Player spawn location
     */
    public abstract Vector2Int getSpawn();

    /**
     * @return The layer where the collidable blocks are
     */
    public abstract MapLayer getBlockLayer();

    /**
     * @return The layer where the non-collidable blocks are
     */
    public abstract MapLayer getBackgroundLayer();


    /**
     * @return Instance of the map object
     */
    public abstract Map getMap();

    /**
     * Spawn the player at {@link #getSpawn()}
     */
    public abstract void spawnPlayer();

    public abstract int getSkylightAt(int blockX);

    public LightLevel lightAt(final int blockX, final int blockY) {
        return lightAt(new Vector2Int(blockX, blockY));
    }

    public abstract LightLevel lightAt(Vector2Int pos);

    public abstract LightMap getLightMap();
}
