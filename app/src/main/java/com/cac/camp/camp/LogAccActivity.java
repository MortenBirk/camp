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

import weka.core.converters.*;

public class LogAccActivity extends Activity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<DataPoint> dataPoints;
    private List<DataWindow> dataWindows;
    private Boolean isLogging = true;

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
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        dataPoints = new ArrayList<DataPoint>();
        dataWindows = new ArrayList<DataWindow>();

    }

    public void logButtonClicked(View view) {

        Button logButton = (Button) findViewById(R.id.stopLogButton);
        isLogging = !isLogging;
        if (isLogging) {
            logButton.setText("Stop log");
            //TODO - start the logging again
        } else {
            logButton.setText("Start log");
            //TODO - stop the logging
            mSensorManager.unregisterListener(this);

            synchronized (dataWindows) {
                Log.d("1", "Collect data");
                WekaDataGenerator.createArff();
                Log.d("2", dataWindows.get(0).toString());

            }
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
