package com.cac.camp.camp;

/**
 * Created by jensemil on 28/11/14.
 */
public class DataPoint {
    private float x;
    private float y;
    private float z;

    public DataPoint(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
