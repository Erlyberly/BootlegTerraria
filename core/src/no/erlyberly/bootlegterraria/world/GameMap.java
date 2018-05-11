package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.Player;

import java.util.ArrayList;

public abstract class GameMap {

    protected ArrayList<Entity> entities;
    private final float GRAVITY = -20f;

    public GameMap(){
        entities = new ArrayList<Entity>();
        entities.add(new Player(600, 600, this));
    }

    public void render (OrthographicCamera camera, SpriteBatch batch){
        for(Entity entity : entities){
            entity.render(batch);
        }
    }

    public void update (float delta){
        for(Entity entity : entities){
            entity.update(delta, GRAVITY);
        }
    }

    public abstract void dispose();

    public TileType getTileTypeByLocation(int layer, float x, float y){
        return this.getTileTypeByCoordinate(layer,
                (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    public abstract TileType getTileTypeByCoordinate(int layer, int col, int row);

    public boolean checkMapCollid(float x, float y, int width, int height){

        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            //System.out.println(width);
            //System.out.println(getPixelWidth() + ", " + getPixelHeight());
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

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getLayers();

    public int getPixelWidth(){
        return this.getWidth() * TileType.TILE_SIZE;
    }

    public int getPixelHeight(){
        return this.getHeight() * TileType.TILE_SIZE;
    }
}
