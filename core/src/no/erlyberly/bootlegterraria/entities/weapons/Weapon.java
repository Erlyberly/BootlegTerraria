package no.erlyberly.bootlegterraria.entities.weapons;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import no.erlyberly.bootlegterraria.world.GameMap;

public abstract class Weapon {

    private String name;
    private Texture image;

    public Weapon(){}

    public Weapon(String name, Texture image){
        this.name = name;
        this.image = image;
    }

    public abstract void use(GameMap map);

    public abstract String getName();

    public abstract Texture getImage();
}
