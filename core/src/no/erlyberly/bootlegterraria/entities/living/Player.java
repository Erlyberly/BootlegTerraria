package no.erlyberly.bootlegterraria.entities.living;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.LivingEntity;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.entities.weapons.weapons.Gun;
import no.erlyberly.bootlegterraria.input.InputSetting;
import no.erlyberly.bootlegterraria.input.event.EventType;
import no.erlyberly.bootlegterraria.input.event.metadata.MouseMetadata;
import no.erlyberly.bootlegterraria.input.event.metadata.ScrolledMetadata;
import no.erlyberly.bootlegterraria.storage.IInventory;
import no.erlyberly.bootlegterraria.storage.impl.Inventory;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

public class Player extends LivingEntity {

    private static final int HORIZONTAL_SPEED = 120;
    private static final int JUMP_VELOCITY = 400;
    private static final int DEFAULT_MAX_HEALTH = 10000;
    private static final int FACING_LEFT = -1;
    private static final int FACING_RIGHT = 1;
    private static final float DODGE_TIME = 0.5f;
    private static final float DODGE_COOLDOWN = 1f;
    private static final float DODGE_SPEED = HORIZONTAL_SPEED * 2.5f;

    private static final TextureRegion PLAYER_TEXTURE = new TextureRegion(new Texture("ErlyBerly_TheGreat.png"));
    private static final TextureRegion HURT_TEXTURE = new TextureRegion(new Texture("ErlyBerly_TheGreat_hurt.png"));

    public boolean god = false; //is the player in god mode?
    public float speedModifier = 1; //how fast is the player going (2 is twice as fast)
    public int staminaRegen = 2000;
    public int maxStamina = 10000;
    public float dodgeStaminaUsage = 3000;

    private final float calculatedSpeed = HORIZONTAL_SPEED * this.speedModifier;
    private float stamina = this.maxStamina; //player start with max stamina

    private Weapon weapon;
    private boolean invincible = false;
    private boolean dodging = false;
    private boolean flying = false; //can the player fly?
    private float dodgeTime = 0f;
    private float dodgeCooldown = 0f;
    private int hurtTimer = 0; //For how long should the player be hurt?

