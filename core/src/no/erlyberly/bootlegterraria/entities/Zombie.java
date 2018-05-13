package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.helpers.GameInfo;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class Zombie extends Entity {

    Texture hpBar = new Texture("hp_fill.png");
    Texture barOutline = new Texture("bar_outline.png");

    private static java.util.Random rng = new java.util.Random();
    private int HORIZONTAL_SPEED = (40 + rng.nextInt(40));
    private static final int JUMP_VELOCITY = 3;
    private GameMap map;
    private int facingX = 1;
    private int maxHp = 10000; //Should be able to increase
    private int hp = 10000;
    private boolean invincible = false;
    private boolean dodging = false;
    private int dodgeFrames = 0;
    private boolean chasing = false;

    private TextureRegion region;
    private Texture image;

    public Zombie(float x, float y, GameMap map) {
        super(x, y, map);
        image = new Texture("zombie.png");
        region = new TextureRegion(image);
        this.map = map;
    }

    public void update(float deltaTime, float gravity){

        super.update(deltaTime, gravity);//Apply gravity

        float distanceFromPLayer = pos.x - map.getPlayer().getPos().x;

        if(distanceFromPLayer > map.getPlayer().getWidth()/2 && distanceFromPLayer < map.getPlayer().getWidth() * 10){
            moveX(-HORIZONTAL_SPEED * deltaTime);
            facingX = -1;
            chasing = true;
        }else if(distanceFromPLayer < -map.getPlayer().getWidth()/2 && distanceFromPLayer > -map.getPlayer().getWidth() * 10){
            moveX(HORIZONTAL_SPEED * deltaTime);
            facingX = 1;
        }else{
            chasing = false;
        }

        if(map.checkMapCollision(pos.x + getWidth() * facingX / 2, pos.y, getWidth(), getHeight()) && onGround && chasing){
            this.velocityY += JUMP_VELOCITY * getWeight();
            chasing = true;
        }
    }

    public void modifyHp(int amount) {
        if(this.hp > 0) {
            this.hp += amount;
        }

        if(this.hp <= 0){
            this.hp = 0;
            this.destroyed = true;
            map.removeEnemy();
        }

        if(this.hp > this.maxHp){
            this.hp = maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    @Override
    public int getHorizontalSpeed() {
        return HORIZONTAL_SPEED;
    }

    @Override
    public int getFacingX() {
        return facingX;
    }

    @Override
    public int getDamage() {
        return 5;
    }

    @Override
    public float getWeight() {
        return 70;
    }

    @Override
    public float getWidth() {
        return TileType.TILE_SIZE - 1;
    }

    @Override
    public float getHeight() {
        return 2 * TileType.TILE_SIZE;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(region.getTexture(), pos.x, pos.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1,
                dodgeFrames * facingX * 360 / 14, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), facingX == 1, false);
        batch.draw(hpBar, pos.x - getWidth() / 2f, pos.y + 34f, (((float)hp / (float)maxHp) * getWidth() * 2f), 5f);
        batch.draw(barOutline, pos.x - getWidth() / 2f, pos.y + 34f, getWidth() * 2f, 5f);
    }

}
