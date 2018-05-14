package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Random;

public class Zombie extends Entity {

    private Texture hpBar = new Texture("hp_fill.png");
    private Texture barOutline = new Texture("bar_outline.png");

    private static Random rng = new Random();
    private int HORIZONTAL_SPEED = (40 + rng.nextInt(40));
    private static final int JUMP_VELOCITY = 300;
    private GameMap map;
    private int facingX = 1;
    private int maxHp = 10000; //Should be able to increase
    private int hp = maxHp;
    private boolean chasing = false;

    private TextureRegion region;

    Zombie(float x, float y, GameMap map) {
        super(x, y, map);
        Texture image = new Texture("zombie.png");
        region = new TextureRegion(image);
        this.map = map;
    }

    public void update() {

        super.update();//Apply gravity

        float horizontalDistanceFromPlayer = pos.x - map.getPlayer().getPos().x;

        if (horizontalDistanceFromPlayer > map.getPlayer().getWidth() / 2 &&
            horizontalDistanceFromPlayer < map.getPlayer().getWidth() * 10) {
            moveX(-HORIZONTAL_SPEED);
            facingX = -1;
            chasing = true;
        }
        else if (horizontalDistanceFromPlayer < -map.getPlayer().getWidth() / 2 &&
                 horizontalDistanceFromPlayer > -map.getPlayer().getWidth() * 10) {
            moveX(HORIZONTAL_SPEED);
            facingX = 1;
            chasing = true;
        }
        else {
            chasing = false;
        }

        if (map.checkMapCollision(pos.x + (getWidth() - TileType.TILE_SIZE / 2) * facingX / 2, pos.y, getWidth(),
                                  getHeight()) && onGround && chasing) {
            this.velocityY += JUMP_VELOCITY;
        }
    }

    public void modifyHp(float amount) {
        if (this.hp > 0) {
            this.hp += amount;
        }

        if (this.hp <= 0) {
            this.hp = 0;
            this.destroyed = true;
            map.removeEnemy();
        }

        if (this.hp > this.maxHp) {
            this.hp = maxHp;
        }
    }

    public boolean isChasing() {
        return chasing;
    }

    public float getHp() {
        return hp;
    }

    @Override
    public float getHorizontalSpeed() {
        return HORIZONTAL_SPEED;
    }

    @Override
    public int getFacingX() {
        return facingX;
    }

    @Override
    public int getDamage() {
        return 300;
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
        batch.draw(region.getTexture(), pos.x, pos.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, 0,
                   region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(),
                   facingX == 1, false);
        batch.draw(hpBar, pos.x - getWidth() / 2f, pos.y + 34f, (((float) hp / (float) maxHp) * getWidth() * 2f), 5f);
        batch.draw(barOutline, pos.x - getWidth() / 2f, pos.y + 34f, getWidth() * 2f, 5f);
    }

}
