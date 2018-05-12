package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class Bullet extends Entity {

    private int facingX;
    private static final int SPEED = 10;
    private GameMap map;
    private int damage = 1000;

    private Texture image;

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
            this.destroyed = true;
            map.removeEntity();
        }
    }

    @Override
    public float getWeight() {
        return 0.5f;
    }

    @Override
    public float getWidth() {
        return Math.round((float)TileType.TILE_SIZE / 1.5f);
    }

    @Override
    public float getHeight() {
        return Math.round((float)TileType.TILE_SIZE * 1.2f);
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public void setDestroyed(boolean destroyed){
        this.destroyed = destroyed;
    }

    public int getDamage(){
        return damage;
    }

    @Override
    public int getHp() {
        return 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(image, pos.x, pos.y, getWidth(), getHeight());
    }

    @Override
    public void modifyHp(int amount) {

    }

}