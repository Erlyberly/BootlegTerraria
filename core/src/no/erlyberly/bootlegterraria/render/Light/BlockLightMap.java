package no.erlyberly.bootlegterraria.render.light;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.light.api.LightMap;
import no.erlyberly.bootlegterraria.util.CancellableThreadScheduler;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.util.Vector2Int;
import no.erlyberly.bootlegterraria.util.aabb.AABB2D;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.HashMap;

public class BlockLightMap implements LightMap {

    protected static final CancellableThreadScheduler LIGHT_THREAD = new CancellableThreadScheduler();

    private final int[] skyLight;
    private final HashMap<Vector2Int, LightInfo> lightInfos;
    private final int mapWidth;
    private final TiledMap map;
    private final int mapHeight;

    public static boolean logLightTime = true;

    public BlockLightMap(int width, int height, TiledMap map) {
        this.mapHeight = height;
        this.mapWidth = width;
        this.map = map;
        this.lightInfos = new HashMap<>();
        this.skyLight = new int[this.mapWidth];
        LIGHT_THREAD.execute(this::initialCalculations);
    }

    @Override
    public LightLevel lightAt(Vector2Int pos) {
        LightInfo li = this.lightInfos.get(pos);
        return li != null ? li.getLightLevel() : LightLevel.LVL_0;
    }

    @Override
    public void put(Vector2Int pos, LightLevel ll) {
        if (ll == LightLevel.LVL_0) {
            remove(pos);
        }
        AABB2D affected = Util.fromLight(pos, ll);
        for (Vector2Int v : affected) {
            this.lightInfos.putIfAbsent(v, new LightInfo(v.x, v.y));
            this.lightInfos.get(v).put(pos, ll);
        }
    }

    public void remove(Vector2Int pos) {
        if (this.lightInfos.containsKey(pos)) {
            final long startTime = System.currentTimeMillis();
            AABB2D aabb = Util.fromLight(pos, lightAt(pos));
            for (Vector2Int v : aabb) {
                LightInfo li = this.lightInfos.get(v);
                if (li != null) {
                    li.remove(pos);
                }

            }
            this.lightInfos.remove(pos);
            if (logLightTime) {
                GameMain.consHldr().log("Removing light took " + (System.currentTimeMillis() - startTime) + " ms");
            }
            if (pos.y == this.skyLight[pos.x] + 1) {
                System.out.println("removing skylight ");
                calculateSkylightColumn(pos.x);
            }
        }
    }

    @Override
    public int[] getSkylight() {
        final int[] cpy = new int[this.skyLight.length];
        System.arraycopy(this.skyLight, 0, cpy, 0, this.skyLight.length);
        return cpy;
    }

    private void calculateSkylightColumn(int col) {
        TiledMapTileLayer tiledLayer = (TiledMapTileLayer) GameMain.inst().getGameMap().getBlockLayer();
        for (int y = tiledLayer.getHeight() - 1; y >= 0; y--) {
            //check if the current cell is collidable
            final TiledMapTileLayer.Cell cell = tiledLayer.getCell(col, y);
            //batch.setColor(1.0f, 1.0f, 1.0f, brightness[x][y].getPercentage());
            if (cell == null) {
                //empty cell
                continue;
            }
            //int yLoc = height - y - 1; //actual map y loc
            final TileType tt = TileType.getTileTypeById(cell.getTile().getId());
            if (tt.isCollidable()) {
                System.out.println("old skylight (" + col + ", " + this.skyLight[col] + ")");
                this.skyLight[col] = this.skyLight[col] < y ? y + 1 : this.skyLight[col];
                put(col, this.skyLight[col], SKY_LIGHT_BRIGHTNESS);
                System.out.println("new skylight found (" + col + ", " + this.skyLight[col] + ")");
                return;
            }
        }
    }

    private void initialCalculations() {

        final long startTime = System.currentTimeMillis();

        for (final MapLayer layer : this.map.getLayers()) {
            if (layer.isVisible() && layer instanceof TiledMapTileLayer) {
                final TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
                final int height = tiledLayer.getHeight();
                for (int x = 0; x < this.mapWidth; x++) {
                    boolean skyFound = false;
                    for (int y = height - 1; y >= 0; y--) {
                        //check if the current cell is collidable
                        final TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                        if (cell == null) {
                            //empty cell
                            continue;
                        }
                        //int yLoc = height - y - 1; //actual map y loc
                        final TileType tt = TileType.getTileTypeById(cell.getTile().getId());
                        if (!skyFound && tt.isCollidable()) {
                            this.skyLight[x] = this.skyLight[x] < y ? y : this.skyLight[x];
                            skyFound = true; //no further looping required
                        }
                        if (tt.getLuminosity() != LightLevel.LVL_0) {
                            put(x, y, tt.getLuminosity());
                        }
                    }
                }
            }
        }
        if (logLightTime) {
            GameMain.consHldr().log(
                "Finding skylight and light emitters took " + (System.currentTimeMillis() - startTime) + " ms");
        }

        for (int x = 0; x < this.skyLight.length; x++) {
            put(x, this.skyLight[x], SKY_LIGHT_BRIGHTNESS);
//            for (int y = this.mapHeight - 1, length = Math.max(-1, this.skyLight[x]); y > length; y--) {
//                put(x, y, SKY_LIGHT_BRIGHTNESS);
//            }
        }

        if (logLightTime) {
            GameMain.consHldr()
                    .log("Total initial calculation took " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

}
