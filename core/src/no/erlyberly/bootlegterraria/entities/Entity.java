package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.world.GameMap;

public abstract class Entity {

    Vector2 pos;
    float velocityY = 0;
    private static final int HORIZONTAL_SPEED = 0;
    private GameMap gameMap;
    boolean onGround = false;
    boolean destroyed = false;
    private int facingX;

    Entity(float x, float y, GameMap gameMap) {
        this.pos = new Vector2(x, y);
        this.gameMap = gameMap;
    }

    public void update(float deltaTime) {

        System.out.println(deltaTime);
        velocityY += GameMap.GRAVITY * deltaTime;
        float newY = pos.y + velocityY;

        if (this instanceof Player) {
            gameMap.checkPlayerMapCollision(pos.x, newY, getWidth(), getHeight());
        }

        if (gameMap.checkMapCollision(pos.x, newY, getWidth(), getHeight())) {
            if (velocityY < 0) {
                pos.y = (float) Math.floor(pos.y);
                onGround = true;
            }
            this.velocityY = 0;
        } else {
            pos.y = newY;
            onGround = false;
        }
    }


    public void moveX(float velocityX) {
        float newX = pos.x + velocityX;
        if (!gameMap.checkMapCollision(newX, pos.y, getWidth(), getHeight())) {
            pos.x = newX;
        }
    }

    public Vector2 getPos() {
        return pos;
    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract boolean isDestroyed();

    public abstract void setDestroyed(boolean destroyed);

    public abstract void render(SpriteBatch batch);

    public abstract void modifyHp(int amount);

    public abstract int getDamage();

    public abstract float getHp();

    public abstract float getHorizontalSpeed();

    public abstract int getFacingX();
}
