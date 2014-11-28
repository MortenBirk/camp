package com.cac.camp.camp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import weka.core.converters.*;

public class LogAccActivity extends Activity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<DataPoint> dataPoints;
    private List<DataWindow> dataWindows;
    private Boolean isLogging = false;
    private int numberOfLogs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_acc);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LogAccFragment())
                    .commit();
        }


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        dataPoints = new ArrayList<DataPoint>();
        dataWindows = new ArrayList<DataWindow>();

    }


    public void runButtonClicked(View view) {
        Button button = (Button) findViewById(R.id.runButton);
        isLogging = !isLogging;
        if (isLogging) {
            button.setText("Stop log");
            //TODO - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            button.setText("Running");
            //TODO - stop the logging
            mSensorManager.unregisterListener(this);
            numberOfLogs++;
            List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(dataWindows);
            WekaDataGenerator.createArff("running" + numberOfLogs, dataWindowsCopy, "running");
            this.clearData();



        }

    }

    public void walkButtonClicked(View view) {
        Button button = (Button) findViewById(R.id.walkButton);
        isLogging = !isLogging;
        if (isLogging) {
            button.setText("Stop log");
            //TODO - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            button.setText("Walking");
            //TODO - stop the logging
            mSensorManager.unregisterListener(this);
            numberOfLogs++;
            List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(dataWindows);
            WekaDataGenerator.createArff("walk" + numberOfLogs, dataWindowsCopy, "walk");
            this.clearData();
        }

    }

    private void clearData() {
        this.dataPoints = new ArrayList<DataPoint>();
        this.dataWindows = new ArrayList<DataWindow>();
    }

    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        TextView text = (TextView)findViewById(R.id.showAcc);
        text.setText("x: " + Float.toString(x) + "\n" + "y: " + Float.toString(y) + "\n" + "z: " + Float.toString(z));

        DataPoint dp = new DataPoint(x, y, z);
        dataPoints.add(dp);

        if (dataPoints.size() % 64 == 0 && dataPoints.size() >= 128) {
            Log.d("3", "Add window");
            int counter = dataWindows.size();
            List<DataPoint> windowedDataPoints = dataPoints.subList(counter * 64, counter * 64 + 127);
            dataWindows.add(new DataWindow(windowedDataPoints));
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LogAccFragment extends Fragment {

        public LogAccFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_log_acc, container, false);
            return rootView;
        }
    }
}
