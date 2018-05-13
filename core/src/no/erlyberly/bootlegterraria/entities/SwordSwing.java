package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class SwordSwing extends Entity {

    private int facingX;
    private static final int HORIZONTAL_SPEED = 10;
    public static final float SWORD_LENGTH = TileType.TILE_SIZE * 5f;
    private GameMap map;
    private int damage = 3000;
    private int frames = 30;

    private Texture image;

    public SwordSwing(float x, float y, GameMap map, int facingX) {
        super(x, y, map);
        this.facingX = facingX;
        this.map = map;
        if (facingX == 1) {
            image = new Texture("sword.png");
        }
        else {
            image = new Texture("sword.png");
        }
    }

    public void update(float deltaTime, float gravity) {
        //System.out.println(frames);
        frames--;
        if (frames <= 0) {
            //System.out.println("Destroy sword");
            this.destroyed = true;
            map.removeEntity();
        }
    }

    @Override
    public float getWidth() {
        return SWORD_LENGTH;
    }

    @Override
    public float getHeight() {
        return TileType.TILE_SIZE * 1.5f;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion region = new TextureRegion(image);
        if (facingX == 1) {
            batch.draw(region, map.getPlayer().getX() + map.getPlayer().getWidth() / 2, map.getPlayer().getY(), 0,
                       getHeight() / 2, getWidth(), getHeight(), 1, 1, frames * 3f - 20);
        }
        else {
            batch.draw(region, map.getPlayer().getX() + map.getPlayer().getWidth() / 2, map.getPlayer().getY(), 0,
                       getHeight() / 2, getWidth(), getHeight(), 1, 1, -frames * 3f + 200);
        }
    }

    @Override
    public void modifyHp(int amount) {

    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public float getHp() {
        return 0;
    }

    @Override
    public float getHorizontalSpeed() {
        return 0;
    }

    @Override
    public int getFacingX() {
        return 0;
    }
}
