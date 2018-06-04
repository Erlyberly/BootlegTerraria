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

public class BlockLightMap implements LightMap {

    protected static final CancellableThreadScheduler LIGHT_THREAD = new CancellableThreadScheduler();

    private final int[] skyLight;
    private final HashMap<Vector2Int, LightInfo> lightInfoMap;
    private final GameMap map;

    public static boolean logLightTime = true;

    public BlockLightMap(GameMap map) {
        this.map = map;
        this.lightInfoMap = new HashMap<>();
        this.skyLight = new int[(int) map.getWidth()];
        LIGHT_THREAD.execute(this::initialCalculations);
    }

    @Override
    public LightLevel lightAt(Vector2Int pos) {
        LightInfo li = this.lightInfoMap.get(pos);
        return li != null ? li.getLightLevel() : LightLevel.LVL_0;
    }

    @Override
    public void put(Vector2Int pos, LightLevel ll, boolean skylight) {
        if (ll == LightLevel.LVL_0) {
            throw new IllegalArgumentException("Tried to put light level 0 as a light source");
        }
        final long startTime = System.currentTimeMillis();
        AABB2D affected = Util.fromLight(pos, ll);
        for (Vector2Int v : affected) {
            this.lightInfoMap.putIfAbsent(v, new LightInfo(v.x, v.y));
            this.lightInfoMap.get(v).put(pos, ll);
        }
        this.lightInfoMap.get(pos).setSkylight(skylight);
        if (logLightTime) {
            GameMain.consHldr().log("Adding light took " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    @Override
    public void remove(Vector2Int pos) {
        if (this.lightInfoMap.containsKey(pos)) {
            final long startTime = System.currentTimeMillis();
            AABB2D aabb = Util.fromLight(pos, lightAt(pos));
            for (Vector2Int v : aabb) {
                LightInfo li = this.lightInfoMap.get(v);
                if (li != null) {
                    li.remove(pos);
                }
            }
            this.lightInfoMap.remove(pos);
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
                put(col, this.skyLight[col], LightLevel.SKY_LIGHT);
                System.out.println("new skylight found (" + col + ", " + this.skyLight[col] + ")");
                return;
            }
        }
    }

    private void initialCalculations() {
        //do not log light time for each added time during the initial calculation to speed things up
        boolean oldLogLightTime = logLightTime;
        logLightTime = false;

        final long startTime = System.currentTimeMillis();

        MapLayer layer = this.map.getBlockLayer();
        if (layer.isVisible() && layer instanceof TiledMapTileLayer) {
            final TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
            final int height = tiledLayer.getHeight();
            for (int x = 0, width = (int) this.map.getWidth(); x < width; x++) {
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

        if (oldLogLightTime) {
            GameMain.consHldr().log(
                "Finding skylight and light emitters took " + (System.currentTimeMillis() - startTime) + " ms");
        }

        for (int x = 0; x < this.skyLight.length; x++) {
            put(x, this.skyLight[x], LightLevel.SKY_LIGHT);

            for (int y = (int) this.map.getHeight() - 1, length = Math.max(-1, this.skyLight[x] - 1); y > length; y--) {
                if (this.map.getTile(x, y) != null) {
                    put(x, y, LightLevel.SKY_LIGHT);
                }
            }
        }

        if (oldLogLightTime) {
            GameMain.consHldr()
                    .log("Total initial calculation took " + (System.currentTimeMillis() - startTime) + " ms");
        }
        logLightTime = oldLogLightTime;
    }

}
