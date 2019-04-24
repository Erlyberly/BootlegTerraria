package no.erlyberly.bootlegterraria.render.light;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.light.api.LightMap;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.util.Vector2Int;
import no.erlyberly.bootlegterraria.util.aabb.AABB2D;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer.logLightEvents;
import static no.erlyberly.bootlegterraria.render.light.LightLevel.SKY_LIGHT;

public class BlockLightMap implements LightMap {

    private final int[] skylight;
    private final HashMap<Vector2Int, LightInfo> lightInfoMap;
    private final GameMap map;

    public static boolean realLight = true;

    private boolean initialized;
    private boolean disabled = false;

    public BlockLightMap(final GameMap map) {
        initialized = false;
        this.map = map;
        lightInfoMap = new HashMap<>();
        skylight = new int[(int) map.getWidth()];
        initialCalculations();
    }

    @Override
    public LightLevel lightAt(final Vector2Int pos) {
        //hide the real light level
        if (disabled || realLight && pos.y >= skylight[pos.x]) {
            return SKY_LIGHT;
        }
        final LightInfo li = lightInfoMap.get(pos);
        return li != null ? li.getLightLevel() : LightLevel.LVL_0;
    }

    @Override
    public void addSource(final Vector2Int pos, final LightLevel ll) {
        addSource(pos, ll, false);
    }

    private void addSkylight(final int blockX, final int blockY) {
        addSource(new Vector2Int(blockX, blockY), LightLevel.SKY_LIGHT, true);
    }

    private void addSource(final Vector2Int pos, final LightLevel ll, final boolean skylight) {
        if (ll == LightLevel.LVL_0) {
            throw new IllegalArgumentException("Tried to add light level 0 as a light source");
        }
        else if (pos.x < 0 || pos.y < 0 || pos.x >= map.getWidth() || pos.y >= map.getHeight()) {
            throw new IllegalArgumentException("Tried to add light source outside of map");
        }

//        GameMain.SECONDARY_THREAD.execute(() -> {
        GameMain.SECONDARY_THREAD.executeAsync(() -> {
            final long startTime = System.currentTimeMillis();
            final AABB2D affected = Util.fromLight(pos, map, ll);


//            getLi(pos).put(pos, ll, skylight);
            if (skylight || !getLi(pos).isSkylight()) {
                for (final Vector2Int v : affected) {
                    //FIXME totally borken lol
                    getLi(v).put(pos, ll, skylight);
                }
            }
            if (logLightEvents) {
                GameMain.console.log("Adding light took " + (System.currentTimeMillis() - startTime) + " ms");
            }
        });
    }

    @Override
    public void removeSource(final Vector2Int pos) {
        removeSource(pos, false);
    }

    private void removeSkylight(final int blockX, final int blockY) {
        removeSource(new Vector2Int(blockX, blockY), true);
    }

    private void removeSource(final Vector2Int pos, final boolean skylight) {
        if (pos.x < 0 || pos.y < 0 || pos.x >= map.getWidth() || pos.y >= map.getHeight()) {
            throw new IllegalArgumentException("Tried to remove light source outside of map");
        }
        if (lightInfoMap.containsKey(pos)) {
            GameMain.SECONDARY_THREAD.executeAsync(() -> {
                final long startTime = System.currentTimeMillis();

                final LightInfo posLi = lightInfoMap.get(pos);
                final Map<Vector2Int, Float> litFrom = posLi.litFrom();

                final ArrayList<LightInfo> emittedLight = new ArrayList<>();

                if (litFrom.containsKey(pos)) {
                    for (final Vector2Int v : Util.fromLight(pos, map, LightLevel.valueOf(litFrom.get(pos)))) {
                        final LightInfo li = lightInfoMap.get(v);
                        if (li != null) {
                            final boolean oldSkylight = li.isSkylight();
                            li.remove(pos, skylight);
                            if (skylight && oldSkylight && li.getEmitting() != null) {
                                emittedLight.add(li);
                            }
                            //remove the instance if there is no light at it
                            if (li.litFrom().isEmpty()) {
                                lightInfoMap.remove(v);
                            }
                        }
                    }
                }

                //re-add all the emitted lights (for torch or lava)
                for (final LightInfo li : emittedLight) {
                    addSource(li.getPosi(), li.getEmitting());
                }

                if (logLightEvents) {
                    GameMain.console.log("Removing light took " + (System.currentTimeMillis() - startTime) + " ms");
                }
            });
        }
    }

    @Override
    public int getSkylightAt(final int blockX) {
        return skylight[blockX];
    }

