package no.erlyberly.bootlegterraria.entities.weapons;

import com.badlogic.gdx.graphics.Texture;
import no.erlyberly.bootlegterraria.entities.Bullet;
import no.erlyberly.bootlegterraria.world.GameMap;

public class Gun extends Weapon{

    public Gun(){}

    public Gun(String name, Texture image) {
        super(name, image);
    }

    public void use(GameMap map){
        map.addEntity(new Bullet(map.getPlayer().getX() + (map.getPlayer().getWidth()/3f * map.getPlayer().getFacingX()), map.getPlayer().getY() + 7f, map, map.getPlayer().getFacingX()));
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Texture getImage() {
        return null;
    }
}
