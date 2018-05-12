package no.erlyberly.bootlegterraria.entities.weapons;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import no.erlyberly.bootlegterraria.world.GameMap;

public abstract class Weapon {

    private String name;
    private Texture image;
    private int cooldown;
    private int cooldownTimer = 0;

    public Weapon(){}

    public Weapon(String name){
        this.name = name;
    }

    public Weapon(int cooldown){
        this.cooldown = cooldown;
    }

    public Weapon(String name, Texture image){
        this.name = name;
        this.image = image;
        this.cooldown = 0;
    }

    public abstract void use(GameMap map);

    public String getName() {
        return name;
    }

    public Texture getImage() {
        return image;
    }

    public int getCooldown(){
        return cooldown;
    }

    public void setCooldown(int cooldown){
        this.cooldown = cooldown;
    }

    public int getCooldownTimer() {
        return cooldownTimer;
    }

    public void setCooldownTimer(int cooldownTimer) {
        this.cooldownTimer = cooldownTimer;
    }

    public void cooldown(){
        if(cooldownTimer < cooldown){
            cooldownTimer++;
        }
    }
}
