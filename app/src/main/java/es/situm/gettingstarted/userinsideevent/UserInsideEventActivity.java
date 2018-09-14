package es.situm.gettingstarted.userinsideevent;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import es.situm.gettingstarted.R;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;

import es.situm.sdk.model.cartography.BuildingInfo;
import es.situm.sdk.model.location.CartesianCoordinate;
import es.situm.sdk.model.location.Location;

import es.situm.sdk.v1.SitumEvent;

public class UserInsideEventActivity extends AppCompatActivity implements LocationListener{

    private BuildingInfo buildingInfo;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 3096;
    private TextView calculating;
    private ProgressBar progressBar;
    private boolean dontShowAgain;
    private AlertDialog alertDialog ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inside_event);

        Intent intent = getIntent();
        if(intent != null){
            buildingInfo = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);
        }
        locationManager = SitumSdk.locationManager();
        calculating = (TextView) findViewById(R.id.calculating);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_positioning);

        dontShowAgain = false;
        checkPermissions();

    }

    private void userInsideEventDialog(){
        AlertDialog.Builder builder; builder = new AlertDialog.Builder(this);
        builder.setMessage("You are inside an event");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Dismiss",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(
                "Do not show again",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dontShowAgain = true;
                        dialog.cancel();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(UserInsideEventActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserInsideEventActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
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

                // No explanation needed, we can request the permission.

                requestPermission();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            startLocation();
        }
    }


    private void requestPermission(){
        ActivityCompat.requestPermissions(UserInsideEventActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ACCESS_FINE_LOCATION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startLocation();
                } else {
                    calculating.setText("Info: permissions must be accepted to use location manager");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void startLocation() {
        if (locationManager.isRunning()) {
            return;
        }
        locationListener = new LocationListener(){

            @Override
            public void onLocationChanged(@NonNull Location location) {
                SitumEvent event = getEventForLocation(location);
                if (event != null) {
                    Log.d("Event", "User inside event: " + event.getName());
                    hideProgress();
                    hideText();
                    if(!dontShowAgain && (alertDialog == null || !alertDialog.isShowing()) ){
                        userInsideEventDialog();
                    }
                }

            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {

            }

            @Override
            public void onError(@NonNull Error error) {
                Toast.makeText(UserInsideEventActivity.this, error.getMessage() , Toast.LENGTH_LONG).show();
            }
        };
        LocationRequest locationRequest = new LocationRequest.Builder()
                .useWifi(true)
                .useBle(true)
                .useForegroundService(true)
                .build();
        locationManager.requestLocationUpdates(locationRequest, locationListener);
    }


    private SitumEvent getEventForLocation(final Location location) {
        for (SitumEvent event : buildingInfo.getEvents()) {
            if (isLocationInsideEvent(location, event)) {
                return event;
            }
        }
        return null;
    }

    private boolean isLocationInsideEvent(Location location, SitumEvent situmEvent) {
        if (!location.getFloorIdentifier()
                .equals(String.valueOf(situmEvent.getFloor_id()))) {
            return false;
        }
        CartesianCoordinate eventCenter = situmEvent.getTrigger().getCenter().getCartesianCoordinate();

        return location.getCartesianCoordinate()
                .distanceTo(eventCenter) <= situmEvent.getRadius();
    }
    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }

    private void hideText(){
        calculating.setVisibility(View.GONE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(@NonNull LocationStatus locationStatus) {

    }

    @Override
    public void onError(@NonNull Error error) {

    }


    private void stopLocation() {
        if (!locationManager.isRunning()) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        stopLocation();
        super.onDestroy();
    }
}