    static {
        /*
         * handle the scrolling to change selected block
         */
        GameMain.input.registerListener(metadata -> {
            final ScrolledMetadata scroll = (ScrolledMetadata) metadata;
            final IInventory inv = GameMain.map.getPlayer().getInv();

            if (scroll.amount < 0) {
                inv.next();
            }
            else if (scroll.amount > 0) {
                inv.prev();
            }
        }, EventType.SCROLLED);

        /*
         * Debug the given tile
         */
        GameMain.input.registerListener(metadata -> {
            final MouseMetadata mMeta = (MouseMetadata) metadata;
            final GameMap map = GameMain.map;
            final TileType type = map.getTileTypeByLocation(map.getBlockLayer(), mMeta.screenX, mMeta.screenY);

            final int blockX = (int) (mMeta.screenX / TileType.TILE_SIZE);
            final int blockY = (int) (mMeta.screenY / TileType.TILE_SIZE);

            if (type != null) {
                GameMain.console.log(
                    "Tile clicked: " + type.getName() + ", id: " + type.getId() + ", dmg: " + type.getDps() +
                    " coord: (" + blockX + ", " + blockY + ")");
            }
            else {
                GameMain.console.log("Not a tile");
            }
        }, EventType.TOUCH_DOWN, Input.Keys.ALT_LEFT);

        /*
         * break blocks
         */
        GameMain.input.registerListener(metadata -> {
            final GameMap map = GameMain.map;
            final Vector3 pos = GameMain.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            final int blockX = (int) (pos.x / TileType.TILE_SIZE);
            final int blockY = (int) (pos.y / TileType.TILE_SIZE);

            map.setTile(blockX, blockY, null);
        }, InputSetting.BREAK_BLOCK);

        /*
         * Place blocks
         */
        GameMain.input.registerListener(metadata -> {
            final GameMap map = GameMain.map;
            final Player player = map.getPlayer();

            if (player.getInv().holding() == null) {
                return;
            }

            final Vector3 pos = GameMain.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            final int blockX = (int) (pos.x / TileType.TILE_SIZE);
            final int blockY = (int) (pos.y / TileType.TILE_SIZE);

            map.setTile(blockX, blockY, player.getInv().holding().getTileType());
        }, InputSetting.PLACE_BLOCK);

        /*
         * Move left
         */
        GameMain.input.registerListener(md -> {
            final Player player = GameMain.map.getPlayer();
            if (!player.dodging) {
                player.moveX(-player.calculatedSpeed);
                player.setFacing(FACING_LEFT);
            }
        }, InputSetting.MOVE_LEFT);

        /*
         * Move right
         */
        GameMain.input.registerListener(md -> {
            final Player player = GameMain.map.getPlayer();
            if (!player.dodging) {
                player.moveX(player.calculatedSpeed);
                player.setFacing(FACING_RIGHT);
            }
        }, InputSetting.MOVE_RIGHT);

        /*
         * Dodging
         */
        GameMain.input.registerListener(md -> {
            final Player player = GameMain.map.getPlayer();
            if (!player.dodging && player.dodgeCooldown == DODGE_COOLDOWN &&
                player.stamina - player.dodgeStaminaUsage >= 0) {

                player.dodging = true;
                player.dodgeCooldown = 0;
                player.invincible = true;
                player.modifyStamina(-player.dodgeStaminaUsage);
            }
        }, InputSetting.DODGE);

        /*
         * Jump and fly up
         */
        GameMain.input.registerListener(md -> {
            final Player player = GameMain.map.getPlayer();
            if (player.isFlying()) {
                player.moveY(player.calculatedSpeed);
            }
            else if (player.onGround && !player.dodging) {
                player.velocityY = JUMP_VELOCITY;
            }
        }, InputSetting.JUMP);

        /*
         * Fly down
         */
        GameMain.input.registerListener(md -> {
            final Player player = GameMain.map.getPlayer();
            if (player.isFlying()) {
                player.moveY(-player.calculatedSpeed);
            }
        }, InputSetting.FLY_DOWN);

        /*
         * Attack
         */
        GameMain.input.registerListener(md -> {
            final Player player = GameMain.map.getPlayer();
            final Weapon weapon = player.getWeapon();
            if (!player.dodging && player.stamina - weapon.getStaminaUsage() >= 0 && weapon.isCooledDown()) {
                weapon.attack(player);
                player.modifyStamina(-weapon.getStaminaUsage());
            }
        }, InputSetting.ATTACK);

        /*
         * Debug input
         */
        GameMain.input.registerListener(md -> {
            System.out.println("Zombie!");
            final GameMap map = GameMain.map;
            final Player player = map.getPlayer();
            map.addEnemy(new Zombie(player.pos.x, player.pos.y + player.getHeight() * 2));
        }, EventType.KEY_DOWN, Input.Keys.Z);

        GameMain.input.registerListener(md -> {
            final IInventory inv = GameMain.map.getPlayer().getInv();
            if (inv.isOpen()) { inv.close(); }
            else { inv.open(); }
        }, InputSetting.INVENTORY);

    }

    public Player(final float x, final float y) {
        super(x, y);

        this.weapon = new Gun();
        setInv(new Inventory(this, 40));
    }

    @Override
    public void update() {
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

        //only apply gravity when the player is not flying
        if (!isFlying()) {
            super.update();
        }

    }

    @Override
    public float getHorizontalSpeed() {
        return HORIZONTAL_SPEED * this.speedModifier;
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

        if (amount < 0) {
            //let the player display the hurt sprite for quarter-(ish) of a second
            this.hurtTimer = Gdx.graphics.getFramesPerSecond() / 4;
        }

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
        return TileType.TILE_SIZE - 2f;
    }

    @Override
    public float getHeight() {
        return 2 * TileType.TILE_SIZE - 1;
    }

    private static final float ANIMATION_DIRECTION_DURATION = 0.3f;
    private float animationTime = 0;

    @Override
    public void render(final SpriteBatch batch) {
        int direction = getFacing();

        //""animation"" of the player, flips the sprite back and forth on a time scale put by
        // ANIMATION_DIRECTION_DURATION
        if (InputSetting.MOVE_LEFT.isKeyPressed() || InputSetting.MOVE_RIGHT.isKeyPressed()) {
            this.animationTime += Gdx.graphics.getRawDeltaTime();
            if (ANIMATION_DIRECTION_DURATION >= this.animationTime) {
                direction *= -1;
            }
            else if (ANIMATION_DIRECTION_DURATION * 2 < this.animationTime) {
                this.animationTime = 0;
            }
        }

        TextureRegion region = PLAYER_TEXTURE;
        if (this.hurtTimer > 0) {
            this.hurtTimer--;
            region = HURT_TEXTURE;
        }

        final float rotation = this.dodgeTime / DODGE_TIME * -getFacing() * 360 * 2;

        startShade(batch);
        batch
            .draw(region.getTexture(), this.pos.x, this.pos.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(),
                  1, 1, rotation, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
                  region.getRegionHeight(), direction != -1, false);
        endShade(batch);
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
