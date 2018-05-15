package no.erlyberly.bootlegterraria.entities.weapons.weapons;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.entities.weapons.entities.SwordSwing;

public class Sword extends Weapon {

    public Sword(String name) {
        super(name);
    }

    @Override
    public void attack0(Entity attacker) {
        float dir = attacker.getFacing() == 1 ? attacker.getWidth() : -SwordSwing.SWORD_LENGTH;
        attacker.getGameMap().addEntity(
            new SwordSwing(attacker.getX() + dir, attacker.getY() + 16f, attacker.getGameMap(), attacker.getFacing()));
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
