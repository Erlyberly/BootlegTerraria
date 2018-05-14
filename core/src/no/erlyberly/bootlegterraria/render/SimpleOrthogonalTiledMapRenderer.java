package no.erlyberly.bootlegterraria.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.helpers.LightLevel;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.g2d.Batch.*;
import static no.erlyberly.bootlegterraria.GameMain.THREAD_SCHEDULER;
import static no.erlyberly.bootlegterraria.helpers.LightLevel.LVL_0;

public class SimpleOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {

    private final int mapWidth;
    private final int mapHeight;
    private int[] skyLight;
    private HashMap<Vector2, LightLevel> lightSources;
    private LightLevel[][] brightness;

    public static final LightLevel SKY_LIGHT_BRIGHTNESS = LightLevel.LVL_7;


    public SimpleOrthogonalTiledMapRenderer(TiledMap map, boolean test) {
        super(map);

        //get the width of the map
        MapProperties prop = map.getProperties();
        mapWidth = prop.get("width", Integer.class);
        mapHeight = prop.get("height", Integer.class);

        skyLight = new int[mapWidth];
        lightSources = new HashMap<>();
        brightness = new LightLevel[mapWidth][mapHeight];

        for (int x = 0; x < brightness.length; x++) {
            for (int y = 0; y < brightness[x].length; y++) {
                brightness[x][y] = LightLevel.LVL_0;
            }
        }

        for (int i = 0; i < mapWidth; i++) {
            skyLight[i] = Integer.MIN_VALUE;
        }
        if (test) {
            test();
        }
        else {
            asyncUpdateLights();
            asyncCalculateLight();
        }
    }

    public void asyncUpdateLights() {
        THREAD_SCHEDULER.execute(this::updateLights);
    }


    public void updateLights() {
        updateLightBetween(0, skyLight.length);
    }

    /**
     * Update the skylight between {@code min} and {@code max}
     *
     * @param min
     *     The minimum x value to check (inclusive)
     * @param max
     *     The maximum x value to check (exclusive)
     */
    public void updateLightBetween(int min, int max) {
        Preconditions.checkArgument(min >= 0, "Minimum argument must be greater than or equal to 0");
        Preconditions
            .checkArgument(max <= skyLight.length, "Maximum argument must be less than or equal to skyLight.length");
        Preconditions.checkArgument(min < max, "Minimum argument must be less than maximum argument");

        //remove all light sources between min and max
        lightSources.entrySet().removeIf(entry -> entry.getKey().x >= min && entry.getKey().x < max);

        for (MapLayer layer : map.getLayers()) {
            if (layer.isVisible() && layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
                int height = tiledLayer.getHeight();
                for (int x = min; x < max; x++) {
                    skyLight[x] = Integer.MIN_VALUE; //reset skylight if already set
                    boolean skyFound = false;
                    for (int y = height - 1; y >= 0; y--) {
                        //check if the current cell is collidable
                        Cell cell = tiledLayer.getCell(x, y);
                        //batch.setColor(1.0f, 1.0f, 1.0f, brightness[x][y].getPercentage());
                        if (cell == null) {
                            //empty cell
                            continue;
                        }
                        //int yLoc = height - y - 1; //actual map y loc
                        TileType tt = TileType.getTileTypeById(cell.getTile().getId());
                        if (!skyFound && tt.isCollidable()) {
                            skyLight[x] = skyLight[x] < y ? y - 1 : skyLight[x];
                            skyFound = true; //no further looping required
                        }
                        if (tt.getLuminosity() != LightLevel.LVL_0) {
                            lightSources.put(new Vector2(x, y), tt.getLuminosity());
                        }
                    }
                }
            }
        }
        for (int x = 0; x < skyLight.length; x++) {
            for (int y = mapHeight - 1; y > skyLight[x]; y--) {
                Vector2 v = new Vector2(x, y);
                lightSources.put(v, SKY_LIGHT_BRIGHTNESS);
            }
        }
    }

