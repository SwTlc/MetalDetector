package com.example.metaldetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Axes extends AppCompatActivity implements SensorEventListener{

    SensorManager sensorManager;
    private Sensor sensorField;
    double field;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_axes);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            check = true;
        }else {
            ((TextView) findViewById(R.id.error)).setText("non hai il magnetometro, impossibile trovare i dati");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorField, Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(check){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            field = getField(x,y,z);
            ((TextView) findViewById(R.id.fieldTxt)).setText("Calculated magnetic field:"+field+ "mu T");


            ((TextView) findViewById(R.id.xreading)).setText("X: " + x + "");
            ((TextView) findViewById(R.id.yreading)).setText("Y: " + y + "");
            ((TextView) findViewById(R.id.zreading)).setText("Z: " + z + "");

        }
    }


    private double getField(float x, float y, float z) {
        return Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z,2);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}