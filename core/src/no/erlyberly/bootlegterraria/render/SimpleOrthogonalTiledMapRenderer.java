package no.erlyberly.bootlegterraria.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.light.BlockLightMap;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.util.Loadable;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

public class SimpleOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer implements Loadable {

    public static boolean logLightEvents = false;

    private final BlockLightMap light;

    private static final TextureRegion BLACK_TEXTURE;

    static {
        if (!GameMain.HEADLESS) {
            final Pixmap pixmap = new Pixmap((int) TileType.TILE_SIZE, (int) TileType.TILE_SIZE, Pixmap.Format.RGB888);
            pixmap.setColor(Color.BLACK);
            pixmap.fill();
            BLACK_TEXTURE = new TextureRegion(new Texture(pixmap));
        }
        else { BLACK_TEXTURE = null; }
    }


    public SimpleOrthogonalTiledMapRenderer(final TiledMap map, final GameMap gameMap) {
        super(map);

        //get the width of the map
        final MapProperties prop = map.getProperties();
        GameMain.backgroundColor = Util.convert(prop.get("backgroundcolor", "#FFFF0000", String.class));

        this.light = new BlockLightMap(gameMap);
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

        for (int row = row2; row >= row1; row--) {
            float x = xStart;
            for (int col = col1; col < col2; col++) {
                final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                final LightLevel ll = this.light.lightAt(col, row);

                //do not draw skylight
                if (ll == LightLevel.LVL_8 && cell == null) {
                    x += layerTileWidth;
                    continue;
                }

                final float darkness = ll.getPercentage();


                final TiledMapTile tile;
                boolean flipX = false;
                boolean flipY = false;
                int rotations = 0;

                float tileOffsetX = 0;
                float tileOffsetY = 0;

                final TextureRegion region;
                final float color;

                if (cell != null) {
                    tile = cell.getTile();
                    flipX = cell.getFlipHorizontally();
                    flipY = cell.getFlipVertically();
                    rotations = cell.getRotation();

                    region = tile.getTextureRegion();
                    tileOffsetX = tile.getOffsetX();
                    tileOffsetY = tile.getOffsetY();
                    color = Color.toFloatBits(batchColor.r * darkness, batchColor.g * darkness, batchColor.b * darkness,
                                              batchColor.a * layer.getOpacity());
                }
                else {
                    region = BLACK_TEXTURE;
                    color = Color.toFloatBits(0, 0, 0, 0.95f - darkness);
                }

                final float x1 = x + tileOffsetX * this.unitScale;
                final float y1 = y + tileOffsetY * this.unitScale;
                final float x2 = x1 + region.getRegionWidth() * this.unitScale;
                final float y2 = y1 + region.getRegionHeight() * this.unitScale;

                final float u1 = region.getU();
                final float v1 = region.getV2();
                final float u2 = region.getU2();
                final float v2 = region.getV();


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
                this.batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
//                }
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

    public BlockLightMap getLight() {
        return this.light;
    }

    @Override
    public boolean isInitialized() {
        return this.light.isInitialized();
    }
}
