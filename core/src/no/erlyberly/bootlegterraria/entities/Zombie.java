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
    private boolean invincible = false;
    private boolean dodging = false;
    private int dodgeFrames = 0;

    private Texture image;

    public Zombie(float x, float y, GameMap map) {
        super(x, y, map);
        image = new Texture("zombie.png");
        this.map = map;
    }

    public void modifyHp(int amount) {
        if(this.hp > 0) {
            this.hp += amount;
        }

        if(this.hp <= 0){
            this.hp = 0;
            this.destroyed = true;
            map.removeEnemy();
        }

        if(this.hp > this.maxHp){
            this.hp = maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    @Override
    public int getDamage() {
        return 5;
    }

    @Override
    public float getWeight() {
        return 70;
    }

    @Override
    public float getWidth() {
        return TileType.TILE_SIZE - 1;
    }

    @Override
    public float getHeight() {
        return 2 * TileType.TILE_SIZE;
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
        batch.draw(image, pos.x, pos.y, getWidth(), getHeight());
    }
}
