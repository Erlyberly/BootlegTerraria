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
    private static final int HORIZONTAL_SPEED = 10;
    private GameMap map;
    private int damage = 1000;

    private TextureRegion region;
    private Texture image;

    public Bullet(float x, float y, GameMap map, int facingX) {
        super(x, y, map);
        this.facingX = facingX;
        this.map = map;
        this.image = new Texture("bulletR.png");
        this.region = new TextureRegion(image);
    }

    public void update(float deltaTime, float gravity) {
        moveX(HORIZONTAL_SPEED * facingX);
        float newX = pos.x + HORIZONTAL_SPEED * facingX;
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
    public int getHorizontalSpeed() {
        return HORIZONTAL_SPEED;
    }

    @Override
    public int getFacingX() {
        return 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(region.getTexture(), pos.x, pos.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1,
                0, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), facingX == -1, false);
    }

    @Override
    public void modifyHp(int amount) {

    }

}