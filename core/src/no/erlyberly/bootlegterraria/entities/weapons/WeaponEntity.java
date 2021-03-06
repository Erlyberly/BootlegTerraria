package no.erlyberly.bootlegterraria.entities.weapons;

import no.erlyberly.bootlegterraria.entities.Entity;

/**
 * An abstract wrapper intended to be used by weapons' bullet/sword to reduce boilerplate code. This class can also be
 * used to test if an entity is a weapon with {@link Class#isAssignableFrom(Class)}
 */
public abstract class WeaponEntity extends Entity {

    protected WeaponEntity(final float x, final float y, final int facing) {
        super(x, y);
        setFacing(facing);
    }

    @Override
    public void modifyHp(final float amount) { }

    @Override
    public float getHealth() {
        return 0;
    }

    @Override
    public float getMaxHealth() {
        return 0;
    }
}
