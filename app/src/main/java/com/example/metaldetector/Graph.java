package com.example.metaldetector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Graph extends AppCompatActivity implements SensorEventListener {

    TextView error_sensor, field_value;
    SensorManager sensorManager;
    private Sensor sensorField;
    float range;
    boolean check = false;
    double field;
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensorField != null) {
            check = true;
            // range = sensorField.getMaximumRange();
            // we get graph view instance
            GraphView graph = (GraphView) findViewById(R.id.graph);
            // data
            series = new LineGraphSeries<DataPoint>();
            graph.addSeries(series);
            // customize a little bit viewport
            Viewport viewport = graph.getViewport();
            viewport.setYAxisBoundsManual(true);
            viewport.setMinY(0);
            // la max y non può essere il range perchè va oltre ai 3000 tipo, ho fatto un po' di prove e 300 forse ci sta
            viewport.setMaxY(300);
            viewport.scrollToEnd();
            // per usare scrollToEnd è necessario che setXAxisBoundManual sia true
            viewport.setXAxisBoundsManual(true);


        }else {
            error_sensor = findViewById(R.id.error);
            error_sensor.setText("non hai il magnetometro, impossibile trovare i dati");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(check) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            field = getField(x, y, z);

            field_value = findViewById(R.id.fieldTxt);
            field_value.setText("Calculated magnetic field:" + field + " μT");

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // we add 100 new entries
                    for (int i = 0; i < 100; i++) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                addEntry();
                            }
                        });

                        // sleep to slow down the add of entries
                        try {
                            Thread.sleep(1000); // ho aumentato a 2 secondi (non so se abbia fatto effetto)
                        } catch (InterruptedException e) {
                            // manage error ... // QUI BISOGNA METTERE QUALCOSA
                        }
                    }
                }
            }).start();
        }
        }
    }


    private double getField(float x, float y, float z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z,2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorField, Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_NORMAL);
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end

        //prova con 300 pti
        series.appendData(new DataPoint(lastX++, field * 10d), true, 300);
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