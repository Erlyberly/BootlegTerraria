package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.Player;

import java.util.ArrayList;

public abstract class GameMap {

    private final static float GRAVITY = -20f;

    private ArrayList<Entity> entities;
    private Player player;

    GameMap() {
        entities = new ArrayList<Entity>();
        player = new Player(600, 600, this);
        entities.add(player);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);
        }
        camera.position.x = player.getX();
        camera.position.y = player.getY();
    }

    public void update(float delta) {
        for (Entity entity : entities) {
            entity.update(delta, GRAVITY);
        }
    }

    public abstract void dispose();

    public TileType getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    public abstract TileType getTileTypeByCoordinate(int layer, int col, int row);

    public boolean checkMapCollision(float x, float y, int width, int height) {

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

    @SuppressWarnings("WeakerAccess")
    public int getPixelWidth() {
        return this.getWidth() * TileType.TILE_SIZE;
    }

    @SuppressWarnings("WeakerAccess")
    public int getPixelHeight() {
        return this.getHeight() * TileType.TILE_SIZE;
    }

    public abstract int getLayers();

    public abstract int getWidth();

    public abstract int getHeight();
}
