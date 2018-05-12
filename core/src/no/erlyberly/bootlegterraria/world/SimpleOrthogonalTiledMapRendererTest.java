package no.erlyberly.bootlegterraria.world;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SimpleOrthogonalTiledMapRendererTest {

    @Test
    void updateSurfaceBlockAll(SimpleOrthogonalTiledMapRenderer renderer) {
        System.out.println(Arrays.toString(renderer.getLightBlockBlocks()));
        renderer.updateSurfaceBlockAll();
        System.out.println(Arrays.toString(renderer.getLightBlockBlocks()));
    }

    @Test
    void updateSurfaceBlock(SimpleOrthogonalTiledMapRenderer renderer) {
    }

    @Test
    void updateSurfaceBlockAt(SimpleOrthogonalTiledMapRenderer renderer) {
        renderer.updateSurfaceBlockAt(0);
        Assertions.assertEquals(7, renderer.getLightBlockBlocks()[0]);

        renderer.updateSurfaceBlockAt(renderer.getLightBlockBlocks().length - 1);
        Assertions.assertEquals(0, renderer.getLightBlockBlocks()[renderer.getLightBlockBlocks().length - 1]);
    }
}