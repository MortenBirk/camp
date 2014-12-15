package com.cac.camp.camp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Birk on 15-12-2014.
 */
public class SensorHandler implements SensorEventListener {
    private Activity activity;
    private boolean isSlave;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<DataPoint> dataPoints;
    private List<DataWindow> dataWindows;

    public SensorHandler(Activity activity) {
        this.activity = activity;
        mSensorManager = (SensorManager)activity.getSystemService(Activity.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        dataPoints = new ArrayList<DataPoint>();
        dataWindows = new ArrayList<DataWindow>();

        isSlave = true;
        if (isSlave) {
            // - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public List<DataWindow> getDataWindows() {
        return dataWindows;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Right in here is where you put code to read the current sensor values and
        //update any views you might have that are displaying the sensor information
        //You'd get accelerometer values like this:
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        DataPoint dp = new DataPoint(x, y, z);
        dataPoints.add(dp);

        if (dataPoints.size() % 64 == 0 && dataPoints.size() >= 128) {
            Log.d("3", "Start: Add window");
            int counter = dataWindows.size();
            List<DataPoint> windowedDataPoints = dataPoints.subList(counter * 64, counter * 64 + 127);
            dataWindows.add(new DataWindow(windowedDataPoints));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onPause() {
        mSensorManager.unregisterListener(this);
    }

    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }
}
