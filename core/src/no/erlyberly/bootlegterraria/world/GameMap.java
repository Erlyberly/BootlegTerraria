package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.entities.Player;
import no.erlyberly.bootlegterraria.helpers.GameInfo;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public abstract class GameMap {

    BitmapFont font = new BitmapFont();

    public final static float GRAVITY = -9.81f;

    private ArrayList<Entity> enimies;
    private ArrayList<Entity> addEnimies;
    private boolean addWaitingEnimies = false;
    private boolean removeWaitingEnimies = false;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> addEntities;
    private boolean addWaitingEntities = false;
    private boolean removeWaitingEntities = false;

    private Player player;

    GameMap() {
        enimies = new ArrayList<Entity>();
        addEnimies = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        addEntities = new ArrayList<Entity>();
        player = new Player(600, 600, this);
    }

    public void addEnemy(Entity entity){
        addEnimies.add(entity);
        addWaitingEnimies = true;
    }

    public void removeEnemy(){
        removeWaitingEnimies = true;
    }

    public void addEntity(Entity entity){
        addEntities.add(entity);
        addWaitingEntities = true;
    }

    public void removeEntity(){
        removeWaitingEntities = true;
    }

    public void render(OrthographicCamera camera, OrthographicCamera hudCamera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        player.render(batch);

        for (Entity enimies : enimies) {
            enimies.render(batch);
        }

        for (Entity entities : entities) {
            entities.render(batch);
        }

        camera.position.x = player.getX();
        camera.position.y = player.getY();
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        //hp bar
        Texture hpBar = new Texture("hp_fill.png");
        Texture barOutline = new Texture("bar_outline.png");
        batch.draw(hpBar, 6f, GameInfo.HEIGHT / 2.1f, (((float)player.getHp() / (float)player.getMaxHp()) * hpBar.getWidth()), hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / 2.1f);
        font.draw(batch, player.getHp() + " / " + player.getMaxHp(), barOutline.getWidth() + 10f, 12f + GameInfo.HEIGHT / 2.1f);

        //stamina bar
        Texture staminaBar = new Texture("stamina_fill.png");
        float staminaPercent = (float)player.getStamina() / (float)player.getMaxStamina();
        if(staminaPercent < 0){
            staminaPercent = 0;
        }
        batch.draw(staminaBar, 6f, GameInfo.HEIGHT / 2.2f, staminaPercent * hpBar.getWidth(), hpBar.getHeight());
        batch.draw(barOutline, 5f, GameInfo.HEIGHT / 2.2f);
        font.draw(batch, player.getStamina() + " / " + player.getMaxStamina(), barOutline.getWidth() + 10f, 12f + GameInfo.HEIGHT / 2.2f);

        //hud text
        font.draw(batch, "Weapon: " + player.getWeapon().getName(), 7f, GameInfo.HEIGHT / 2.28f);
        batch.end();
    }

    @SuppressWarnings("Duplicates")
    public void update(float delta) {

        checkEntityEnemyCollision();
        checkPlayerEnemyCollision();

        player.update(delta, GRAVITY);

        for (Entity entity : enimies) {
            entity.update(delta, GRAVITY);
        }

        for (Entity entity : entities) {
            entity.update(delta, GRAVITY);
        }

        if(addWaitingEnimies){
            for(Entity add : addEnimies) {
                enimies.add(add);
            }
            addEnimies.clear();
            addWaitingEnimies = false;
        }

        if(removeWaitingEnimies){
            ArrayList<Entity> removeEnimies = new ArrayList<Entity>();
            for(Entity i : enimies) {
                if(i.isDestroyed()){
                    removeEnimies.add(i);
                }
            }
            for(Entity remove : removeEnimies){
                enimies.remove(remove);
            }
            removeWaitingEnimies = false;
        }

        for (Entity entity : entities) {
            entity.update(delta, GRAVITY);
        }

        if(addWaitingEntities){
            for(Entity add : addEntities) {
                entities.add(add);
            }
            addEntities.clear();
            addWaitingEntities = false;
        }

        if(removeWaitingEntities){
            ArrayList<Entity> removeEntities = new ArrayList<Entity>();
            for(Entity i : entities) {
                if(i.isDestroyed()){
                    removeEntities.add(i);
                }
            }
            for(Entity remove : removeEntities){
                entities.remove(remove);
            }
            removeWaitingEntities = false;
        }
    }

    public abstract void dispose();

    public TileType getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    public abstract TileType getTileTypeByCoordinate(int layer, int col, int row);

    public boolean checkMapCollision(float x, float y, float width, float height) {

        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE); row < Math.ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) (x / TileType.TILE_SIZE); col < Math.ceil((x + width) / TileType.TILE_SIZE); col++) {
                for (int layer = 0; layer < getLayers(); layer++) {
                    TileType type = getTileTypeByCoordinate(layer, col, row);
                    if (type != null && type.isCollidable()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean checkPlayerMapCollision(float x, float y, float width, float height) {

        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }

        for (int row = (int) (y / TileType.TILE_SIZE); row < Math.ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) (x / TileType.TILE_SIZE); col < Math.ceil((x + width) / TileType.TILE_SIZE); col++) {
                for (int layer = 0; layer < getLayers(); layer++) {
                    TileType type = getTileTypeByCoordinate(layer, col, row);
                    if (type != null && type.isCollidable()) {
                        if(type.getDamage() != 0 && !player.isInvincible()){
                            player.modifyHp(-type.getDamage());
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void checkEntityEnemyCollision(){
        for(Entity entities : entities){
            for(Entity enimies : enimies){
                if(Intersector.overlaps(new Rectangle(entities.getX(), entities.getY(), entities.getWidth(), entities.getHeight()), new Rectangle(enimies.getX(), enimies.getY(), enimies.getWidth(), enimies.getHeight()))){
                    enimies.modifyHp(-entities.getDamage());
                    enimies.moveX((enimies.getHorizontalSpeed() / 8f) * -enimies.getFacingX());
                }
            }
        }
    }

    public void checkPlayerEnemyCollision(){

        float playerX = player.getX();
        float playerY = player.getY();
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();

        for(Entity enemies : enimies){
            if(!player.isInvincible() && Intersector.overlaps(new Rectangle(enemies.getX(), enemies.getY(), enemies.getWidth(), enemies.getHeight()), new Rectangle(playerX, playerY, playerWidth, playerHeight))){
                player.modifyHp(-enemies.getDamage());
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public float getPixelWidth() {
        return this.getWidth() * TileType.TILE_SIZE;
    }

    @SuppressWarnings("WeakerAccess")
    public float getPixelHeight() {
        return this.getHeight() * TileType.TILE_SIZE;
    }

    public abstract int getLayers();

    public abstract float getWidth();

    public abstract float getHeight();

    public Player getPlayer(){
        return player;
    }

    public void render(SpriteBatch batch){

    }
}
