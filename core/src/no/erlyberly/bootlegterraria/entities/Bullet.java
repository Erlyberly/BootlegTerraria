package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class Bullet extends Entity {

    private Texture image;
    private int facingX;
    private static final int SPEED = 10;
    private GameMap map;

    public Bullet(float x, float y, GameMap map, int facingX) {
        super(x, y, map);
        this.facingX = facingX;
        this.map = map;
        if(facingX == 1) {
            image = new Texture("bulletR.png");
        } else{
            image = new Texture("bulletL.png");
        }
    }

    public void update(float deltaTime, float gravity) {
        moveX(SPEED * facingX);
        float newX = pos.x + SPEED * facingX;
        if (map.checkMapCollision(newX, pos.y, getWidth(), getHeight())) {
            this.destroy = true;
            map.removeEntity();
        }
    }

    @Override
    public float getWeight() {
        return 0.5f;
    }

    @Override
    public int getWidth() {
        return Math.round((float)TileType.TILE_SIZE / 1.5f);
    }

    @Override
    public int getHeight() {
        return Math.round((float)TileType.TILE_SIZE * 1.2f);
    }

    public boolean getDestroy(){
        return destroy;
    }

    public void setDestroy(boolean destroy){
        this.destroy = destroy;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(image, pos.x, pos.y, getWidth(), getHeight());
    }

}