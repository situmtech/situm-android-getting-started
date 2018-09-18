package es.situm.gettingstarted.indooroutdoor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import es.situm.gettingstarted.R;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.model.location.Location;

/**
 * Created by alberto.penas on 7/07/17.
 */

public class IndoorOutdoorActivity
        extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 3096;
    private TextView statusTV;
    private TextView resultTV;
    private ToggleButton toggleButton;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_outdoor);
        setup();
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        checkPermissions();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        stopLocation();
        super.onDestroy();
    }


    private void setup() {
        locationManager = SitumSdk.locationManager();
        resultTV = (TextView) findViewById(R.id.textView);
        statusTV = (TextView) findViewById(R.id.status_tv);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    startLocation();
                }else{
                    stopLocation();
                }
            }
        });
    }


    private void checkPermissions() {
        toggleButton.setEnabled(false);
        if (ContextCompat.checkSelfPermission(IndoorOutdoorActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(IndoorOutdoorActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(findViewById(android.R.id.content),
                        "Needed location permission to enable service",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermission();
                    }
                }).show();
            } else {

                requestPermission();
            }
        }else{
            toggleButton.setEnabled(true);
        }
    }


    private void requestPermission(){
        ActivityCompat.requestPermissions(IndoorOutdoorActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ACCESS_FINE_LOCATION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    toggleButton.setEnabled(true);
                } else {
                    statusTV.setText("Info: permissions must be accepted to use location manager");

                }
                return;
            }

        }
    }


    private void startLocation() {
        if (locationManager.isRunning()) {
            return;
        }
        locationListener = new LocationListener(){

            @Override
            public void onLocationChanged(@NonNull Location location) {
                resultTV.setText(location.toString());
            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                statusTV.setText("Status: " + locationStatus.name());
            }

            @Override
            public void onError(@NonNull Error error) {
                Toast.makeText(IndoorOutdoorActivity.this, error.getMessage() , Toast.LENGTH_LONG).show();
            }
        };
        LocationRequest locationRequest = new LocationRequest.Builder()
                .useWifi(true)
                .useBle(true)
                .useForegroundService(true)
                .build();
        locationManager.requestLocationUpdates(locationRequest, locationListener);
    }


    private void stopLocation() {
        if (!locationManager.isRunning()) {
            return;
        }
        locationManager.removeUpdates(locationListener);
        statusTV.setText("Status:");
        resultTV.setText("");

    }
}
