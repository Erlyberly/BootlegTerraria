package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.Player;
import no.erlyberly.bootlegterraria.helpers.GameInfo;
import no.erlyberly.bootlegterraria.helpers.RoundTo;

import java.util.ArrayList;

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

        camera.position.x = RoundTo.RoundToNearest(player.getX(), GameMain.getCameraPixel());
        camera.position.y = player.getY();
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        float hpBarModifier = 1.03f;

        //hp bar
        batch.draw(hpBar, 6f, GameInfo.HEIGHT / hpBarModifier,
                   (((float) player.getHp() / (float) player.getMaxHp()) * hpBar.getWidth()), hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / hpBarModifier);
        font.draw(batch, (int) player.getHp() + " / " + player.getMaxHp(), barOutline.getWidth() + 10f,
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

    public TileType getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    public abstract TileType getTileTypeByCoordinate(int layer, int col, int row);

    public boolean checkMapCollision(float x, float y, float width, float height) {

        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE); row < Math.ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) (x / TileType.TILE_SIZE); col < Math.ceil((x + width) / TileType.TILE_SIZE); col++) {
                for (int layer = 0; layer < getLayers(); layer++) {
                    TileType type = getTileTypeByCoordinate(layer, col, row);
                    if (type != null && type.isCollidable()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean checkPlayerMapCollision(float x, float y, float width, float height) {

        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE); row < Math.ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) (x / TileType.TILE_SIZE); col < Math.ceil((x + width) / TileType.TILE_SIZE); col++) {
                for (int layer = 0; layer < getLayers(); layer++) {
                    TileType type = getTileTypeByCoordinate(layer, col, row);
                    if (type != null && type.isCollidable()) {
                        if (type.getDps() != 0 && !player.isInvincible()) {
                            player.modifyHp(-type.getDps() * Gdx.graphics.getDeltaTime());
                        }
                        return true;
                    }
                }
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
                    enemies.moveX(enemies.getHorizontalSpeed() * -enemies.getFacingX());
                }
            }
        }
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

    @SuppressWarnings("WeakerAccess")
    public float getPixelWidth() {
        return this.getWidth() * TileType.TILE_SIZE;
    }

    @SuppressWarnings("WeakerAccess")
    public float getPixelHeight() {
        return this.getHeight() * TileType.TILE_SIZE;
    }

    public abstract int getLayers();

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract int getSpawnX();

    public abstract int getSpawnY();

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void render(SpriteBatch batch) {

    }
}