    @Override
    public void calculateSkylight(final int blockX) {
        Preconditions
            .checkArgument(Util.isBetween(0, blockX, skylight.length), "The argument must be between 0 and mapWidth - 1");
        GameMain.SECONDARY_THREAD.executeAsync(() -> {
            final long startTime = System.currentTimeMillis();
            final boolean oldLogLightTime = logLightEvents;
            logLightEvents = false;

            final int oldSkylight = skylight[blockX];

            final TiledMapTileLayer tiledLayer = GameMain.map.getBlockLayer();

            boolean skylightFound = false;

            for (int y = tiledLayer.getHeight() - 1; y >= 0; y--) { //loop from the top of the map
                final TiledMapTileLayer.Cell cell = tiledLayer.getCell(blockX, y);
                if (cell == null) {
                    //empty cell
                    continue;
                }
                final TileType tt = TileType.getTileTypeById(cell.getTile().getId());
                //check if the current cell is collidable, if it is this is where the skylight stops
                if (tt.isSolid()) {
                    skylight[blockX] = y + 1;
                    skylightFound = true;
                    break;
                }
            }
            if (!skylightFound) {
                skylight[blockX] = 0;
            }

            final int newSkylight = skylight[blockX];

            if (oldSkylight < newSkylight) { //placed a block above skylight
                for (int y = oldSkylight; y < newSkylight; y++) {

                    removeSkylight(blockX, y);


                    //add lights on either end
                    if (blockX + 1 < skylight.length && skylight[blockX + 1] < y) {
                        addSkylight(blockX + 1, y);
                    }
                    if (blockX - 1 >= 0 && skylight[blockX - 1] < y) {
                        addSkylight(blockX - 1, y);
                    }
                }
            }
            else if (oldSkylight > skylight[blockX]) { //placed a block below skylight
                for (int y = newSkylight; y < oldSkylight; y++) {
                    addSkylight(blockX, y);
                }
            }
            if (oldSkylight != newSkylight) {
                addSkylight(blockX, newSkylight);
            }
            if (oldLogLightTime) {
                GameMain.console
                    .log("Calculating skylight column " + blockX + " took " + (System.currentTimeMillis() - startTime) + " ms");
            }
            logLightEvents = oldLogLightTime;
        });
    }

    @Override
    public LightInfo lightInfoAt(final Vector2Int pos) {
        if (map.isOutsideMap(pos.x, pos.y)) {
            return null;
        }
        return lightInfoMap.getOrDefault(pos, new LightInfo(pos));
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    private void initialCalculations() {
        GameMain.SECONDARY_THREAD.executeAsync(() -> {

            //do not log light time for each added time during the initial calculation to speed things up
            final boolean oldLogLight = logLightEvents;
            logLightEvents = false;

            final long startTimeMain = System.currentTimeMillis();

            final MapLayer layer = map.getBlockLayer();
            if (layer.isVisible() && layer instanceof TiledMapTileLayer) {
                final TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
                final int height = tiledLayer.getHeight();
                for (int x = 0, width = (int) map.getWidth(); x < width; x++) {
                    for (int y = height - 1; y >= 0; y--) { //start at the top of the map

                        //check if the current cell is collidable
                        final TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                        if (cell == null) {
                            //empty cell
                            continue;
                        }
                        final TileType tt = TileType.getTileTypeById(cell.getTile().getId());
                        if (tt.isSolid() && skylight[x] == 0) {
                            skylight[x] = y + 1;
//                        System.out.printf("skylight @ (%d,%d)%n", x, this.skylight[x]);
                        }
                        if (tt.getLuminosity() != LightLevel.LVL_0) {
                            addSource(x, y, tt.getLuminosity());
                        }
                    }
                }
            }

            GameMain.console
                .log("Finding skylight and light emitters took " + (System.currentTimeMillis() - startTimeMain) + " ms");

            final long startTimeSkylight = System.currentTimeMillis();

            for (int x = 0; x < skylight.length; x++) {
                //add lights up to the highest skylight point of either left, right and here
                final int skyLft = skylight[Math.max(0, x - 1)];
                final int skyRht = skylight[Math.min(x + 1, skylight.length - 1)];
                int hi = Math.max(Math.max(skyLft, skyRht), skylight[x]);
                hi = Util.clamp(0, hi, (int) map.getHeight() - 1);
//                System.out.printf("Adding skylight from %d to %d @ %d%n", this.skylight[x], hi, x);

                for (int y = skylight[x]; y < hi + 1; y++) {
//                    System.out.printf("Adding skylight @ (%d,%d)%n", x, y);
                    addSkylight(x, y);
                }

            }

            GameMain.SECONDARY_THREAD.executeAsync(() -> {
                logLightEvents = oldLogLight;
                initialized = true;
                GameMain.console.log("Adding all skylights took " + (System.currentTimeMillis() - startTimeSkylight) + " ms");
                GameMain.console
                    .log("Total initial light calculation took " + (System.currentTimeMillis() - startTimeMain) + " ms");
            });
        });
    }

    private LightInfo getLi(final Vector2Int pos) {
        lightInfoMap.putIfAbsent(pos, new LightInfo(pos));
        return lightInfoMap.get(pos);
    }

    private LightInfo getLi(final int blockX, final int blockY) {
        return getLi(new Vector2Int(blockX, blockY));
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
