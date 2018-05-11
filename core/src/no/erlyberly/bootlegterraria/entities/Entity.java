package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.world.GameMap;

public abstract class Entity {

    Vector2 pos;
    private EntityType type;
    float velocityY = 0;
    private GameMap map;
    boolean grounded = false;

    Entity(float x, float y, EntityType type, GameMap map) {
        this.pos = new Vector2(x, y);
        this.type = type;
        this.map = map;
    }

    public void update(float deltaTime, float gravity) {
        this.velocityY += gravity * deltaTime * getWeight();
        float deltaY = this.velocityY * deltaTime;

        if (map.checkMapCollision(pos.x, pos.y + deltaY, getWidth(), getHeight())) {
            if (velocityY < 0) {
                this.pos.y = (float) Math.floor(pos.y);
                grounded = true;
            }
            this.velocityY = 0;
        }
        else {
            this.pos.y += deltaY;
            grounded = false;
        }
    }

    float getWeight() {
        return type.getWeight();
    }

    int getWidth() {
        return type.getWidth();
    }

    int getHeight() {
        return type.getHeight();
    }

    public abstract void render(SpriteBatch batch);

    void moveX(float amount) {
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

    public EntityType getType() {
        return type;
    }

    public boolean isGrounded() {
        return grounded;
    }
}
