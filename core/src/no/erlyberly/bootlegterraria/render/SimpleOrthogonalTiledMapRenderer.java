package no.erlyberly.bootlegterraria.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.Light.LightLevel;
import no.erlyberly.bootlegterraria.render.Light.api.Vector2Int;
import no.erlyberly.bootlegterraria.util.CancellableThreadScheduler;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.HashMap;

import static com.badlogic.gdx.graphics.g2d.Batch.*;
import static no.erlyberly.bootlegterraria.render.Light.LightLevel.*;

public class SimpleOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {

    private final int mapWidth;
    private final int mapHeight;
    private final int[] skyLight;
    private final HashMap<Vector2Int, LightLevel> lightSources;
    private int[][][] brightness;

    public static boolean logLightTime = true;

    public static final LightLevel SKY_LIGHT_BRIGHTNESS = LightLevel.LVL_7;
    private static final int MIN_LIGHT_DEPTH = -1;

    private static final int LIGHT_INFO_SIZE = 3; //how much information we have at each block

    private static final int LIGHT_INFO_BRIGHTNESS = 0; //how bright this block should be
    private static final int LIGHT_INFO_SOURCES = 1; //how many light sources shine at this block
    private static final int LIGHT_INFO_CALC_LL = 2; //the calculated light level at this location

    private static final CancellableThreadScheduler LIGHT_THREAD = new CancellableThreadScheduler();

