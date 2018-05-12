package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.entities.weapons.Gun;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class Player extends Entity {

    private static final int HORIZONTAL_SPEED = 120;
    private static final int JUMP_VELOCITY = 4;
    private GameMap map;
    private int facingX = 1;
    private Weapon weapon = new Gun();

    private Texture image;

    public Player(float x, float y, GameMap map) {
        super(x, y, map);
        image = new Texture("wooferR.png");
        this.map = map;
    }

    public void update(float deltaTime, float gravity) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && onGround) {
            this.velocityY += JUMP_VELOCITY * getWeight();
        }

        else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !onGround && this.velocityY > 0) {
            this.velocityY += JUMP_VELOCITY * getWeight() * deltaTime;
        }

        super.update(deltaTime, gravity);//Apply gravity

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX(-HORIZONTAL_SPEED * deltaTime);
            image = new Texture("wooferL.png");
            facingX = -1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX(HORIZONTAL_SPEED * deltaTime);
            image = new Texture("wooferR.png");
            facingX = 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.E)){
            weapon.use(map);
        }

    }

    public int getFacingX() {
        return facingX;
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
