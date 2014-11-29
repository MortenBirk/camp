package com.cac.camp.camp;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jensemil on 28/11/14.
 */
public class DataWindow {
    private List<DataPoint> dataPoints;

    public DataWindow(List<DataPoint> dataPoints) {

        this.dataPoints = new CopyOnWriteArrayList<DataPoint>(dataPoints);
    }

    @Override
    public String toString() {
        String dataString = "";
        for (DataPoint dp : dataPoints) {
            dataString += dp.toString() + "\n";
        }
        return dataString;
    }

    public double getMax() {
        double max = 0;
        for (DataPoint dp : this.dataPoints) {
            if (dp.getMagnitude() > max) {
                max = dp.getMagnitude();
            }
        }
        return max;
    }

    public double getMin() {
        double min = Double.MAX_VALUE;
        for (DataPoint dp : this.dataPoints) {
            if (dp.getMagnitude() < min) {
                min = dp.getMagnitude();
            }
        }
        return min;
    }

    public double getIntegral() {
        double integral = 0;
        for (DataPoint dp : this.dataPoints) {
            integral += dp.getMagnitude();
        }
        return integral;
    }

    public double getMean() {
        double mean = 0;
        for (DataPoint dp : this.dataPoints) {
            double m = dp.getMagnitude();
            mean += m;
        }
        return mean / this.dataPoints.size();
    }

    public double getStdDev() {
        double mean = this.getMean();
        double stdDev = 0;
        for (DataPoint dp : this.dataPoints) {
            double m = dp.getMagnitude();
            stdDev += (m - mean)*(m - mean);
        }

        return Math.sqrt(stdDev / this.dataPoints.size());
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }
}
