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
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.render.light.api.LightMap;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.util.GameInfo;
import no.erlyberly.bootlegterraria.util.Vector2Int;
import org.jetbrains.annotations.Nullable;

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

    public final BitmapFont font;

    private Texture hpBar;
    private Texture barOutline;
    private Texture staminaBar;
    Texture loadingSplashScreen;


    GameMap(final String fileName, final boolean headless) {
        this.fileName = fileName;
        enemies = new ArrayList<>();
        addEnimies = new ArrayList<>();
        entities = new ArrayList<>();
        addEntities = new ArrayList<>();

        if (!headless) {

            final FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/UbuntuMono-R.ttf"));
            final FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 20;
            parameter.minFilter = Texture.TextureFilter.Linear;
            font = generator.generateFont(parameter);
            generator.dispose();

            hpBar = new Texture("hp_fill.png");
            barOutline = new Texture("bar_outline.png");
            staminaBar = new Texture("stamina_fill.png");
            loadingSplashScreen = new Texture("goodlogic.png");
        }
        else { font = null; }
    }

    public void addEnemy(final Entity entity) {
        addEnimies.add(entity);
        addWaitingEnimies = true;
    }

    public void removeEnemy() {
        removeWaitingEnimies = true;
    }

    public void addEntity(final Entity entity) {
        addEntities.add(entity);
        addWaitingEntities = true;
    }

    public void removeEntity() {
        removeWaitingEntities = true;
    }

    public void render(final OrthographicCamera camera, final OrthographicCamera hudCamera, final SpriteBatch batch) {
        batch.begin();

        player.render(batch);

        for (final Entity enimies : enemies) {
            enimies.render(batch);
        }

        for (final Entity entities : entities) {
            entities.render(batch);
        }

        final Vector3 position = camera.position;
        position.x += (player.getX() - position.x) * cameraLerp * player.speedModifier * Gdx.graphics.getDeltaTime();
        position.y += (player.getY() - position.y) * cameraLerp * player.speedModifier * Gdx.graphics.getDeltaTime();

//        camera.position.x = player.getX();
//        camera.position.y = player.getY();

        batch.setProjectionMatrix(hudCamera.combined);

        final float hpBarModifier = 1.03f;

        //hp bar
        batch.draw(hpBar, 6f, GameInfo.HEIGHT / hpBarModifier,
                   ((player.getHealth() / player.getMaxHealth()) * hpBar.getWidth()), hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / hpBarModifier);
        font.draw(batch, (int) player.getHealth() + " / " + (int) player.getMaxHealth(), barOutline.getWidth() + 10f,
                  12f + GameInfo.HEIGHT / hpBarModifier);

        final float staminaBarModifier = 1.055f;

        //stamina bar
        final float staminaPercent = player.getStamina() / (float) player.getMaxStamina();

        batch.draw(staminaBar, 6f, GameInfo.HEIGHT / staminaBarModifier, staminaPercent * hpBar.getWidth(),
                   hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / staminaBarModifier);
        font.draw(batch, (int) player.getStamina() + " / " + player.getMaxStamina(), barOutline.getWidth() + 10f,
                  12f + GameInfo.HEIGHT / staminaBarModifier);

        final Vector3 mousePos = GameMain.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        final int blockX = (int) (mousePos.x / TileType.TILE_SIZE);
        final int blockY = (int) (mousePos.y / TileType.TILE_SIZE);

        final Vector2Int pos = new Vector2Int(blockX, blockY);

        final String tile = getTileTypeByCoordinate(getBlockLayer(), blockX, blockY) + "";
        final String skylight = (blockX >= 0 && blockX < getWidth()) ? getSkylightAt(blockX) + "" : "??";

        final TileStack holding = player.getInv().holding();
        final String selBlk;
        if (holding == null) {
            selBlk = "None";
        }
        else {
            selBlk = String.format("%s %s[%s]", holding.getAmount(), holding.getTileType().getName(),
                                   holding.getTileType().getId());
        }

        final String[] msgs = {//
            "Equipped : " + player.getWeapon().getName(), //
            "FPS : " + Gdx.graphics.getFramesPerSecond(), //
            "Sel Blk : " + selBlk + " {sel=" + player.getInv().getSel() + "}", //
            //BC - block coordinates, MC - mouse coordinates, SL - SkyLight
            String.format("Mouse : BC (%d, %d) MC (%.1f, %.1f) SL %s", blockX, blockY, mousePos.x, mousePos.y,
                          skylight), //
            String.format("Blk@Mus: Blk %s LI %s", tile, getLightMap().lightInfoAt(pos)), //
            String.format("World : Name %s width %.0f height %.0f", fileName, getWidth(), getHeight()), //
        };

        for (int i = 0; i < msgs.length; i++) {
            font.draw(batch, msgs[i], 7f, GameInfo.HEIGHT / (1.07f + 0.030f * i));
        }

        batch.end();
    }


    public void update() {

        checkEntityEnemyCollision();
        checkPlayerEnemyCollision();

        player.update();

        for (final Entity entity : enemies) {
            entity.update();
        }

        for (final Entity entity : entities) {
            entity.update();
        }

        if (addWaitingEntities) {
            entities.addAll(addEntities);
            addEntities.clear();
            addWaitingEntities = false;
        }

        if (removeWaitingEntities) {
            final ArrayList<Entity> removeEntities = new ArrayList<>();
            for (final Entity i : entities) {
                if (i.isDestroyed()) {
                    removeEntities.add(i);
                }
            }
            for (final Entity remove : removeEntities) {
                entities.remove(remove);
            }
            removeWaitingEntities = false;
        }
    }

    public abstract void dispose();

    public TileType getTileTypeByLocation(final MapLayer layer, final float x, final float y) {
        return getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
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
                    if (type.getDps() != 0 && !player.isInvincible()) {
                        player.modifyHp(-type.getDps() * Gdx.graphics.getDeltaTime());
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
        for (final Entity entity : entities) {
            final Rectangle entRect = entity.toRect();
            for (final Entity enemy : enemies) {
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
        final Rectangle playerRect = player.toRect();

        for (final Entity enemy : enemies) {
            if (!player.isInvincible() && Intersector.overlaps(enemy.toRect(), playerRect)) {
                player.modifyHp(-enemy.getDamage() * Gdx.graphics.getDeltaTime());
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
        return player;
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

    /**
     *
     * @param blockX
     * @return
     * @throws IllegalArgumentException If the block location is outside the map
     */
    public abstract int getSkylightAt(int blockX);

    /**
     * @return The LightLevel at the given pos or null if the pos is invalid
     */
    @Nullable
    public LightLevel lightAt(final int blockX, final int blockY) {
        return lightAt(new Vector2Int(blockX, blockY));
    }

    /**
     * @return The LightLevel at the given pos or null if the pos is invalid
     */
    @Nullable
    public abstract LightLevel lightAt(Vector2Int pos);

    public abstract LightMap getLightMap();


    public String getFileName() {
        return fileName;
    }
}
