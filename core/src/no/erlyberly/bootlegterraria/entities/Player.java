package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.world.GameMap;

public class Player extends Entity {

    private static final int SPEED = 120;
    private static final int JUMP_VELOCITY = 8;

    private Texture image;

    public Player(float x, float y, GameMap map) {
        super(x, y, map);
        image = new Texture("woofer.png");
    }

    public void update(float deltaTime, float gravity) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && grounded) {
            this.velocityY += JUMP_VELOCITY * getWeight();
        }

        else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !grounded && this.velocityY > 0) {
            this.velocityY += JUMP_VELOCITY * getWeight() * deltaTime;
        }

        super.update(deltaTime, gravity);//Apply gravity

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX(-SPEED * deltaTime);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX(SPEED * deltaTime);
        }

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
    public void render(SpriteBatch batch) {
        batch.draw(image, pos.x, pos.y, getWidth(), getHeight());
    }
}
