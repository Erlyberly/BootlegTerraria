package no.erlyberly.bootlegterraria.entities.weapons.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.entities.weapons.WeaponEntity;
import no.erlyberly.bootlegterraria.world.TileType;

import static no.erlyberly.bootlegterraria.GameMain.IMAGES_FOLDER;

public class Bullet extends WeaponEntity {

    private static final TextureRegion TEXTURE = new TextureRegion(new Texture(IMAGES_FOLDER + "bullet.png"));

    private final float spawnX;


    public Bullet(final float x, final float y, final int facing) {
        super(x, y, facing);
        spawnX = x;
    }

    @Override
    public void update() {
        final float newX = getPos().x + getHorizontalSpeed() * getFacing() * Gdx.graphics.getDeltaTime();
        final boolean colliding = gameMap.checkMapCollision(newX, pos.y, getWidth(), getHeight());
        final boolean tooFarAway = Math.abs(pos.x - spawnX) > gameMap.getPlayer().getWidth() * 15;
        if (colliding || tooFarAway) {
            destroyed = true;
            gameMap.cleanEntities();
            return;
        }
        moveX(getHorizontalSpeed() * getFacing());
    }

    @Override
    public float getWidth() {
        return TileType.TILE_SIZE / 1.5f;
    }

    @Override
    public float getHeight() {
        return TileType.TILE_SIZE / 2f;
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
        return true;
    }

    @Override
    public void render(final SpriteBatch batch) {
        batch.draw(TEXTURE.getTexture(), pos.x, pos.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, 0,
                   TEXTURE.getRegionX(), TEXTURE.getRegionY(), TEXTURE.getRegionWidth(), TEXTURE.getRegionHeight(),
                   !isFacingRight(), false);
    }
}
