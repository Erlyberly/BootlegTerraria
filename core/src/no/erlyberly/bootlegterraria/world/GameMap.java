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
import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.entities.Player;
import no.erlyberly.bootlegterraria.util.GameInfo;

import java.util.ArrayList;

import static java.lang.Math.ceil;

@SuppressWarnings("ALL")
public abstract class GameMap {

    public static float gravity = 9.81f;

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

    Texture hpBar = new Texture("hp_fill.png");
    Texture barOutline = new Texture("bar_outline.png");
    Texture staminaBar = new Texture("stamina_fill.png");

    GameMap() {
        enemies = new ArrayList<Entity>();
        addEnimies = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        addEntities = new ArrayList<Entity>();

        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/UbuntuMono-R.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.minFilter = Texture.TextureFilter.Linear;
        this.font = generator.generateFont(parameter);
        generator.dispose();
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

        camera.position.x = player.getX();
        camera.position.y = player.getY();

        batch.setProjectionMatrix(hudCamera.combined);

        float hpBarModifier = 1.03f;

        //hp bar
        batch.draw(hpBar, 6f, GameInfo.HEIGHT / hpBarModifier,
                   (((float) player.getHealth() / (float) player.getMaxHealth()) * hpBar.getWidth()),
                   hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / hpBarModifier);
        font.draw(batch, (int) player.getHealth() + " / " + player.getMaxHealth(), barOutline.getWidth() + 10f,
                  12f + GameInfo.HEIGHT / hpBarModifier);

        float staminaBarModifier = 1.055f;

        //stamina bar
        float staminaPercent = (float) player.getStamina() / (float) player.getMaxStamina();

        batch.draw(staminaBar, 6f, GameInfo.HEIGHT / staminaBarModifier, staminaPercent * hpBar.getWidth(),
                   hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / staminaBarModifier);
        font.draw(batch, (int) player.getStamina() + " / " + player.getMaxStamina(), barOutline.getWidth() + 10f,
                  12f + GameInfo.HEIGHT / staminaBarModifier);

        //hud text
        font.draw(batch, "Weapon: " + player.getWeapon().getName(), 7f, GameInfo.HEIGHT / 1.07f);
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 7f, GameInfo.HEIGHT / (1.095f));
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
//                for (int layer = 0; layer < getLayers(); layer++) {
                TileType type = getTileTypeByCoordinate(getBlockLayer(), col, row);
                if (type != null && type.isCollidable()) {
                    return true;
                }
//                }
            }
        }

        return false;
    }

    public boolean checkPlayerMapCollision(float x, float y, float width, float height) {

        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE); row < ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) (x / TileType.TILE_SIZE); col < ceil((x + width) / TileType.TILE_SIZE); col++) {
//                for (int layer = 0; layer < getLayers(); layer++) {
                TileType type = getTileTypeByCoordinate(getBlockLayer(), col, row);
                if (type != null && type.isCollidable()) {
                    if (type.getDps() != 0 && !player.isInvincible()) {
                        player.modifyHp(-type.getDps() * Gdx.graphics.getDeltaTime());
                    }
                    return true;
                }
//                }
            }
        }

        return false;
    }

    public void checkEntityEnemyCollision() {
        for (Entity entities : entities) {
            for (Entity enemies : enemies) {
                if (Intersector.overlaps(
                    new Rectangle(entities.getX(), entities.getY(), entities.getWidth(), entities.getHeight()),
                    new Rectangle(enemies.getX(), enemies.getY(), enemies.getWidth(), enemies.getHeight()))) {
                    enemies.modifyHp(-entities.getDamage() * Gdx.graphics.getDeltaTime());
                    enemies.moveX(enemies.getHorizontalSpeed() * -enemies.getFacing());
                }
            }
        }
    }

    public boolean isOutsideMap(int x, int y) {
        if (x < 0 || y < 0 || x >= getPixelWidth() || y > getPixelHeight()) {
            return true;
        }
        return false;
    }

    public void checkPlayerEnemyCollision() {

        float playerX = player.getX();
        float playerY = player.getY();
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();

        for (Entity enemies : enemies) {
            if (!player.isInvincible() && Intersector
                .overlaps(new Rectangle(enemies.getX(), enemies.getY(), enemies.getWidth(), enemies.getHeight()),
                          new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
                player.modifyHp(-enemies.getDamage() * Gdx.graphics.getDeltaTime());
            }
        }
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
     * @param x
     *     X coordinate of the block to change
     * @param y
     *     Y coordinate of the block to change
     * @param tt
     *     The new block
     */
    public abstract void setBlockAt(int x, int y, TileType tt);

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
    public abstract Vector2 getSpawn();

    /**
     * @return The layer where the collidable blocks are
     */
    public abstract MapLayer getBlockLayer();

    /**
     * @return Instance of the map object
     */
    public abstract Map getMap();
}
