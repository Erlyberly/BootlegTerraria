package no.erlyberly.bootlegterraria.entities.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import no.erlyberly.bootlegterraria.world.GameMap;

public abstract class Weapon {

    private String name;
    private Texture image;
    private float cooldown;
    private float cooldownTimer = 0;
    private int staminaUsage;

    public Weapon() {}

    public Weapon(String name) {
        this.name = name;
    }

    public Weapon(int cooldown) {
        this.cooldown = cooldown;
    }

    public Weapon(String name, Texture image) {
        this.name = name;
        this.image = image;
        this.cooldown = 0;
    }

    public abstract int use(GameMap map);

    public String getName() {
        return name;
    }

    public Texture getImage() {
        return image;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public float getCooldownTimer() {
        return cooldownTimer;
    }

    public void setCooldownTimer(int cooldownTimer) {
        this.cooldownTimer = cooldownTimer;
    }

    public abstract int getStaminaUsage();

    public void cooldown() {
        if (cooldownTimer < cooldown) {
            cooldownTimer += Gdx.graphics.getRawDeltaTime();
        }
    }
}
