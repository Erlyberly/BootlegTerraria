package no.erlyberly.bootlegterraria.entities.weapons.weapons;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.entities.weapons.entities.Bullet;

public class Gun extends Weapon {

    public Gun(final String name) {
        super(name);
    }

    public Gun() {
    }

    @Override
    protected void attack0(final Entity attacker) {
        attacker.getGameMap().addEntity(
            new Bullet(attacker.getX() + (attacker.getWidth() / 3f * attacker.getFacing()), attacker.getY() + 16f,
                       attacker.getFacing()));
    }

    @Override
    public int getStaminaUsage() {
        return 1000;
    }

    @Override
    public float getCooldownTime() {
        return 0.1f;
    }

}
