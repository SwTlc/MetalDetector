package com.example.metaldetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class Axes extends AppCompatActivity implements SensorEventListener{

    TextView sensor_name, error_sensor, field_value, x_value, y_value, z_value;
    SensorManager sensorManager;
    private Sensor sensorField;
    double field;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_axes);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensorField != null) {
            check = true;
            sensor_name = findViewById(R.id.sensor_magn);
            sensor_name.setText("Sensor name: " + sensorField.getName() + "\n"
                    + "Sensor type: " + sensorField.getStringType() + "\n"
                    + "Sensor vendor: " + sensorField.getVendor() + "\n"
                    + "Sensor version: " + sensorField.getVersion() + "\n" + "\n");
        }else {
            error_sensor = findViewById(R.id.error);
            error_sensor.setText("non hai il magnetometro, impossibile trovare i dati");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(check){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            field = getField(x,y,z);

            field_value = findViewById(R.id.fieldTxt);
            x_value = findViewById(R.id.xreading);
            y_value = findViewById(R.id.yreading);
            z_value = findViewById(R.id.zreading);

            field_value.setText("Calculated magnetic field:  "+field+ " μT");


            x_value.setText("X: " + x + " μT");
            y_value.setText("Y: " + y + " μT");
            z_value.setText("Z: " + z + " μT");

        }
    }

    private double getField(float x, float y, float z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z,2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorField, Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}