    private static final Texture BLACK_TEXTURE;

    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        BLACK_TEXTURE = new Texture(pixmap);
    }


    public SimpleOrthogonalTiledMapRenderer(final TiledMap map, final boolean test) {
        super(map);

        //get the width of the map
        final MapProperties prop = map.getProperties();
        this.mapWidth = prop.get("width", Integer.class);
        this.mapHeight = prop.get("height", Integer.class);

        GameMain.backgroundColor = Util.convert(prop.get("backgroundcolor", "#FFFF0000", String.class));
        this.skyLight = new int[this.mapWidth];
        this.lightSources = new HashMap<>();
        this.brightness = new LightLevel[this.mapWidth][this.mapHeight];

        for (int x = 0; x < this.brightness.length; x++) {
            for (int y = 0; y < this.brightness[x].length; y++) {
                this.brightness[x][y] = LightLevel.LVL_0;
            }
        }

        for (int i = 0; i < this.mapWidth; i++) {
            this.skyLight[i] = MIN_LIGHT_DEPTH;
        }
        if (test) {
            test();
        }
        else {
            asyncUpdateLights();
        }
    }

    public void asyncUpdateLights() {
        LIGHT_THREAD.cancelTasks(); //cancel previous lighting tasks
        asyncUpdateLightBetween(0, this.skyLight.length);
    }

    /**
     * Update the skylight between {@code min} and {@code max}
     *
     * @param min
     *     The minimum x value to check (inclusive)
     * @param max
     *     The maximum x value to check (exclusive)
     */
    public void asyncUpdateLightBetween(final int min, final int max) {

        Preconditions.checkArgument(min >= 0, "Minimum argument must be greater than or equal to 0");
        Preconditions.checkArgument(max <= this.skyLight.length,
                                    "Maximum argument must be less than or equal to skyLight.length");
        Preconditions.checkArgument(min < max, "Minimum argument must be less than maximum argument");

        LIGHT_THREAD.execute(() -> {
            final long startTime = System.currentTimeMillis();
            //remove all light sources between min and max
            this.lightSources.entrySet().removeIf(entry -> entry.getKey().x >= min && entry.getKey().x < max);

            for (final MapLayer layer : this.map.getLayers()) {
                if (layer.isVisible() && layer instanceof TiledMapTileLayer) {
                    final TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
                    final int height = tiledLayer.getHeight();
                    for (int x = min; x < max; x++) {
                        this.skyLight[x] = MIN_LIGHT_DEPTH; //reset skylight if already set
                        boolean skyFound = false;
                        for (int y = height - 1; y >= 0; y--) {
                            //check if the current cell is collidable
                            final Cell cell = tiledLayer.getCell(x, y);
                            //batch.setColor(1.0f, 1.0f, 1.0f, brightness[x][y].getPercentage());
                            if (cell == null) {
                                //empty cell
                                continue;
                            }
                            //int yLoc = height - y - 1; //actual map y loc
                            final TileType tt = TileType.getTileTypeById(cell.getTile().getId());
                            if (!skyFound && tt.isCollidable()) {
                                this.skyLight[x] = this.skyLight[x] < y ? y - 1 : this.skyLight[x];
                                skyFound = true; //no further looping required
                            }
                            if (tt.getLuminosity() != LightLevel.LVL_0) {
                                this.lightSources.put(new Vector2(x, y), tt.getLuminosity());
                            }
                        }
                    }
                }
            }
            for (int x = 0; x < this.skyLight.length; x++) {
                for (int y = this.mapHeight - 1, length = Math.max(-1, this.skyLight[x]); y > length; y--) {
                    final Vector2 v = new Vector2(x, y);
                    this.lightSources.put(v, SKY_LIGHT_BRIGHTNESS);
                }
            }
            calculateLight(0, this.skyLight.length);
//            calculateLight(min, max);
            if (logLightTime) {
                GameMain.getConsoleHandler()
                        .log("Light update took " + (System.currentTimeMillis() - startTime) + " ms");
            }
        });
    }

    public void asyncUpdateLightAt(final int x) {
        Preconditions.checkArgument(x >= 0, "x must be greater than 0");
        Preconditions.checkArgument(x < this.skyLight.length, "x must be less than the width of the world");

        asyncUpdateLightBetween(x, x + 1);
    }

    /**
     * @return A copy of the blocking lighting positions
     */
    public int[] getSkyLights() {
        final int[] cpy = new int[this.skyLight.length];
        System.arraycopy(this.skyLight, 0, cpy, 0, this.skyLight.length);
        return cpy;
    }

    private LightLevel[][] cpyBrightness() {
        final LightLevel[][] newBrightness = new LightLevel[this.mapWidth][this.mapHeight];
        System.arraycopy(this.brightness, 0, newBrightness, 0, this.skyLight.length);
        return newBrightness;
    }

    private void calculateLight(final int rawMin, final int rawMax) {
        Preconditions.checkArgument(rawMin >= 0, "Minimum argument must be greater than or equal to 0");
        Preconditions.checkArgument(rawMax <= this.skyLight.length,
                                    "Maximum argument must be less than or equal to skyLight.length");
        Preconditions.checkArgument(rawMin < rawMax, "Minimum argument must be less than maximum argument");

        final int calculatedMin = Math.max(0, rawMin - LIGHT_LEVELS);
        final int calculatedMax = Math.min(this.skyLight.length, rawMax + LIGHT_LEVELS);

        //copy the current brightness
        final LightLevel[][] newBrightness = cpyBrightness();

        //reset the lights between min and max
        for (int x = calculatedMin; x < calculatedMax; x++) {
            for (int y = 0; y < newBrightness[x].length; y++) {
                newBrightness[x][y] = LightLevel.LVL_0;
            }
        }


        this.lightSources.forEach((coord, lightLevel) -> {
            if (coord.x >= calculatedMin && coord.x < calculatedMax) {
                newBrightness[(int) coord.x][(int) coord.y] = lightLevel;
            }
        });

        //do a pass for right-to-left light then a pass for left-to-right light
        for (int x = calculatedMin; x < calculatedMax; x++) {
            calculateLightColumn(x, newBrightness);
        }
        for (int x = calculatedMax - 1; x >= calculatedMin; x--) {
            calculateLightColumn(x, newBrightness);
        }

        //make sure we get no concurrency errors
        Gdx.app.postRunnable(() -> this.brightness = newBrightness);
    }

    @Deprecated
    //TODO remove when done debugging
    public void calculateLightAt(int x, int y, LightLevel newLightLevel) {
        calculateLightAt(x, y, newLightLevel, cpyBrightness());
    }

    //Clean all light in radius of the current light at xy
    //then for every light source that's MAX_LIGHT_LEVEL away,
    // recalculate for all light of that block that's within the blacked out radius
    //note: that's not what's implemented below as of yet
    //TODO implement the lighting over
    //TODO remove this message then the above todo is done
    private void calculateLightAt(int blockX, int blockY, LightLevel newLightLevel,
                                  final LightLevel[][] newBrightness) {
        System.out.println("blockX = [" + blockX + "], blockY = [" + blockY + "], newLightLevel = [" + newLightLevel +
                           "], newBrightness = [" + "]");
        LightLevel currLevel = newBrightness[blockX][blockY];
        if (newLightLevel != LVL_0) {
            this.lightSources.put(new Vector2(blockX, blockY), newLightLevel);
        }

        //How far the light at x,y travel
        final int lightRadius =
            (newLightLevel.getLvl() > currLevel.getLvl() ? newLightLevel.getLvl() : currLevel.getLvl()) - 1;

        //start coords, top right
        final int x0 = Util.between(0, blockX - lightRadius, this.mapWidth);
        final int y0 = Util.between(0, blockY - lightRadius, this.mapHeight);

        //end coords, lower left
        final int x1 = Util.between(0, blockX + lightRadius, this.mapWidth);
        final int y1 = Util.between(0, blockY + lightRadius, this.mapHeight);


        //reset light for the affected area
        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                newBrightness[x][y] = LightLevel.LVL_0;
            }
        }

