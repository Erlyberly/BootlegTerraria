package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class SimpleOrthogonalTiledMapRendererTest {

    private static HeadlessApplication headlessApp;

    @BeforeAll
    static void before() {
        final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        headlessApp = new HeadlessApplication(new ApplicationAdapter() { }, config);
    }

    @Test
    void updateSurfaceBlockAll() {
        headlessApp.postRunnable(() -> {
            SimpleOrthogonalTiledMapRenderer renderer =
                new SimpleOrthogonalTiledMapRenderer(new TmxMapLoader().load("map.tmx"));
            renderer.updateSurfaceBlockAll();
        });
    }

    @Test
    void updateSurfaceBlock() {
        headlessApp.postRunnable(() -> {

        });
    }

    @Test
    void updateSurfaceBlockAt() {
        headlessApp.postRunnable(() -> {

        });
    }

    @AfterAll
    static void tearDown() {
        headlessApp.exit();
    }
}