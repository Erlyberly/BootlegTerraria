package no.erlyberly.bootlegterraria.entities.weapons;

import com.badlogic.gdx.Gdx;
import no.erlyberly.bootlegterraria.entities.Entity;

public abstract class Weapon {

    private String name;
    private float cooldown;

    public Weapon() {}


    public Weapon(final String name) {
        this.name = name;
    }

    /**
     * @return The display name of this weapon
     */
    public String getName() {
        if (name == null) {
            return getClass().getSimpleName();
        }
        return name;
    }

    /**
     * Attack with a the weapon and reset the cooldown time
     */
    public void attack(final Entity attacker) {
        attack0(attacker);
        resetCooldown();
    }

    /**
     * @return If this weapon is ready to attack
     */
    public boolean isCooledDown() {
        return getCooldownTime() <= getCooldown();
    }

    public float getCooldown() {
        return cooldown;
    }

    public void resetCooldown() {
        cooldown = 0;
    }

    /**
     * Cool the weapon, should be called once every frame
     */
    public void cooldown() {
        if (!isCooledDown()) {
            cooldown += Gdx.graphics.getRawDeltaTime();
        }
    }

    /**
     * @return How long, in seconds, the delay between shots should be
     */
    public abstract float getCooldownTime();

    /**
     * @return How much stamina should an attack with this weapon cost
     */
    public abstract int getStaminaUsage();

    /**
     * What to do when the weapon is used
     */
    protected abstract void attack0(Entity attacker);
}