    public void updateLightAt(int x) {
        Preconditions.checkArgument(x >= 0, "x must be greater than 0");
        Preconditions.checkArgument(x < skyLight.length, "x must be less than the width of the world");
        updateLightBetween(x, x + 1);
    }

    /**
     * @return A copy of the blocking lighting positions
     */
    public int[] getSkyLights() {
        int[] cpy = new int[skyLight.length];
        System.arraycopy(skyLight, 0, cpy, 0, skyLight.length);
        return cpy;
    }

    public void asyncCalculateLight() {
        THREAD_SCHEDULER.execute(this::calculateLight);
    }

    public void calculateLight() {

        LightLevel[][] newBrightness = new LightLevel[mapWidth][mapHeight];

        for (int x = 0; x < newBrightness.length; x++) {
            for (int y = 0; y < newBrightness[x].length; y++) {
                newBrightness[x][y] = LightLevel.LVL_0;
            }
        }


        //first pass, set all light sources to their own level
        for (Map.Entry<Vector2, LightLevel> entry : lightSources.entrySet()) {
            Vector2 coord = entry.getKey();
            newBrightness[(int) coord.x][(int) coord.y] = entry.getValue();
        }

        //do 7(LIGHT_LEVELS) passes to make sure the brightness is correct on all blocks
        for (int i = 0; i < LightLevel.LIGHT_LEVELS; i++) {
            for (int x = 0; x < newBrightness.length; x++) {
                for (int y = 0; y < newBrightness[x].length; y++) {
                    LightLevel currBrightness = newBrightness[x][y];
                    LightLevel dimmer = currBrightness.dimmer();
                    if (currBrightness == LVL_0) {
                        continue;
                    }
                    for (int x1 = Math.max(x - 1, 0); x1 <= Math.min(x + 1, newBrightness.length - 1); x1++) {
                        for (int y1 = Math.max(y - 1, 0); y1 <= Math.min(y + 1, newBrightness[x].length - 1); y1++) {
                            if (newBrightness[x1][y1].getLvl() < currBrightness.getLvl()) {
                                newBrightness[x1][y1] = dimmer;
                            }
                        }
                    }
                }
            }
        }

        Gdx.app.postRunnable(() -> brightness = newBrightness);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        final Color batchColor = batch.getColor();

        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();

        final float layerTileWidth = layer.getTileWidth() * unitScale;
        final float layerTileHeight = layer.getTileHeight() * unitScale;

        final float layerOffsetX = layer.getRenderOffsetX() * unitScale;
        // offset in tiled is y down, so we flip it
        final float layerOffsetY = -layer.getRenderOffsetY() * unitScale;

        final int col1 = Math.max(0, (int) ((viewBounds.x - layerOffsetX) / layerTileWidth));
        final int col2 = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth - layerOffsetX) /
                                                     layerTileWidth));

        final int row1 = Math.max(0, (int) ((viewBounds.y - layerOffsetY) / layerTileHeight));
        final int row2 = Math.min(layerHeight,
                                  (int) ((viewBounds.y + viewBounds.height + layerTileHeight - layerOffsetY) /
                                         layerTileHeight));

        float y = row2 * layerTileHeight + layerOffsetY;
        float xStart = col1 * layerTileWidth + layerOffsetX;
        final float[] vertices = this.vertices;

        for (int row = row2; row >= row1; row--) {
            float x = xStart;
            for (int col = col1; col < col2; col++) {
                final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) {
                    x += layerTileWidth;
                    continue;
                }
                final TiledMapTile tile = cell.getTile();

                if (tile != null && brightness[col][row] != LVL_0) {
                    final boolean flipX = cell.getFlipHorizontally();
                    final boolean flipY = cell.getFlipVertically();
                    final int rotations = cell.getRotation();

                    TextureRegion region = tile.getTextureRegion();

                    float x1 = x + tile.getOffsetX() * unitScale;
                    float y1 = y + tile.getOffsetY() * unitScale;
                    float x2 = x1 + region.getRegionWidth() * unitScale;
                    float y2 = y1 + region.getRegionHeight() * unitScale;

                    float u1 = region.getU();
                    float v1 = region.getV2();
                    float u2 = region.getU2();
                    float v2 = region.getV();

                    float darkness = brightness[col][row].getPercentage();
                    final float color = Color
                        .toFloatBits(batchColor.r * darkness, batchColor.g * darkness, batchColor.b * darkness,
                                     batchColor.a * layer.getOpacity());

                    vertices[X1] = x1;
                    vertices[Y1] = y1;
                    vertices[C1] = color;
                    vertices[U1] = u1;
                    vertices[V1] = v1;

                    vertices[X2] = x1;
                    vertices[Y2] = y2;
                    vertices[C2] = color;
                    vertices[U2] = u1;
                    vertices[V2] = v2;

                    vertices[X3] = x2;
                    vertices[Y3] = y2;
                    vertices[C3] = color;
                    vertices[U3] = u2;
                    vertices[V3] = v2;

                    vertices[X4] = x2;
                    vertices[Y4] = y1;
                    vertices[C4] = color;
                    vertices[U4] = u2;
                    vertices[V4] = v1;

                    if (flipX) {
                        float temp = vertices[U1];
                        vertices[U1] = vertices[U3];
                        vertices[U3] = temp;
                        temp = vertices[U2];
                        vertices[U2] = vertices[U4];
                        vertices[U4] = temp;
                    }
                    if (flipY) {
                        float temp = vertices[V1];
                        vertices[V1] = vertices[V3];
                        vertices[V3] = temp;
                        temp = vertices[V2];
                        vertices[V2] = vertices[V4];
                        vertices[V4] = temp;
                    }
                    if (rotations != 0) {
                        switch (rotations) {
                            case Cell.ROTATE_90: {
                                float tempV = vertices[V1];
                                vertices[V1] = vertices[V2];
                                vertices[V2] = vertices[V3];
                                vertices[V3] = vertices[V4];
                                vertices[V4] = tempV;

                                float tempU = vertices[U1];
                                vertices[U1] = vertices[U2];
                                vertices[U2] = vertices[U3];
                                vertices[U3] = vertices[U4];
                                vertices[U4] = tempU;
                                break;
                            }
                            case Cell.ROTATE_180: {
                                float tempU = vertices[U1];
                                vertices[U1] = vertices[U3];
                                vertices[U3] = tempU;
                                tempU = vertices[U2];
                                vertices[U2] = vertices[U4];
                                vertices[U4] = tempU;
                                float tempV = vertices[V1];
                                vertices[V1] = vertices[V3];
                                vertices[V3] = tempV;
                                tempV = vertices[V2];
                                vertices[V2] = vertices[V4];
                                vertices[V4] = tempV;
                                break;
                            }
                            case Cell.ROTATE_270: {
                                float tempV = vertices[V1];
                                vertices[V1] = vertices[V4];
                                vertices[V4] = vertices[V3];
                                vertices[V3] = vertices[V2];
                                vertices[V2] = tempV;

                                float tempU = vertices[U1];
                                vertices[U1] = vertices[U4];
                                vertices[U4] = vertices[U3];
                                vertices[U3] = vertices[U2];
                                vertices[U2] = tempU;
                                break;
                            }
                        }
                    }
                    batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
                }
                x += layerTileWidth;
            }
            y -= layerTileHeight;
        }
    }

    @Override
    public void render() {
        beginRender();
        MapLayers mls = map.getLayers();
        for (int i = 0, size = mls.getCount(); i < size; i++) {
            renderMapLayer(mls.get(i));
        }
        endRender();
    }

    private void test() {
        SimpleOrthogonalTiledMapRendererTest mapRendererTest = new SimpleOrthogonalTiledMapRendererTest();

        mapRendererTest.updateSkylightsAt(this);
        mapRendererTest.updateSkylightsAll(this);
        mapRendererTest.testLightSources(this);
        Gdx.app.exit();
    }

    HashMap<Vector2, LightLevel> getLightSources() {
        return lightSources;
    }
}
