package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.Player;

import java.util.ArrayList;

public abstract class GameMap {

    public final static float GRAVITY = -9.81f;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> addEntities;
    private boolean addWaiting = false;
    private boolean removeWaiting = false;
    private Player player;

    GameMap() {
        entities = new ArrayList<Entity>();
        addEntities = new ArrayList<Entity>();
        player = new Player(600, 600, this);
        entities.add(player);
    }

    public void addEntity(Entity entity){
        addEntities.add(entity);
        addWaiting = true;
    }

    public void removeEntity(){
        removeWaiting = true;
    }

    public void render(OrthographicCamera camera, OrthographicCamera hudCamera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity entity : entities) {
            entity.render(batch);
        }
        camera.position.x = player.getX();
        camera.position.y = player.getY();
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.draw(new Texture("hp_fill.png"), 7f + (getPlayer().getHp() / 100), Gdx.graphics.getHeight() / 2.1f, 20f, new Texture("hp_fill.png").getHeight());
        batch.draw(new Texture("hp_outline.png"), 5f, Gdx.graphics.getHeight() / 2.1f);
        batch.end();
    }

    @SuppressWarnings("Duplicates")
    public void update(float delta) {
        if(addWaiting){
            for(Entity add : addEntities) {
                entities.add(add);
            }
            addEntities.clear();
            addWaiting = false;
        }

        if(removeWaiting){
            ArrayList<Entity> removeEntities = new ArrayList<Entity>();
            for(Entity i : entities) {
                if(i.getDestroy()){
                    removeEntities.add(i);
                }
            }
            for(Entity remove : removeEntities){
                entities.remove(remove);
            }
            removeWaiting = false;
        }

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

    public Player getPlayer(){
        return player;
    }

    public void render(SpriteBatch batch){

    }
}
