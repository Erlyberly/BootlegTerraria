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


    public Bullet(final float x, final float y, final int facing) {
        super(x, y, facing);
        this.spawnX = x;
    }

    @Override
    public void update() {
        final float newX = getPos().x + getHorizontalSpeed() * getFacing() * Gdx.graphics.getDeltaTime();
        final boolean colliding = this.gameMap.checkMapCollision(newX, this.pos.y, getWidth(), getHeight());
        final boolean tooFarAway = Math.abs(this.pos.x - this.spawnX) > this.gameMap.getPlayer().getWidth() * 15;
        if (colliding || tooFarAway) {
            this.destroyed = true;
            this.gameMap.removeEntity();
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
    public boolean isFlying() {
        return false;
    }

    @Override
    public void render(final SpriteBatch batch) {
        batch.draw(TEXTURE.getTexture(), this.pos.x, this.pos.y, getWidth() / 2, getHeight() / 2, getWidth(),
                   getHeight(), 1, 1, 0, TEXTURE.getRegionX(), TEXTURE.getRegionY(), TEXTURE.getRegionWidth(),
                   TEXTURE.getRegionHeight(), !isFacingRight(), false);
    }
}