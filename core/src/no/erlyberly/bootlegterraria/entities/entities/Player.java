package no.erlyberly.bootlegterraria.entities.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.entities.weapons.weapons.Gun;
import no.erlyberly.bootlegterraria.world.TileType;

public class Player extends Entity {

    private static final int HORIZONTAL_SPEED = 120;
    private static final int JUMP_VELOCITY = 400;
    private static final float DODGE_TIME = 0.5f;
    private static final float DODGE_COOLDOWN = 1f;
    private static final float DODGE_SPEED = HORIZONTAL_SPEED * 2.5f;
    private static final int DEFAULT_MAX_HEALTH = 10000;

    private Weapon weapon = new Gun();
    private int maxStamina = 10000; //Should be able to increase
    private float stamina = this.maxStamina;
    private final int staminaRegen = 2000;

    private boolean invincible = false;

    private boolean dodging = false;
    private float dodgeTime = 0;
    private float dodgeCooldown = 0;
    private final float dodgeStaminaUsage = 3000;


    private static final TextureRegion PLAYER_TEXTURE = new TextureRegion(new Texture("ErlyBerly_TheGreat.png"));
    private static final TextureRegion HURT_TEXTURE = new TextureRegion(new Texture("ErlyBerly_TheGreat_hurt.png"));

    private boolean isHurt;
    private boolean flying;

    public boolean god = false;
    public float speed = 1;

    public Player(final float x, final float y) {
        super(x, y);
    }

    @Override
    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !this.dodging) {
            if (this.dodgeCooldown == DODGE_COOLDOWN && this.stamina - this.dodgeStaminaUsage >= 0) {
                this.dodging = true;
                this.dodgeCooldown = 0;
                this.invincible = true;
                modifyStamina(-this.dodgeStaminaUsage);
            }
        }

        if (this.dodging) {
            moveX(DODGE_SPEED * getFacing());
            this.dodgeTime += Gdx.graphics.getDeltaTime();
            if (this.dodgeTime >= DODGE_TIME) {
                this.dodgeTime = 0;
                this.dodging = false;
                this.invincible = false;
            }
        }

        this.weapon.cooldown();
        modifyStamina(this.staminaRegen * Gdx.graphics.getDeltaTime());
        if (this.dodgeCooldown < DODGE_COOLDOWN) {
            this.dodgeCooldown += Gdx.graphics.getDeltaTime();
        }
        else {
            this.dodgeCooldown = DODGE_COOLDOWN;
        }

        final float speed = this.speed * HORIZONTAL_SPEED;

        if (isFlying()) {

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                moveY(speed);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                moveY(-speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !this.dodging) {
                moveX(-speed);
                setFacing(-1);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !this.dodging) {
                moveX(speed);
                setFacing(1);
            }
        }
        else {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && this.onGround && !this.dodging) {
                this.velocityY = JUMP_VELOCITY;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !this.dodging) {
                moveX(-speed);
                setFacing(-1);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !this.dodging) {
                moveX(speed);
                setFacing(1);
            }
            super.update();//Apply gravity
        }


        if (Gdx.input.isKeyPressed(Input.Keys.E) && !this.dodging &&
            this.stamina - this.weapon.getStaminaUsage() >= 0 && this.weapon.isCooledDown()) {

            this.weapon.attack(this);
            modifyStamina(-this.weapon.getStaminaUsage());
        }

        //For testing purposes only
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            System.out.println("Zombie!");
            this.gameMap.addEnemy(new Zombie(this.pos.x, this.pos.y + getHeight() * 2));
        }
    }

    @Override
    public float getHorizontalSpeed() {
        return HORIZONTAL_SPEED * this.speed;
    }

    @Override
    public float getMaxHealth() {
        return DEFAULT_MAX_HEALTH;
    }

    @Override
    public void modifyHp(final float amount) {
        if (this.god) {
            return;
        }

        if (amount < 0) { this.isHurt = true; }

        if (this.health > 0) {
            this.health += amount;
        }

        if (this.health <= 0) {
            this.health = 0;
        }

        if (this.health > this.getMaxHealth()) {
            this.health = this.getMaxHealth();
        }
    }

    public int getMaxStamina() {
        return this.maxStamina;
    }

    public void setMaxStamina(final int maxStamina) {
        this.maxStamina = maxStamina;
    }

    public float getStamina() {
        return this.stamina;
    }

    public void setStamina(final int stamina) {
        this.stamina = stamina;
    }

    public void modifyStamina(final float amount) {
        if (this.god) {
            return;
        }

        if (this.stamina >= -this.maxStamina) {
            this.stamina += amount;
        }

        if (this.stamina > this.maxStamina) {
            this.stamina = this.maxStamina;
        }
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public void setWeapon(final Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public void setInvincible(final boolean invincible) {
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

    private static final float ANIMATION_DIRECTION_DURATION = 0.3f;
    private float animationTime = 0;

    @Override
    public void render(final SpriteBatch batch) {
        int direction = getFacing();

        //""animation"" of the player, flips the sprite back and forth on a time scale put by
        // ANIMATION_DIRECTION_DURATION
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.animationTime += Gdx.graphics.getRawDeltaTime();
            if (ANIMATION_DIRECTION_DURATION >= this.animationTime) {
                direction *= -1;
            }
            else if (ANIMATION_DIRECTION_DURATION * 2 < this.animationTime) {
                this.animationTime = 0;
            }
        }

        TextureRegion region = PLAYER_TEXTURE;
        if (this.isHurt) {
            this.isHurt = false;
            region = HURT_TEXTURE;
        }


        batch
            .draw(region.getTexture(), this.pos.x, this.pos.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(),
                  1, 1, this.dodgeTime / DODGE_TIME * -getFacing() * 360 * 2, region.getRegionX(), region.getRegionY(),
                  region.getRegionWidth(), region.getRegionHeight(), direction != -1, false);

    }

    @Override
    public float getDamage() {
        return 0;
    }

    @Override
    public boolean isFlying() {
        return this.flying;
    }

    public void setFlying(final boolean flying) {
        this.flying = flying;
    }
}
