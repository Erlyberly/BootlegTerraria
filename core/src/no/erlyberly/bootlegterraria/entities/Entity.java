package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.world.GameMap;

public abstract class Entity {

    Vector2 pos;
    float velocityY = 0;
    private static final int HORIZONTAL_SPEED = 0;
    private GameMap map;
    boolean onGround = false;
    boolean destroyed = false;
    private int facingX = 1;

    Entity(float x, float y, GameMap map) {
        this.pos = new Vector2(x, y);
        this.map = map;
    }

    public void update(float deltaTime, float gravity) {
        this.velocityY += gravity * deltaTime * getWeight();
        float deltaY = this.velocityY * deltaTime;

        if(this instanceof Player){
            map.checkPlayerMapCollision(pos.x, pos.y + deltaY, getWidth(), getHeight());
        }

        if (map.checkMapCollision(pos.x, pos.y + deltaY, getWidth(), getHeight())) {
            if (velocityY < 0) {
                this.pos.y = (float) Math.floor(pos.y);
                onGround = true;
            }
            this.velocityY = 0;
        }
        else {
            this.pos.y += deltaY;
            onGround = false;
        }
    }


    public void moveX(float amount) {
        float newX = pos.x + amount;
        if (!map.checkMapCollision(newX, pos.y, getWidth(), getHeight())) {
            this.pos.x = newX;
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

    public boolean isOnGround() {
        return onGround;
    }

    public abstract float getWeight();

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract boolean isDestroyed();

    public abstract void setDestroyed(boolean destroyed);

    public abstract void render(SpriteBatch batch);

    public abstract void modifyHp(int amount);

    public abstract int getDamage();

    public abstract int getHp();

    public abstract int getHorizontalSpeed();

    public abstract int getFacingX();
}
