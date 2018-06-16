package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import no.erlyberly.bootlegterraria.entities.entities.Player;
import no.erlyberly.bootlegterraria.inventory.CreativeInventory;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.render.light.api.LightMap;
import no.erlyberly.bootlegterraria.util.GameInfo;
import no.erlyberly.bootlegterraria.util.Vector2Int;

import java.util.ArrayList;

import static java.lang.Math.ceil;

@SuppressWarnings("ALL")
public abstract class GameMap {

    public static float gravity = 9.81f;
    public static float cameraLerp = 2.5f;
    private final String fileName;

    private ArrayList<Entity> enemies;
    private ArrayList<Entity> addEnimies;
    private boolean addWaitingEnimies = false;
    private boolean removeWaitingEnimies = false;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> addEntities;
    private boolean addWaitingEntities = false;
    private boolean removeWaitingEntities = false;

    private Player player;

    BitmapFont font;

    private Texture hpBar;
    private Texture barOutline;
    private Texture staminaBar;
    Texture loadingSplashScreen;

    private CreativeInventory inv;

    GameMap(String fileName, boolean headless) {
        this.fileName = fileName;
        enemies = new ArrayList<Entity>();
        addEnimies = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        addEntities = new ArrayList<Entity>();


        if (!headless) {

            final FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/UbuntuMono-R.ttf"));
            final FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 20;
            parameter.minFilter = Texture.TextureFilter.Linear;
            this.font = generator.generateFont(parameter);
            generator.dispose();

            hpBar = new Texture("hp_fill.png");
            barOutline = new Texture("bar_outline.png");
            staminaBar = new Texture("stamina_fill.png");
            loadingSplashScreen = new Texture("goodlogic.png");

            inv = new CreativeInventory();
        }
    }

    public void addEnemy(Entity entity) {
        addEnimies.add(entity);
        addWaitingEnimies = true;
    }

    public void removeEnemy() {
        removeWaitingEnimies = true;
    }

    public void addEntity(Entity entity) {
        addEntities.add(entity);
        addWaitingEntities = true;
    }

    public void removeEntity() {
        removeWaitingEntities = true;
    }

