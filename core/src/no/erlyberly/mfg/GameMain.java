package no.erlyberly.mfg;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import entities.Player;
import helpers.GameInfo;
import world.GameMap;
import world.TileType;
import world.TiledGameMap;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class GameMain extends Game {

	private SpriteBatch batch;
    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
	private GameMap gameMap;

	private Player player;

	private World world;

	public static void updateResolution(final int width, final int height) {

	}

	@Override
	public void create () {
	    batch = new SpriteBatch();
		this.gameMap = new TiledGameMap();

		this.camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
		camera.update();
	}

	@Override
	public void render () {

	    Gdx.gl.glClearColor(1,1,1,1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    if(Gdx.input.isTouched()){
	        camera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
        }

        if(Gdx.input.justTouched()){
            Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TileType type = gameMap.getTileTypeByLocation(1, pos.x, pos.y);

            if(type != null){
                System.out.println("Tile clicked: " + type.getName() +
                        ", " + type.getId() + ", " + type.getDamage());
            }else{
                System.out.println("Not a tile");
            }

        }

        camera.update();
        gameMap.update(Gdx.graphics.getDeltaTime());
		gameMap.render(camera, batch);
	}

	@Override
	public void dispose () {
		batch.dispose();
		gameMap.dispose();
	}

	public SpriteBatch getBatch(){
	    return batch;
    }

}
