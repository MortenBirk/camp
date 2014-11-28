package com.cac.camp.camp;

/**
 * Created by jensemil on 28/11/14.
 */
public class DataPoint {
    private float x;
    private float y;
    private float z;
    private String className;

    public DataPoint(float x, float y, float z, String className) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.className = className;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ") --> " + className;
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

    public double getMagnitude() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public String getClassName() {
        return this.className;
    }
}
