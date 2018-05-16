package no.erlyberly.bootlegterraria.entities.weapons.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.entities.weapons.WeaponEntity;
import no.erlyberly.bootlegterraria.world.TileType;

public class Bullet extends WeaponEntity {

    private static final TextureRegion TEXTURE = new TextureRegion(new Texture("bullet.png"));

    private final float spawnX;


    public Bullet(float x, float y, int facing) {
        super(x, y, facing);
        this.spawnX = x;
    }

    @Override
    public void update() {
        float newX = getPos().x + getHorizontalSpeed() * getFacing() * Gdx.graphics.getDeltaTime();
        boolean colliding = gameMap.checkMapCollision(newX, this.pos.y, getWidth(), getHeight());
        boolean tooFarAway = Math.abs(this.pos.x - this.spawnX) > gameMap.getPlayer().getWidth() * 15;
        if (colliding || tooFarAway) {
            this.destroyed = true;
            gameMap.removeEntity();
        }
        moveX(getHorizontalSpeed() * getFacing());
    }

    @Override
    public float getWidth() {
        return TileType.TILE_SIZE / 1.5f;
    }

    @Override
    public float getHeight() {
        return TileType.TILE_SIZE * 1.2f;
    }

    @Override
    public float getHorizontalSpeed() {
        return 500;
    }

    @Override
    public float getDamage() {
        return getHorizontalSpeed() * 100;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(TEXTURE.getTexture(), this.pos.x, this.pos.y, getWidth() / 2, getHeight() / 2, getWidth(),
                   getHeight(), 1, 1, 0, TEXTURE.getRegionX(), TEXTURE.getRegionY(), TEXTURE.getRegionWidth(),
                   TEXTURE.getRegionHeight(), !isFacingRight(), false);
    }
}