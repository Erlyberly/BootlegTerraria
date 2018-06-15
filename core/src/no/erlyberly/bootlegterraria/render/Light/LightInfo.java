package no.erlyberly.bootlegterraria.render.light;

import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.util.Vector2Int;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LightInfo {

    private final HashMap<Vector2Int, Float> litFrom; // where this light is lit from

    private LightLevel currLL; //the current calculated light level
    private LightLevel emitting; //How much brightness this tile is emitting (not including skylight)
    private final Vector2 posf;
    private final Vector2Int posi;

    private boolean skylight;

    LightInfo(final Vector2Int pos) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.currLL = LightLevel.LVL_0;
        this.litFrom = new HashMap<>();
        this.posf = new Vector2(pos.x, pos.y);
        this.posi = pos;
        this.skylight = false;
    }

    LightInfo(final int blockX, final int blockY) {
        this(new Vector2Int(blockX, blockY));
    }

    /**
     * @param src
     *     The source of the light
     * @param srcBrt
     *     The brightness of the source light
     * @param skylight
     *     If this light is a skylight or not
     */
    public void put(final Vector2Int src, final LightLevel srcBrt, final boolean skylight) {
        if (srcBrt == LightLevel.LVL_0) {
            remove(src, skylight);
            return;
        }
        final float brightness;
        if (this.posi.equals(src)) {
            brightness = srcBrt.getLvl();
            if (skylight) {
                //mark this tile as a light info tile
                this.skylight = true;
            }
            else {
                //take a not of what this tile would be emitting if it wasn't a skylight
                this.emitting = srcBrt;
                if (this.skylight) {
                    //do not update the brightness if it is already skylight (as skylight should always overwrite
                    // tile light)
                    return;
                }
            }
        }
        else {
            brightness = srcBrt.getLvl() - this.posf.dst(src.x, src.y);
        }

        //No light when brightness is less than 0 (this point is out of the light radius)
        if (brightness <= 0) {
            return;
        }
        this.litFrom.put(src, brightness);

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
//        if (!this.skylight && skylight) {
//            //do nothing when trying to remove the skylight, but this isn't
//            return;
//        }
        if (this.litFrom.containsKey(src) || skylight && this.posi.equals(src)) {
            this.litFrom.remove(src);
            if (this.posi.equals(src)) {
                if (!this.skylight && skylight) {
                    return;
                }
                if (skylight /*&& this.skylight*/) { //this was a skylight, but now it's not
                    this.skylight = false;
                }
                else /*if (!skylight)*/ {
                    this.emitting = LightLevel.LVL_0;
                }
            }
            calculateLightLevel();
        }
    }

    /**
     * Calculate the brightness of this based on the objects that shine on it
     */
    private void calculateLightLevel() {
        //if nothing is shining of it the light level must be 0
        if (this.litFrom.isEmpty()) {
            this.currLL = LightLevel.LVL_0;
            return;
        }
        //FIXME threw a NoSuchElementException from Collections.max
        this.currLL = LightLevel.valueOf(Collections.max(this.litFrom.values()));
    }

    public LightLevel getLightLevel() {
        return this.currLL;
    }

    public Map<Vector2Int, Float> litFrom() {
        return this.litFrom;
    }

    Vector2 getPosf() {
        return this.posf;
    }

    Vector2Int getPosi() {
        return this.posi;
    }

    public boolean isSkylight() {
        return this.skylight;
    }

    public LightLevel getEmitting() {
        return this.emitting;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof LightInfo)) { return false; }

        final LightInfo info = (LightInfo) o;

        return this.posi.equals(info.posi);
    }

    @Override
    public int hashCode() {
        return this.posi.hashCode();
    }

    @Override
    public String toString() {
        return "LightInfo{" + "posi=" + this.posi + ", currLL=" + this.currLL + ", emitting=" + this.emitting +
               ", skylight=" + this.skylight + ", lit from " + this.litFrom.size() + " different places" + '}';
    }
}
