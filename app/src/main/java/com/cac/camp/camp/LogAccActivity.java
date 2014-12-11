package com.cac.camp.camp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
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
import android.widget.Toast;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import weka.core.converters.*;
import com.google.android.gms.location.LocationListener;

public class LogAccActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<DataPoint> dataPoints;
    private List<DataWindow> dataWindows;
    private Boolean isLogging = false;
    private Boolean isClassifying = false;
    private int numberOfLogs = 0;

    private AssetManager assetMgr;



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

        assetMgr = this.getAssets();




    }



    public void calmButtonClicked(View view) {
        Button button = (Button) findViewById(R.id.calmButton);
        isLogging = !isLogging;
        if (isLogging) {
            button.setText("Stop calm log");
            //TODO - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            button.setText("Calm party");
            //TODO - stop the logging
            mSensorManager.unregisterListener(this);
            numberOfLogs++;
            List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(dataWindows);
            WekaDataGenerator.saveArff(dataWindowsCopy, "calmParty" + numberOfLogs, "calmParty");
            this.clearData();

        }

    }

    public void normalButtonClicked(View view) {
        Button button = (Button) findViewById(R.id.normalButton);
        isLogging = !isLogging;
        if (isLogging) {
            button.setText("Stop normal log");
            // - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            button.setText("Normal party");
            // - stop the logging
            mSensorManager.unregisterListener(this);
            numberOfLogs++;
            List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(dataWindows);
            WekaDataGenerator.saveArff(dataWindowsCopy, "normalParty" + numberOfLogs, "normalParty");
            this.clearData();
        }

    }

    public void wildButtonClicked(View view) {
        Button button = (Button) findViewById(R.id.wildButton);
        isLogging = !isLogging;
        if (isLogging) {
            button.setText("Stop wild log");
            // - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            button.setText("Wild party");
            // - stop the logging
            mSensorManager.unregisterListener(this);
            numberOfLogs++;
            List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(dataWindows);
            WekaDataGenerator.saveArff(dataWindowsCopy, "wildParty" + numberOfLogs, "wildParty");
            this.clearData();
        }

    }

    public void classifyButtonClicked(View view) {
        Button button = (Button) findViewById(R.id.classifyButton);
        isClassifying = !isClassifying;
        if (isClassifying) {
            button.setText("Stop classifying");
            // - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            button.setText("Classify");
            // - stop the logging
            mSensorManager.unregisterListener(this);
            List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(dataWindows);
            WekaDataGenerator.classify(dataWindowsCopy, this, assetMgr);
        }
    }

    public void presentClassification(List<String> classifications) {
        String classS = "";
        for (String s : classifications)
            classS += s;

        Log.e("classify", classS);

        TextView text = (TextView)findViewById(R.id.showAcc);
        text.setText(classS);
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
