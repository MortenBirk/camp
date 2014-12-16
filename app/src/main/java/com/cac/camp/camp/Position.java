package com.cac.camp.camp;

/**
 * Created by Birk on 16-12-2014.
 */


public class Position {

    private double longitude;
    private double lattitude;

    public Position(double latti, double longi) {
        longitude = longi;
        lattitude = latti;
    }

    public void setLongitude(double d) {
        longitude = d;
    }

    public void setLattitude(double d) {
        lattitude = d;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

}

