package es.situm.gettingstarted.animateposition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import es.situm.gettingstarted.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.model.location.Location;

/**
 *
 * Created by alejandro.trigo on 31/01/18.
 */

public class AnimatePositionActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 3096;
    private static final String TAG = "AnimatePositionActivity";

    private static final int UPDATE_LOCATION_ANIMATION_TIME = 600;
    private static final int MIN_CHANGE_IN_BEARING_TO_ANIMATE_CAMERA = 10;

    private GoogleMap map;
    private Marker marker;

    private GetBuildingCaseUse getBuildingCaseUse = new GetBuildingCaseUse();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location current;
    private String buildingId;
    private Building building;
    private GroundOverlay groundOverlay;

    private boolean markerWithOrientation = false;
    private LatLng lastCameraLatLng;
    private float lastCameraBearing;

    PositionAnimator positionAnimator = new PositionAnimator();

    private FloatingActionButton button;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animate_position);
        locationManager = SitumSdk.locationManager();

        Intent intent = getIntent();
        if (intent != null)
            if (intent.hasExtra(Intent.EXTRA_TEXT))
                buildingId = intent.getStringExtra(Intent.EXTRA_TEXT);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setup();

    }

    @Override
    public void onResume(){
        checkPermisions();
        super.onResume();
    }

    @Override
    public void onDestroy(){
        getBuildingCaseUse.cancel();
        SitumSdk.locationManager().removeUpdates(locationListener);
        stopLocation();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getBuildingCaseUse.get(buildingId, new GetBuildingCaseUse.Callback() {
            @Override
            public void onSuccess(Building build, Floor floor, Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
                building = build;
                drawBuilding(building, bitmap);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(AnimatePositionActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void setup(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button = (FloatingActionButton) findViewById(R.id.start_button);

        View.OnClickListener buttonListenerLocation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(AnimatePositionActivity.class.getSimpleName(), "button clicked");
                if(locationManager.isRunning()){
                    stopLocation();
                    SitumSdk.locationManager().removeUpdates(locationListener);
                }else {
                    markerWithOrientation = false;
                    startLocation();
                }
            }
        };
        button.setOnClickListener(buttonListenerLocation);
    }

    private void drawBuilding(Building building, Bitmap bitmap){
        Bounds drawBounds = building.getBounds();
        Coordinate coordinateNE = drawBounds.getNorthEast();
        Coordinate coordinateSW = drawBounds.getSouthWest();
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

        map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .bearing((float) building.getRotation().degrees())
                .positionFromBounds(latLngBounds)
                .zIndex(1));

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    private void startLocation(){
        if(locationManager.isRunning()){
            return;
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                current = location;
                LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                        location.getCoordinate().getLongitude());
                if (marker == null){
                    initializeMarker(latLng);
                }
                if (groundOverlay == null) {
                    initializeGroundOverlay();
                }
                updateMarkerIcon(location);
                positionAnimator.animate(marker, groundOverlay, current);

                centerInUser(location);


            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                Log.d(TAG, "onStatusChanged: " + locationStatus);
            }

            @Override
            public void onError(@NonNull Error error) {
                Log.e(TAG, "onError: " + error.getMessage());
            }

        };

        LocationRequest locationRequest = new LocationRequest.Builder()
                .buildingIdentifier(buildingId)
                .useDeadReckoning(true)
                .build();
        SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
    }

    private void initializeMarker(LatLng latLng) {
        Bitmap bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.position);
        Bitmap arrowScaled = Bitmap.createScaledBitmap(bitmapArrow, bitmapArrow.getWidth() / 4,bitmapArrow.getHeight() / 4, false);

        marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .zIndex(100)
                .flat(true)
                .anchor(0.5f,0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(arrowScaled)));
    }

    private void initializeGroundOverlay() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        Bitmap bitmapPosbg = BitmapFactory.decodeResource(getResources(), R.drawable.situm_posbg, opts);
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmapPosbg))
                .anchor(0.5f, 0.5f)
                .position(new LatLng(0, 0), 2)
                .zIndex(2);
        groundOverlay = map.addGroundOverlay(groundOverlayOptions);}

    private void centerInUser(Location location) {
        float tilt = 40;
        float bearing = (location.hasBearing()) && location.isIndoor() ? (float) (location.getBearing().degrees()) : map.getCameraPosition().bearing;

        LatLng latLng = new LatLng(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());

        //Skip if no change in location and little bearing change
        boolean skipAnimation = lastCameraLatLng != null && lastCameraLatLng.equals(latLng)
                && (Math.abs(bearing - lastCameraBearing)) < MIN_CHANGE_IN_BEARING_TO_ANIMATE_CAMERA;
        lastCameraLatLng = latLng;
        lastCameraBearing = bearing;
        if (!skipAnimation) {
            CameraPosition cameraPosition = new CameraPosition.Builder(map.getCameraPosition())
                    .target(latLng)
                    .bearing(bearing)
                    .tilt(tilt)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), UPDATE_LOCATION_ANIMATION_TIME, null);
        }

    }

    private void updateMarkerIcon(Location location) {
        boolean newLocationHasOrientation = (location.hasBearing()) && location.isIndoor();
        if (markerWithOrientation == newLocationHasOrientation) {
            return;
        }
        markerWithOrientation = newLocationHasOrientation;

        BitmapDescriptor bitmapDescriptor;
        Bitmap bitmapScaled;
        if(markerWithOrientation){
            Bitmap bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.pose);
            bitmapScaled = Bitmap.createScaledBitmap(bitmapArrow, bitmapArrow.getWidth() / 4,bitmapArrow.getHeight() / 4, false);
        } else {
            Bitmap bitmapCircle = BitmapFactory.decodeResource(getResources(), R.drawable.position);
            bitmapScaled = Bitmap.createScaledBitmap(bitmapCircle, bitmapCircle.getWidth() / 4,bitmapCircle.getHeight() / 4, false);
        }
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmapScaled);
        marker.setIcon(bitmapDescriptor);
    }


    private void stopLocation(){
        if (!locationManager.isRunning()){
            return;
        }
        locationManager.removeUpdates(locationListener);
        current = null;
        positionAnimator.clear();
        if (groundOverlay != null) {
            groundOverlay.remove();
            groundOverlay = null;
        }
        if(marker != null){
            marker.remove();
            marker = null;
        }
    }

    /**
     * Getting the permisions we need about localization.
     *
     */
    private void requestPermisions(){
        ActivityCompat.requestPermissions(AnimatePositionActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ACCESS_FINE_LOCATION_REQUEST_CODE);
    }

    /**
     * Checking if we have the requested permissions
     *
     */
    private void checkPermisions(){
        if(ContextCompat.checkSelfPermission(AnimatePositionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(AnimatePositionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){

                Snackbar.make(findViewById(android.R.id.content),
                        "Need location permission to enable sevice",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Open", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermisions();
                            }
                        }).show();
            }else{
                requestPermisions();
            }
        }
    }

    /**
     *
     * REQUESTCODE = 1 : NO PERMISSIONS
     *
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults){
        switch(requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST_CODE: {
                if(!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    finishActivity(1);
                }
            }
        }
    }

    // END REQUEST PERMISIONS



}
