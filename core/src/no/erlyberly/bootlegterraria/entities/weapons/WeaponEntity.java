package no.erlyberly.bootlegterraria.entities.weapons;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.world.GameMap;

/**
 * An abstract wrapper intended to be used by weapons' bullet/sword to reduce boilerplate code
 */
public abstract class WeaponEntity extends Entity {

    protected WeaponEntity(float x, float y, GameMap gameMap) {
        super(x, y, gameMap);
    }

    @Override
    public void modifyHp(float amount) { }

    @Override
    public float getHealth() {
        return 0;
    }

    @Override
    public float getMaxHealth() {
        return 0;
    }
}
