package no.erlyberly.bootlegterraria.entities.weapons.weapons;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.entities.weapons.entities.SwordSwing;

public class Sword extends Weapon {

    public Sword(final String name) {
        super(name);
    }

    @Override
    public void attack0(final Entity attacker) {
        final float dir = attacker.getFacing() == 1 ? attacker.getWidth() : -SwordSwing.SWORD_LENGTH;
        attacker.getGameMap()
                .addEntity(new SwordSwing(attacker.getX() + dir, attacker.getY() + 16f, attacker.getFacing()));
    }

    @Override
    public int getStaminaUsage() {
        return 2000;
    }

    @Override
    public float getCooldownTime() {
        return 0.6f;
    }
}
