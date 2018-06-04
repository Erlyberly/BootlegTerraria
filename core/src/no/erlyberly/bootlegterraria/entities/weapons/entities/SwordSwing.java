package no.erlyberly.bootlegterraria.entities.weapons.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.entities.weapons.WeaponEntity;
import no.erlyberly.bootlegterraria.world.TileType;

public class SwordSwing extends WeaponEntity {

    public static final float SWORD_LENGTH = TileType.TILE_SIZE * 5f;
    private static final TextureRegion TEXTURE = new TextureRegion(new Texture("sword.png"));

    private float attackTime = 0.5f;


    public SwordSwing(float x, float y, int facing) {
        super(x, y, facing);
    }

    @Override
    public void update() {
        this.attackTime -= Gdx.graphics.getRawDeltaTime();
        if (this.attackTime <= 0) {
            this.destroyed = true;
            this.gameMap.removeEntity();
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
    public void render(SpriteBatch batch) {

        float rotation;
        if (isFacingRight()) { rotation = this.attackTime * 180f - 20; }
        else { rotation = -this.attackTime * 180f + 200; }

        batch.draw(TEXTURE, this.gameMap.getPlayer().getX() + this.gameMap.getPlayer().getWidth() / 2,
                   this.gameMap.getPlayer().getY(), 0, getHeight() / 2, getWidth(), getHeight(), 1, 1, rotation);
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
