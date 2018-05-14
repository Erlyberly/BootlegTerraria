package no.erlyberly.bootlegterraria.entities.weapons;

import com.badlogic.gdx.graphics.Texture;
import no.erlyberly.bootlegterraria.entities.Bullet;
import no.erlyberly.bootlegterraria.world.GameMap;

public class Gun extends Weapon {

    private int staminaUsage = 1000;

    private static final float DEFAULT_COOLDOWN_TIME = 0.2f;

    public Gun() {
        setCooldown(DEFAULT_COOLDOWN_TIME);
    }

    public Gun(String name) {
        super(name);
        setCooldown(DEFAULT_COOLDOWN_TIME);
    }

    public Gun(int cooldown) {
        super(cooldown);
    }

    public Gun(String name, Texture image) {
        super(name, image);
    }

    public int use(GameMap map) {
        if (getCooldownTimer() >= getCooldown()) {
            map.addEntity(
                new Bullet(map.getPlayer().getX() + (map.getPlayer().getWidth() / 3f * map.getPlayer().getFacingX()),
                           map.getPlayer().getY() + 16f, map, map.getPlayer().getFacingX()));
            setCooldownTimer(0);
            return -staminaUsage;
        }
        else {
            return 0;
        }
    }

    @Override
    public int getStaminaUsage() {
        return staminaUsage;
    }
}
