package com.example.localization;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor accelerationSensor;
    private Sensor magnetometerSensor;
    private TextView sensorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init sensor manager and get sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // init text view
        sensorTextView = findViewById(R.id.senseText);
        sensorTextView.setText(R.string.display);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // register sensor listeners
        if (accelerationSensor != null) {
            mSensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magnetometerSensor != null) {
            mSensorManager.registerListener( this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override 
    protected void onStop() {
        super.onStop();
        // unregister sensor listeners
        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    public void onSensorChanged(SensorEvent event) {
        // write the measurement data into buffers
            DetectMotion.writeData(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


