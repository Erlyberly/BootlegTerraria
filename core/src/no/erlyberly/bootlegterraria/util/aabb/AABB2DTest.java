package no.erlyberly.bootlegterraria.util.aabb;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AABB2DTest {

    @Test
    void overlap() {

        AABB2D tst1 = new AABB2D(0, 10, 0, 10);
        AABB2D tst2 = new AABB2D(5, 15, 5, 15);
        AABB2D tst3 = new AABB2D(15, 18, 5, 15);

        assertTrue(tst1.overlap(tst1));

        assertTrue(tst1.overlap(tst2));
        assertTrue(tst2.overlap(tst1));

        assertFalse(tst1.overlap(tst3));
        assertFalse(tst3.overlap(tst1));
    }

    @Test
    void hasPoint() {
        AABB2D tst1 = new AABB2D(0, 10, 0, 10);

        assertFalse(tst1.hasPoint(new Vector2(-1, -1)));
        assertFalse(tst1.hasPoint(new Vector2(0, -1)));
        assertFalse(tst1.hasPoint(new Vector2(-1, 0)));

        assertTrue(tst1.hasPoint(new Vector2(0, 0)));
        assertTrue(tst1.hasPoint(new Vector2(5, 5)));

        assertFalse(tst1.hasPoint(new Vector2(10, 10)));
    }


    @Test
    void iterator() {
        AABB2D tst1 = new AABB2D(0, 2, 0, 2);

        ArrayList<Vector2> act = new ArrayList<>();

        for (Vector2 v : tst1) {
            act.add(v);
        }
        Vector2[] actv = new Vector2[act.size()];
        actv = act.toArray(actv);
        Vector2[] exp = {new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, 1), new Vector2(1, 1)};
        
        assertArrayEquals(exp, actv);

    }
}