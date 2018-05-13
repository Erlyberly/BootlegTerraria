package no.erlyberly.bootlegterraria.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.entities.weapons.Gun;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class Player extends Entity {

    private static final int HORIZONTAL_SPEED = 120;
    private static final int JUMP_VELOCITY = 500;
    private static final float DODGE_TIME = 0.50f;
    private static final float DODGE_COOLDOWN = 1f;
    private GameMap game;
    private int facingX = 1;
    private Weapon weapon = new Gun("Gun");
    private int maxHp = 10000; //Should be able to increase
    private float hp = 10000;
    private int maxStamina = 10000; //Should be able to increase
    private float stamina = 10000;
    private int staminaRegen = 30;
    private boolean invincible = false;
    private int dodgeSpeed = HORIZONTAL_SPEED * 5;
    private boolean dodging = false;
    private float dodgeTime = 0;
    private float dodgeCooldown = 0;

    private Texture image;
    private TextureRegion region;


    public boolean god = false;

    public Player(float x, float y, GameMap game) {
        super(x, y, game);
        image = new Texture("woofer.png");
        region = new TextureRegion(image);
        this.game = game;
    }

    public void update(float deltaTime, float gravity) {

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !dodging) {
            if (dodgeCooldown == DODGE_COOLDOWN && stamina - 2000 >= 0) {
                dodging = true;
                dodgeCooldown = DODGE_COOLDOWN;
                invincible = true;
                modifyStamina(-2000);
            }
        }

        if (dodging) {
            moveX(dodgeSpeed * facingX * deltaTime);
            dodgeTime += deltaTime;
            if (dodgeTime >= DODGE_TIME) {
                dodgeTime = 0;
                dodging = false;
                invincible = false;
            }
        }

        weapon.cooldown();
        modifyStamina(staminaRegen);
        if (dodgeCooldown < DODGE_COOLDOWN) {
            dodgeCooldown += deltaTime;
        }else{
            dodgeCooldown = DODGE_COOLDOWN;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && onGround && !dodging) {
            this.velocityY = 2;
            System.out.println(getVelocityY());
        }

        /*
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && !onGround && this.velocityY > 0 && !dodging) {
            this.velocityY += gravity / 4;
        }*/

        super.update(deltaTime, gravity);//Apply gravity

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !dodging) {
            moveX(-HORIZONTAL_SPEED * deltaTime);
            facingX = -1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !dodging) {
            moveX(HORIZONTAL_SPEED * deltaTime);
            facingX = 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.E) && !dodging) {
            if (stamina - weapon.getStaminaUsage() >= 0) {
                modifyStamina(weapon.use(game));
            }
        }

        //For testing purposes only
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            System.out.println("Zombie!");
            game.addEnemy(new Zombie(pos.x, pos.y + getHeight() * 2, game));
        }

    }

    public int getFacingX() {
        return facingX;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public float getHp() {
        return hp;
    }

    @Override
    public float getHorizontalSpeed() {
        return HORIZONTAL_SPEED;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void modifyHp(int amount) {
        if (god) {
            return;
        }
        if (this.hp > 0) {
            this.hp += amount;
        }

        if (this.hp <= 0) {
            this.hp = 0;
        }

        if (this.hp > this.maxHp) {
            this.hp = maxHp;
        }
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }

    public float getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void modifyStamina(int amount) {
        if (god) {
            return;
        }
        if (this.stamina >= -this.maxStamina) {
            this.stamina += amount;
        }

        if (this.stamina < -this.maxStamina) {
            this.stamina = -this.maxStamina;
        }

        if (this.stamina > this.maxStamina) {
            this.stamina = maxStamina;
        }
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
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
        return false;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(region.getTexture(), pos.x, pos.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1,
                   dodgeTime/DODGE_TIME * -facingX * 360 * 2, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
                   region.getRegionHeight(), facingX == -1, false);
    }

    @Override
    public int getDamage() {
        return 0;
    }
}
