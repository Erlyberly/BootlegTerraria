package no.erlyberly.bootlegterraria.entities.weapons.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.entities.weapons.WeaponEntity;
import no.erlyberly.bootlegterraria.world.TileType;

import static no.erlyberly.bootlegterraria.GameMain.IMAGES_FOLDER;

public class SwordSwing extends WeaponEntity {

    public static final float SWORD_LENGTH = TileType.TILE_SIZE * 5f;
    private static final TextureRegion TEXTURE = new TextureRegion(new Texture(IMAGES_FOLDER + "sword.png"));

    private float attackTime = 0.5f;


    public SwordSwing(final float x, final float y, final int facing) {
        super(x, y, facing);
    }

    @Override
    public void update() {
        attackTime -= Gdx.graphics.getRawDeltaTime();
        if (attackTime <= 0) {
            destroyed = true;
            gameMap.removeEntity();
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
    public void render(final SpriteBatch batch) {

        final float rotation;
        if (isFacingRight()) { rotation = attackTime * 180f - 20; }
        else { rotation = -attackTime * 180f + 200; }

        batch.draw(TEXTURE, gameMap.getPlayer().getX() + gameMap.getPlayer().getWidth() / 2, gameMap.getPlayer().getY(), 0,
                   getHeight() / 2, getWidth(), getHeight(), 1, 1, rotation);
    }

    @Override
    public float getHorizontalSpeed() {
        return 0;
    }

    @Override
    public float getDamage() {
        return 5000;
    }

    @Override
    public boolean isFlying() {
        return false;
    }
}
