package no.erlyberly.bootlegterraria.entities.living;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.entities.LivingEntity;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Random;

public class Zombie extends LivingEntity {

    private final static Texture HEALTH_BAR = new Texture("hp_fill.png");
    private final static Texture BAR_OUTLINE = new Texture("bar_outline.png");
    private final static TextureRegion TEXTURE = new TextureRegion(new Texture("zombie.png"));

    private static final Random RNG = new Random();
    private static final int JUMP_VELOCITY = 300;


    private final int horizontalSpeed;
    private boolean chasing;


    Zombie(final float x, final float y) {
        super(x, y);
        this.horizontalSpeed = 40 + RNG.nextInt(40);
        this.chasing = false;
    }

    @Override
    public void update() {

        super.update(); //Apply gravity

        final float horizontalDistanceFromPlayer = this.pos.x - this.gameMap.getPlayer().getPos().x;

        if (horizontalDistanceFromPlayer > this.gameMap.getPlayer().getWidth() / 2 &&
            horizontalDistanceFromPlayer < this.gameMap.getPlayer().getWidth() * 10) {
            moveX(-this.horizontalSpeed);
            setFacing(-1);
            this.chasing = true;
        }
        else if (horizontalDistanceFromPlayer < -this.gameMap.getPlayer().getWidth() / 2 &&
                 horizontalDistanceFromPlayer > -this.gameMap.getPlayer().getWidth() * 10) {
            moveX(this.horizontalSpeed);
            setFacing(1);
            this.chasing = true;
        }
        else {
            this.chasing = false;
        }

        if (this.gameMap
                .checkMapCollision(this.pos.x + (getWidth() - TileType.TILE_SIZE / 2) * getFacing() / 2, this.pos.y,
                                   getWidth(), getHeight()) && this.onGround && this.chasing) {
            this.velocityY += JUMP_VELOCITY;
        }
    }

    @Override
    public void modifyHp(final float amount) {
        if (this.health > 0) {
            this.health += amount;
        }

        if (this.health <= 0) {
            this.health = 0;
            this.destroyed = true;
            this.gameMap.removeEnemy();
        }

        if (this.health > this.getMaxHealth()) {
            this.health = this.getMaxHealth();
        }
    }

    public boolean isChasing() {
        return this.chasing;
    }

    @Override
    public float getHorizontalSpeed() {
        return this.horizontalSpeed;
    }

    @Override
    public float getDamage() {
        return 500;
    }

    @Override
    public boolean isFlying() {
        return false;
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
    public float getMaxHealth() {
        return 8000;
    }

    @Override
    public void render(final SpriteBatch batch) {

        startShade(batch);
        batch.draw(TEXTURE.getTexture(), this.pos.x, this.pos.y, getWidth() / 2, getHeight() / 2, getWidth(),
                   getHeight(), 1, 1, 0, TEXTURE.getRegionX(), TEXTURE.getRegionY(), TEXTURE.getRegionWidth(),
                   TEXTURE.getRegionHeight(), isFacingRight(), false);
        batch.draw(HEALTH_BAR, this.pos.x - getWidth() / 2f, this.pos.y + 34f,
                   (this.health / this.getMaxHealth() * getWidth() * 2f), 5f);
        batch.draw(BAR_OUTLINE, this.pos.x - getWidth() / 2f, this.pos.y + 34f, getWidth() * 2f, 5f);
        endShade(batch);
    }

}
