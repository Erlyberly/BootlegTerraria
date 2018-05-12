package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class Zombie extends Entity {

    private static final int HORIZONTAL_SPEED = 120;
    private static final int JUMP_VELOCITY = 4;
    private GameMap map;
    private int facingX = 1;
    private int maxHp = 10000; //Should be able to increase
    private int hp = 10000;

    private Texture image;

    public Zombie(float x, float y, GameMap map) {
        super(x, y, map);
        image = new Texture("zombie.png");
        this.map = map;
    }

    @Override
    public float getWeight() {
        return 70;
    }

    @Override
    public int getWidth() {
        return TileType.TILE_SIZE - 1;
    }

    @Override
    public int getHeight() {
        return 2 * TileType.TILE_SIZE;
    }

    @Override
    public boolean getDestroy() {
        return false;
    }

    @Override
    public void setDestroy(boolean destroy) {
        this.destroy  = destroy;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(image, pos.x, pos.y, getWidth(), getHeight());
    }
}
