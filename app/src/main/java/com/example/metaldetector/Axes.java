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
import android.widget.TextView;

public class Axes extends AppCompatActivity implements SensorEventListener, LocationListener {

    private static final long MIN_TIME = 1;
    private static final long MIN_DISTANCE = 300;
    SensorManager man;
    Sensor sensor;
    SensorEventListener thisActivity = this;
    double earthField;
    Location l;
    LocationManager locationManager;
    Context context;
    Location location;
    double latitude, longitude, altitude;
    boolean isGPSEnabled = false;// flag for network status
    boolean isNetworkEnabled = false;// flag for GPS status
    boolean canGetLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        man = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (man.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            l = getLocation();
            if (l != null) {
                GeomagneticField gmf = new GeomagneticField((float) l.getLatitude(),
                        (float) l.getLongitude(),
                        (float) l.getAltitude(),
                        l.getTime());
                earthField = getEarthField(gmf);
            } else {
                ((TextView) findViewById(R.id.debug)).setText("non trovo la posizione");
            }
        } else {
            ((TextView) findViewById(R.id.error)).setText("non hai il magnetometro, impossibile trovare i dati");
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        man.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        man.registerListener(thisActivity,
                sensor,
                Sensor.TYPE_MAGNETIC_FIELD,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float magneticField = (float) getField(x, y, z);

        ((TextView) findViewById(R.id.xreading)).setText("X: " + x + "");
        ((TextView) findViewById(R.id.yreading)).setText("Y: " + y + "");
        ((TextView) findViewById(R.id.zreading)).setText("Z: " + z + "");

        ((TextView) findViewById(R.id.earthTxt)).setText("Earth: " + earthField);
        ((TextView) findViewById(R.id.fieldTxt)).setText("Calculated: " + magneticField);

        // I'm not sure i have to repeat this step inside OnSensorChanged.
        // Instructions inside the if statement are executed by onCreate, too.
        if (l != null) {
            GeomagneticField gmf = new GeomagneticField((float) l.getLatitude(),
                    (float) l.getLongitude(),
                    (float) l.getAltitude(),
                    l.getTime());
            earthField = getEarthField(gmf);
        }


        TextView metalNearby = (TextView) findViewById(R.id.metalNearby);

        if (magneticField > 1.4 * earthField || magneticField < 0.6 * earthField) {
            //there is a high probability that some metal is close to the sensor
            metalNearby.setText("Ho rilevato un metallo");
        } else {
            metalNearby.setText("Sto cercando...");
        }
    }

    private double getEarthField(GeomagneticField gmf) {
        return getField(gmf.getX(), gmf.getY(), gmf.getZ());
    }

    private double getField(float x, float y, float z) {
        return Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public Location getLocation() {
        // The minimum distance to change Updates in meters
        final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
        // The minimum time between updates in milliseconds
        final long MIN_TIME_BW_UPDATES = 200 * 10 * 1; // 2 seconds
        // Declaring a Location Manager
        LocationManager locationManager;

        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);// getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);// getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled, Log.e è un messaggio di errore
                //String Network_GPS = null;
                //String Disable = null;
                final String TAG = "Network_GPS";
                final String MSG = "Disable";
                Log.e(TAG, MSG);
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    // Log.e(“Network”, “Network”);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            altitude = location.getAltitude();
                        }
                    }
                } else
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            //Log.e(“GPS Enabled”, “GPS Enabled”);
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    altitude = location.getAltitude();
                                }
                            }
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public double getAltitude() {
        if (location != null) {
            altitude = location.getAltitude();
        }
        return altitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        l = location;
    }
}