    public void render(OrthographicCamera camera, OrthographicCamera hudCamera, SpriteBatch batch) {
        batch.begin();

        player.render(batch);

        for (Entity enimies : enemies) {
            enimies.render(batch);
        }

        for (Entity entities : entities) {
            entities.render(batch);
        }

        Vector3 position = camera.position;
        position.x += (player.getX() - position.x) * cameraLerp * player.speed * Gdx.graphics.getDeltaTime();
        position.y += (player.getY() - position.y) * cameraLerp * player.speed * Gdx.graphics.getDeltaTime();

//        camera.position.x = player.getX();
//        camera.position.y = player.getY();

        batch.setProjectionMatrix(hudCamera.combined);

        float hpBarModifier = 1.03f;

        //hp bar
        batch.draw(hpBar, 6f, GameInfo.HEIGHT / hpBarModifier,
                   (((float) player.getHealth() / (float) player.getMaxHealth()) * hpBar.getWidth()),
                   hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / hpBarModifier);
        font.draw(batch, (int) player.getHealth() + " / " + (int) player.getMaxHealth(), barOutline.getWidth() + 10f,
                  12f + GameInfo.HEIGHT / hpBarModifier);

        float staminaBarModifier = 1.055f;

        //stamina bar
        float staminaPercent = (float) player.getStamina() / (float) player.getMaxStamina();

        batch.draw(staminaBar, 6f, GameInfo.HEIGHT / staminaBarModifier, staminaPercent * hpBar.getWidth(),
                   hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / staminaBarModifier);
        font.draw(batch, (int) player.getStamina() + " / " + (int) player.getMaxStamina(), barOutline.getWidth() + 10f,
                  12f + GameInfo.HEIGHT / staminaBarModifier);

        final Vector3 mousePos =
            GameMain.inst().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        final int blockX = (int) (mousePos.x / TileType.TILE_SIZE);
        final int blockY = (int) (mousePos.y / TileType.TILE_SIZE);

        Vector2Int pos = new Vector2Int(blockX, blockY);

        String tile = getTileTypeByCoordinate(getBlockLayer(), blockX, blockY) + "";
        String skylight = (blockX >= 0 && blockX < getWidth()) ? getSkylightAt(blockX) + "" : "??";

        String[] msgs = {//
            "Equipped : " + player.getWeapon().getName(), //
            "FPS : " + Gdx.graphics.getFramesPerSecond(), //
            "Sel Blk : " + inv.getSelectedTileTypeAsString(), //
        //BC - block coordinates, MC - mouse coordinates, SL - SkyLight
            String.format("Mouse : BC (%d, %d) MC (%.1f, %.1f) SL %s", blockX, blockY, mousePos.x, mousePos.y,
                          skylight),//
            String.format("Blk @ Mus: Blk %s LI %s", tile, getLightMap().lightInfoAt(pos)),//
            String.format("World : Name %s width %.0f height %.0f", fileName, getWidth(), getHeight()),//
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

        for (Entity entity : enemies) {
            entity.update();
        }

        for (Entity entity : entities) {
            entity.update();
        }

        if (addWaitingEnimies) {
            for (Entity add : addEnimies) {
                enemies.add(add);
            }
            addEnimies.clear();
            addWaitingEnimies = false;
        }

        if (removeWaitingEnimies) {
            ArrayList<Entity> removeEnimies = new ArrayList<Entity>();
            for (Entity i : enemies) {
                if (i.isDestroyed()) {
                    removeEnimies.add(i);
                }
            }
            for (Entity remove : removeEnimies) {
                enemies.remove(remove);
            }
            removeWaitingEnimies = false;
        }

        if (addWaitingEntities) {
            for (Entity add : addEntities) {
                entities.add(add);
            }
            addEntities.clear();
            addWaitingEntities = false;
        }

        if (removeWaitingEntities) {
            ArrayList<Entity> removeEntities = new ArrayList<Entity>();
            for (Entity i : entities) {
                if (i.isDestroyed()) {
                    removeEntities.add(i);
                }
            }
            for (Entity remove : removeEntities) {
                entities.remove(remove);
            }
            removeWaitingEntities = false;
        }

        if (Gdx.input.isTouched() || Gdx.input.isButtonPressed(Input.Buttons.FORWARD)) {
            final Vector3 pos =
                GameMain.inst().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            final TileType type = getTileTypeByLocation(getBlockLayer(), pos.x, pos.y);

            final int blockX = (int) (pos.x / TileType.TILE_SIZE);
            final int blockY = (int) (pos.y / TileType.TILE_SIZE);


            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                if (Gdx.input.justTouched()) {
                    if (type != null) {
                        GameMain.consHldr().log(
                            "Tile clicked: " + type.getName() + ", id: " + type.getId() + ", dmg: " + type.getDps() +
                            " coord: (" + blockX + ", " + blockY + ")");
                    }
                    else {
                        GameMain.consHldr().log("Not a tile");
                    }
                }
            }
            else {
                TileType tt = null;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    tt = inv.getSelectedTileType();
                }
                else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    tt = null;
                }
                else {
                    //invalid button pressed, do nothing
                    return;
                }
                setTile(blockX, blockY, tt);
            }
        }
    }

    public abstract void dispose();

    public TileType getTileTypeByLocation(MapLayer layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    public abstract TileType getTileTypeByCoordinate(MapLayer layer, int col, int row);

    public boolean checkMapCollision(float x, float y, float width, float height) {
        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE), rows = (int) ceil((y + height) / TileType.TILE_SIZE);
             row < rows; row++) {
            for (int col = (int) (x / TileType.TILE_SIZE), cols = (int) Math.ceil((x + width) / TileType.TILE_SIZE);
                 col < cols; col++) {
                TileType type = getTileTypeByCoordinate(getBlockLayer(), col, row);
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
    public boolean checkPlayerMapCollisionDamage(float x, float y, float width, float height) {
        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE); row < ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) (x / TileType.TILE_SIZE); col < ceil((x + width) / TileType.TILE_SIZE); col++) {
                TileType type = getTileTypeByCoordinate(getBlockLayer(), col, row);
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
     * Check if there is a collision between entities and enemies
     */
    public void checkEntityEnemyCollision() {
        for (Entity entity : entities) {
            Rectangle entRect = entity.toRect();
            for (Entity enemy : enemies) {
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
    public boolean isOutsideMap(int blockX, int blockY) {
        return blockX < 0 || blockY < 0 || blockX >= getWidth() || blockY >= getHeight();
    }

    public void checkPlayerEnemyCollision() {
        Rectangle playerRect = player.toRect();

        for (Entity enemy : enemies) {
            if (!player.isInvincible() && Intersector.overlaps(enemy.toRect(), playerRect)) {
                player.modifyHp(-enemy.getDamage() * Gdx.graphics.getDeltaTime());
            }
        }
    }

    public Vector2Int blockToPixel(int blockX, int BlockY) {
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

    public void setPlayer(Player player) {
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
    public TileType getTile(int blockX, int blockY) {
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

    public LightLevel lightAt(int blockX, int blockY) {
        return lightAt(new Vector2Int(blockX, blockY));
    }

    public abstract LightLevel lightAt(Vector2Int pos);

    public abstract LightMap getLightMap();
}
