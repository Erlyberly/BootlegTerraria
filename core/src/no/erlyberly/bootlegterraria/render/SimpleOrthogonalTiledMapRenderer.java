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
import no.erlyberly.bootlegterraria.util.Util;

import static com.badlogic.gdx.graphics.g2d.Batch.*;
import static no.erlyberly.bootlegterraria.render.light.LightLevel.LVL_0;

public class SimpleOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {

    public static boolean logLightTime = true;

    private final BlockLightMap light;

    private static final Texture BLACK_TEXTURE;

    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        BLACK_TEXTURE = new Texture(pixmap);
    }


    public SimpleOrthogonalTiledMapRenderer(final TiledMap map) {
        super(map);

        //get the width of the map
        final MapProperties prop = map.getProperties();
        int mapWidth = prop.get("width", Integer.class);
        int mapHeight = prop.get("height", Integer.class);

        GameMain.backgroundColor = Util.convert(prop.get("backgroundcolor", "#FFFF0000", String.class));

        this.light = new BlockLightMap(mapWidth, mapHeight, map);
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


        Texture texture;

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

                    LightLevel ll = this.light.lightAt(col, row);
                    final float darkness = ll.getPercentage();
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
                    if (ll == LVL_0) {
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

    public BlockLightMap getLight() {
        return this.light;
    }
}
