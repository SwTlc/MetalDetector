package com.example.metaldetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class Graph extends AppCompatActivity {

    TextView sensor_name, error_sensor, field_value, x_value, y_value, z_value;
    SensorManager sensorManager;
    private Sensor sensorField;
    GraphViewSeries graphSeries;
    float range;
    boolean check = false;
    boolean stop = false;
    double field;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensorField != null) {
            check = true;
            range = sensorField.getMaximumRange();

            GraphView graphView = new LineGraphView(this, "Intensity");
            graphView = findViewById(R.id.graph);
            graphView.setScrollable(true);
            graphView.setViewPort(0, range); // the x range you want to show without scrolling
            graphSeries = new GraphViewSeries(new GraphViewDataInterface[0]);
            graphView.addSeries(graphSeries);
            setContentView(graphView);

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

            while(!stop) {
                graphSeries.appendData(new GraphView.GraphViewData(i, field), true, 300);
                //where 300 is the maximum number of values to keep
                i++;
                field_value = findViewById(R.id.fieldTxt);
                field_value.setText("Calculated magnetic field:" + field + " μT");
            }

        }
    }

    public void onClick(View view) {
        stop = true;
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