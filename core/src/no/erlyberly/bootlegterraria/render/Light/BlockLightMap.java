package no.erlyberly.bootlegterraria.render.light;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.light.api.LightMap;
import no.erlyberly.bootlegterraria.util.CancellableThreadScheduler;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.util.Vector2Int;
import no.erlyberly.bootlegterraria.util.aabb.AABB2D;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.HashMap;
import java.util.Map;

import static no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer.logLightTime;

public class BlockLightMap implements LightMap {

    private static final CancellableThreadScheduler LIGHT_THREAD = new CancellableThreadScheduler();

    private final int[] skylight;
    private final HashMap<Vector2Int, LightInfo> lightInfoMap;
    private final GameMap map;

    public BlockLightMap(GameMap map) {
        this.map = map;
        this.lightInfoMap = new HashMap<>();
        this.skylight = new int[(int) map.getWidth()];
        LIGHT_THREAD.execute(this::initialCalculations);
    }

    @Override
    public LightLevel lightAt(Vector2Int pos) {
        //hide the real light level
        if (pos.y > this.skylight[pos.x]) {
            return LightLevel.SKY_LIGHT;
        }
        LightInfo li = this.lightInfoMap.get(pos);
        return li != null ? li.getLightLevel() : LightLevel.LVL_0;
    }

    @Override
    public void addSource(Vector2Int pos, LightLevel ll) {
        if (ll == LightLevel.LVL_0) {
            throw new IllegalArgumentException("Tried to add light level 0 as a light source");
        }
        final long startTime = System.currentTimeMillis();
        AABB2D affected = Util.fromLight(pos, ll);
        for (Vector2Int v : affected) {
            this.lightInfoMap.putIfAbsent(v, new LightInfo(v.x, v.y));
            this.lightInfoMap.get(v).put(pos, ll);
        }
        if (logLightTime) {
            GameMain.consHldr().log("Adding light took " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    @Override
    public void removeSource(Vector2Int pos) {
        if (this.lightInfoMap.containsKey(pos)) {
            final long startTime = System.currentTimeMillis();

            LightInfo posLi = this.lightInfoMap.get(pos);
            Map<Vector2Int, Float> litFrom = posLi.litFrom();

            if (litFrom.containsKey(pos)) {
                for (Vector2Int v : Util.fromLight(pos, LightLevel.valueOf(litFrom.get(pos)))) {
                    LightInfo li = this.lightInfoMap.get(v);
                    if (li != null) {
                        li.remove(pos);
                        //remove the instance if there is no light at it
                        if (li.litFrom().isEmpty()) {
                            this.lightInfoMap.remove(v);
                        }
                    }
                }
            }

            if (logLightTime) {
                GameMain.consHldr().log("Removing light took " + (System.currentTimeMillis() - startTime) + " ms");
            }
            if (pos.y == this.skylight[pos.x] + 1) {
                System.out.println("recalculating skylight at " + pos.x);
                calculateSkylight(pos.x);
            }
        }
    }

    @Override
    public int getSkylightAt(int blockX) {
        return this.skylight[blockX];
    }

    @Override
    public void calculateSkylight(int blockX) {
        int oldSkylight = this.skylight[blockX];

        TiledMapTileLayer tiledLayer = (TiledMapTileLayer) GameMain.inst().getGameMap().getBlockLayer();
        for (int y = tiledLayer.getHeight() - 1; y >= 0; y--) {
            //check if the current cell is collidable
            final TiledMapTileLayer.Cell cell = tiledLayer.getCell(blockX, y);
            if (cell == null) {
                //empty cell
                continue;
            }
            //int yLoc = height - y - 1; //actual map y loc
            final TileType tt = TileType.getTileTypeById(cell.getTile().getId());
            if (tt.isCollidable()) {
                this.skylight[blockX] = y + 1;
                break;
            }
        }

        int newSkylight = this.skylight[blockX];

        if (oldSkylight < newSkylight) { //placed a block above skylight
//            System.out.println("Placed above old skylight");
            for (int i = oldSkylight; i < newSkylight; i++) {
                removeSource(blockX, i);
            }
        }
        else if (oldSkylight > this.skylight[blockX]) { //placed a block below skylight
//            System.out.println("Placed below old skylight");
            for (int i = newSkylight; i < oldSkylight; i++) {
                addSource(blockX, i, LightLevel.SKY_LIGHT);
            }
        }
        if (oldSkylight != newSkylight) {
            addSource(blockX, newSkylight, LightLevel.SKY_LIGHT);
        }
    }

    @Override
    public LightInfo lightInfoAt(Vector2Int pos) {
        return this.lightInfoMap.getOrDefault(pos, new LightInfo(pos));
    }

    private void initialCalculations() {
        //do not log light time for each added time during the initial calculation to speed things up
        boolean oldLogLightTime = logLightTime;
        logLightTime = false;

        final long startTimeMain = System.currentTimeMillis();

        MapLayer layer = this.map.getBlockLayer();
        if (layer.isVisible() && layer instanceof TiledMapTileLayer) {
            final TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
            final int height = tiledLayer.getHeight();
            for (int x = 0, width = (int) this.map.getWidth(); x < width; x++) {
                for (int y = height - 1; y >= 0; y--) { //start at the top of the map
                    //check if the current cell is collidable
                    final TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                    if (cell == null) {
                        //empty cell
                        continue;
                    }
                    final TileType tt = TileType.getTileTypeById(cell.getTile().getId());
                    if (tt.isCollidable() && this.skylight[x] == 0) {
                        this.skylight[x] = y + 1;
//                        System.out.printf("skylight @ (%d,%d)%n", x, this.skylight[x]);
                    }
                    if (tt.getLuminosity() != LightLevel.LVL_0) {
                        addSource(x, y, tt.getLuminosity());
                    }
                }
            }
        }

        GameMain.consHldr().log(
            "Finding skylight and light emitters took " + (System.currentTimeMillis() - startTimeMain) + " ms");

        final long startTimeSkylight = System.currentTimeMillis();

        for (int x = 0; x < this.skylight.length; x++) {
            int skyLft = this.skylight[Math.max(0, x - 1)];
            int skyRht = this.skylight[Math.min(x + 1, this.skylight.length - 1)];
            int hi = Math.max(Math.max(skyLft, skyRht), this.skylight[x]);

//            System.out.printf("Adding skylight from %d to %d @ %d%n", this.skylight[x], hi, x);

            for (int y = this.skylight[x]; y < hi + 1; y++) {
                addSource(x, y, LightLevel.SKY_LIGHT);
            }
        }
        GameMain.consHldr().log(
            "Adding all skylights took " + (System.currentTimeMillis() - startTimeSkylight) + " ms\n" +
            "Total initial calculation took " + (System.currentTimeMillis() - startTimeMain) + " ms");

        logLightTime = oldLogLightTime;
    }

}
