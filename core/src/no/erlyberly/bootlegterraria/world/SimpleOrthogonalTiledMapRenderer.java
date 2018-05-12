package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.google.common.base.Preconditions;

import java.util.Iterator;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

public class SimpleOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {

    private int[] topBlocking;


    public static final float LIGHT_CUTOFF = 0.33f;


    public SimpleOrthogonalTiledMapRenderer(TiledMap map) {
        super(map);

        //get the width of the map
        MapProperties prop = map.getProperties();
        int mapWidth = prop.get("width", Integer.class);

        topBlocking = new int[mapWidth];
        for (int i = 0; i < mapWidth; i++) {
            topBlocking[i] = Integer.MIN_VALUE;
        }
    }

    public void updateSurfaceBlockAll() {
        updateSurfaceBlock(0, topBlocking.length);
    }

    /**
     * Update the surfaceblocks between {@code min} and {@code max}
     *
     * @param min
     *     The minimum
     */
    public void updateSurfaceBlock(int min, int max) {
        Preconditions.checkArgument(min >= 0, "Minimum argument must be greater than or equal to 0");
        Preconditions.checkArgument(max <= topBlocking.length,
                                    "Maximum argument must be less than or equal to topBlocking.length");
        Preconditions.checkArgument(min >= max, "Minimum argument must be less than maximum argument");

        for (Iterator<MapLayer> it = map.getLayers().iterator(); it.hasNext(); ) {
            MapLayer layer = it.next();
            if (layer.isVisible() && layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
                for (int x = min; x < max; x++) {
                    for (int y = 0; y < tiledLayer.getHeight(); y++) {
                        //check if the current cell is collidable
                        Cell cell = tiledLayer.getCell(x, y);
                        TileType tt = TileType.getTileTypeById(cell.getTile().getId());
                        if (tt.isCollidable()) {
                            topBlocking[x] = topBlocking[x] < y ? y : topBlocking[x];
                            break; //no further looping required
                        }
                    }
                }
            }
        }
    }

    public void updateSurfaceBlockAt(int x) {
        updateSurfaceBlock(x, x + 1);
    }

    private void updateSurfaceBlockAt0(int x, TiledMapTileLayer tiledLayer) {

    }

    /**
     * @return A copy of the blocking lighting positions
     */
    public int[] getLightBlockBlocks() {
        int[] cpy = new int[topBlocking.length];
        System.arraycopy(topBlocking, 0, cpy, 0, topBlocking.length);
        return cpy;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        final Color batchColor = batch.getColor();
        final float color =
            Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());

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

                if (tile != null) {
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
}
