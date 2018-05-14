package no.erlyberly.bootlegterraria.entities.weapons;

import no.erlyberly.bootlegterraria.entities.SwordSwing;
import no.erlyberly.bootlegterraria.world.GameMap;

public class Sword extends Weapon {

    private int staminaUsage = 2000;

    public Sword(String name) {
        super(name);
        setCooldown(30);
    }

    @Override
    public int use(GameMap map) {
        if (getCooldownTimer() >= getCooldown()) {
            if (map.getPlayer().getFacingX() == 1) {
                map.addEntity(
                    new SwordSwing(map.getPlayer().getX() + (map.getPlayer().getWidth()), map.getPlayer().getY() + 16f,
                                   map, map.getPlayer().getFacingX()));
            }
            else {
                map.addEntity(
                    new SwordSwing(map.getPlayer().getX() - SwordSwing.SWORD_LENGTH, map.getPlayer().getY() + 16f, map,
                                   map.getPlayer().getFacingX()));
            }
            setCooldownTimer(0);
            return -staminaUsage;
        }
        else {
            return 0;
        }
    }

    public int getStaminaUsage() {
        return staminaUsage;
    }
}
