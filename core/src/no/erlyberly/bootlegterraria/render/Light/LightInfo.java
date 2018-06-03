package no.erlyberly.bootlegterraria.render.light;

import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.util.Vector2Int;

import java.util.HashMap;
import java.util.Optional;

public class LightInfo {

    private LightLevel currLL; //the current calculated light level
    private final HashMap<Vector2Int, Float> litFrom; // where this light is lit from
    //    private final Vector2Int pos;
    private final Vector2 posf;


    public LightInfo(int blockX, int blockY) {
        this.currLL = LightLevel.LVL_0;
        this.posf = new Vector2(blockX, blockY);
        this.litFrom = new HashMap<>();
    }

    /**
     * @param src
     *     The source of the light
     * @param srcBrt
     *     The brightness of the source light
     */
    public void put(Vector2Int src, LightLevel srcBrt) {
//        System.out.println("src = [" + src + "], srcBrt = [" + srcBrt + "]");
        if (srcBrt == LightLevel.LVL_0) {
            System.out.println("removing light at " + src);
            remove(src);
            return;
        }
        float dist = srcBrt.getLvl() - this.posf.dst(src.x, src.y);
        //No light when brightness is less than 0 (this point is out of the light radius)
        if (dist <= 0) {
            return;
        }
        this.litFrom.put(src, dist);

        calculateLightLevel();
    }

    /**
     * Remove a light from the set of objects that light this tile
     *
     * @param v
     *     The source to remove
     */
    public void remove(Vector2Int v) {
        if (this.litFrom.containsKey(v)) {
            this.litFrom.remove(v);
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
//        System.out.println("litFrom = " + this.litFrom);
        Optional<Float> max = this.litFrom.values().stream().max(Float::compareTo);
        if (max.isPresent()) {
//            System.out.print("max = " + max);
//            System.out.println("| Math.round(avgBrightness.getAsDouble()) = " + Math.round(max.getAsDouble()));
            this.currLL = LightLevel.valueOf(Math.round(max.get()));
        }
        else {
            this.currLL = LightLevel.LVL_0;
        }
    }

    public LightLevel getLightLevel() {
        return this.currLL;
    }

    public Vector2 getPos() {
        return this.posf;
    }
}