//        calculateLightColumn(blockX, newBrightness);

        AABB2D orgAABB = new AABB2D(x0, x1, y0, y1);
        System.out.println("orgAABB = " + orgAABB);
        this.lightSources.forEach((pos, level) -> {
            AABB2D lightSourceAABB = Util.fromLight(pos, level);
            if (orgAABB.overlap(lightSourceAABB)) {
                newBrightness[(int) pos.x][(int) pos.y] = level;
                System.out.print("lightSourceAABB = " + lightSourceAABB);
                System.out.println(" | pos = " + pos);
                for (Vector2 rv : lightSourceAABB) {
                    if (orgAABB.hasPoint(rv)) {
                        System.out.println("rv = " + rv);
                        boolean relX = blockX >= rv.x;
                        boolean relY = blockY >= rv.y;

                        System.out.print("relX = " + relX);
                        System.out.println(" | relY = " + relY);

                        float dx;
                        float dy;

                        if (relX && relY) {
                            dx = blockX - rv.x;
                            dy = blockY - rv.y;
                        }
                        else if (relX && !relY) {
                            dx = blockX + rv.x;
                            dy = blockY - rv.y;
                        }
                        else if (!relX && relY) {
                            dx = blockX - rv.x;
                            dy = blockY + rv.y;
                        }
                        else /*if(!relX && !relY)*/ {
                            dx = blockX + rv.x;
                            dy = blockY + rv.y;
                        }
                        newBrightness[(int) rv.x][(int) rv.y] = lightLevelDelta((int) dx, (int) dy, level);
                    }
                }
            }
        });
        Gdx.app.postRunnable(() -> this.brightness = newBrightness);
    }

    private LightLevel lightLevelDelta(float dx, float dy, LightLevel initialLightLevel) {
        int initialLvl = initialLightLevel.getLvl();
        int delta = (int) (dx > dy ? dx : dy);
        return LightLevel.valueOf(initialLvl - delta);
    }

    private void calculateLightColumn(final int x, final LightLevel[][] newBrightness) {
        for (int y = 0, length = newBrightness[x].length; y < length; y++) {
            final LightLevel currBrightness = newBrightness[x][y];
            final LightLevel dimmer = currBrightness.dimmer();
            if (currBrightness == LVL_0 || currBrightness == LVL_1) {
                continue;
            }

            for (int x1 = Math.max(x - 1, 0), maxX = Math.min(x + 1, newBrightness.length - 1); x1 <= maxX; x1++) {
                for (int y1 = Math.max(y - 1, 0), maxY = Math.min(y + 1, newBrightness[x].length - 1);
                     y1 <= maxY; y1++) {
                    if (newBrightness[x1][y1].getLvl() < currBrightness.getLvl()) {
                        newBrightness[x1][y1] = dimmer;
                    }
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void renderTileLayer(final TiledMapTileLayer layer) {
        final Color batchColor = this.batch.getColor();

        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();

        final float layerTileWidth = layer.getTileWidth() * this.unitScale;
        final float layerTileHeight = layer.getTileHeight() * this.unitScale;

        final float layerOffsetX = layer.getRenderOffsetX() * this.unitScale;
        // offset in tiled is y down, so we flip it
        final float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale;

        final int col1 = Math.max(0, (int) ((this.viewBounds.x - layerOffsetX) / layerTileWidth));
        final int col2 = Math.min(layerWidth,
                                  (int) ((this.viewBounds.x + this.viewBounds.width + layerTileWidth - layerOffsetX) /
                                         layerTileWidth));

        final int row1 = Math.max(0, (int) ((this.viewBounds.y - layerOffsetY) / layerTileHeight));
        final int row2 = Math.min(layerHeight,
                                  (int) ((this.viewBounds.y + this.viewBounds.height + layerTileHeight - layerOffsetY) /
                                         layerTileHeight));

        float y = row2 * layerTileHeight + layerOffsetY;
        final float xStart = col1 * layerTileWidth + layerOffsetX;
        final float[] vertices = this.vertices;


        Texture texture = null;

        for (int row = row2; row >= row1; row--) {
            float x = xStart;
            for (int col = col1; col < col2; col++) {
                final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) {
                    x += layerTileWidth;
                    continue;
                }
                final TiledMapTile tile = cell.getTile();

                if (tile != null) {

                    final boolean flipX = cell.getFlipHorizontally();
                    final boolean flipY = cell.getFlipVertically();
                    final int rotations = cell.getRotation();

                    final TextureRegion region = tile.getTextureRegion();

                    final float x1 = x + tile.getOffsetX() * this.unitScale;
                    final float y1 = y + tile.getOffsetY() * this.unitScale;
                    final float x2 = x1 + region.getRegionWidth() * this.unitScale;
                    final float y2 = y1 + region.getRegionHeight() * this.unitScale;

                    final float u1 = region.getU();
                    final float v1 = region.getV2();
                    final float u2 = region.getU2();
                    final float v2 = region.getV();

                    final float darkness = this.brightness[col][row].getPercentage();
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
                                final float tempV = vertices[V1];
                                vertices[V1] = vertices[V2];
                                vertices[V2] = vertices[V3];
                                vertices[V3] = vertices[V4];
                                vertices[V4] = tempV;

                                final float tempU = vertices[U1];
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
                                final float tempV = vertices[V1];
                                vertices[V1] = vertices[V4];
                                vertices[V4] = vertices[V3];
                                vertices[V3] = vertices[V2];
                                vertices[V2] = tempV;

                                final float tempU = vertices[U1];
                                vertices[U1] = vertices[U4];
                                vertices[U4] = vertices[U3];
                                vertices[U3] = vertices[U2];
                                vertices[U2] = tempU;
                                break;
                            }
                        }
                    }
                    if (this.brightness[col][row] == LVL_0) {
                        texture = BLACK_TEXTURE;
                    }
                    else {
                        texture = region.getTexture();
                    }
                    this.batch.draw(texture, vertices, 0, NUM_VERTICES);
                }
                x += layerTileWidth;
            }
            y -= layerTileHeight;
        }
    }

    /**
     * Manually overwrite this to fix an internal LibGDX array reusability error
     */
    @Override
    public void render() {
        beginRender();
        final MapLayers mls = this.map.getLayers();
        for (int i = 0, size = mls.getCount(); i < size; i++) {
            renderMapLayer(mls.get(i));
        }
        endRender();
    }

    private void test() {
        final SimpleOrthogonalTiledMapRendererTest mapRendererTest = new SimpleOrthogonalTiledMapRendererTest();

        mapRendererTest.updateSkylightsAt(this);
        mapRendererTest.updateSkylightsAll(this);
        mapRendererTest.testLightSources(this);
        Gdx.app.exit();
    }

    HashMap<Vector2, LightLevel> getLightSources() {
        return this.lightSources;
    }

    public LightLevel lightAt(int x, int y) {
        return this.brightness[x][y];
    }
}
