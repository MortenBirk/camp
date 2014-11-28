package com.cac.camp.camp;

import java.util.List;

/**
 * Created by jensemil on 28/11/14.
 */
public class DataWindow {
    private List<DataPoint> dataPoints;

    public DataWindow(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    @Override
    public String toString() {
        String dataString = "";
        for (DataPoint dp : dataPoints) {
            dataString += dp.toString() + "\n";
        }
        return dataString;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }
}
