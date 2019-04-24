package no.erlyberly.bootlegterraria.render.light;

import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.util.Vector2Int;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LightInfo {

    private final HashMap<Vector2Int, Float> litFrom; // where this light is lit from

    private LightLevel currLL; //the current calculated light level (including skylight)
    private LightLevel emitting; //How much brightness this tile is emitting (NOT including skylight)
    private final Vector2 posf;
    private final Vector2Int posi;

    private boolean skylight; //if this light is lit by the sky

    LightInfo(final Vector2Int pos) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        currLL = LightLevel.LVL_0;
        litFrom = new HashMap<>(30);
        posf = new Vector2(pos.x, pos.y);
        posi = pos;
        skylight = false;
    }

    LightInfo(final int blockX, final int blockY) {
        this(new Vector2Int(blockX, blockY));
    }

    /**
     * Setting the {@code srcBrt} to {@link LightLevel#LVL_0} is the same as {@link #remove(Vector2Int, boolean)}
     *
     * @param src
     *     The source of the light
     * @param srcBrt
     *     The brightness of the source light
     * @param skylight
     *     If this light is a skylight or not
     */
    public void put(final Vector2Int src, final LightLevel srcBrt, final boolean skylight) {
        if (srcBrt == LightLevel.LVL_0) {
            throw new IllegalArgumentException("Cannot set the light to 0, please use the remove method");
        }

        float brightness = 0;

        //place at this light levels location
        if (posi.equals(src)) {
            brightness = srcBrt.getLvl();
            if (skylight) {
                //mark this tile as a light info tile
                this.skylight = true;
//                brightness = LightLevel.max().getLvl();
            }
            else {
                //take a note of what this tile would be emitting if it wasn't a skylight
                emitting = srcBrt;
                if (this.skylight) {
                    //do not update the brightness if it is already skylight (as skylight should always overwrite
                    // tile light)
                    return;
                }
//                brightness = srcBrt.getLvl();
            }
        }
        else {
            brightness = srcBrt.getLvl() - posf.dst(src.x, src.y);
//            brightness = (skylight ? LightLevel.LVL_8 : srcBrt).getLvl() - posf.dst(src.x, src.y);
//            if (brightness == 3.8377223) {
//                System.out.println("---\nposi = " + posi);
//                System.out.println("src = [" + src + "], srcBrt = [" + srcBrt + "], skylight = [" + skylight + "]");
//                System.out.println("litFrom MAX = " + litFrom.values().stream().max(Float::compareTo));
//                System.out.println("litFrom = " + litFrom.values());
//            }
        }

        //No light when brightness is less than 0 (this point is out of the light radius)
        if (brightness <= 0) {
            return;
        }
        litFrom.put(src, brightness);
        calculateLightLevel();
    }

    /**
     * Remove a light from the set of objects that light this tile
     *
     * @param src
     *     The source to remove
     * @param skylight
     *     If the removed light is skylight
     */
    public void remove(final Vector2Int src, final boolean skylight) {
        litFrom.remove(src);
        if (posi.equals(src)) {
//            if (!this.skylight && skylight) {
//                return;
//            }
            if (skylight /*&& this.skylight*/) { //this was a skylight, but now it's not
                this.skylight = false;
            }
            else /*if (!skylight)*/ { //this location is no longer emitting light
                emitting = LightLevel.LVL_0;
            }
        }

        calculateLightLevel();
    }


    /**
     * Calculate the brightness of this based on the objects that shine on it
     */
    private void calculateLightLevel() {
        //if nothing is shining of it the light level must be 0
        if (litFrom.isEmpty()) {
            currLL = LightLevel.LVL_0;
        }
        else if (skylight) { currLL = LightLevel.max(); }
        else { currLL = LightLevel.valueOf(Collections.max(litFrom.values())); }
    }

    public LightLevel getLightLevel() {
        return currLL;
    }

    public Map<Vector2Int, Float> litFrom() {
        return litFrom;
    }

    Vector2 getPosf() {
        return posf;
    }

    Vector2Int getPosi() {
        return posi;
    }

    public boolean isSkylight() {
        return skylight;
    }

    public LightLevel getEmitting() {
        return emitting;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof LightInfo)) { return false; }

        final LightInfo info = (LightInfo) o;

        return posi.equals(info.posi);
    }

    @Override
    public int hashCode() {
        return posi.hashCode();
    }

    @Override
    public String toString() {
        return "LightInfo{" + "posi=" + posi + ", currLL=" + currLL + ", emitting=" + emitting + ", skylight=" +
               skylight + ", lit from " + litFrom.size() + " different places" + '}';
    }
}
