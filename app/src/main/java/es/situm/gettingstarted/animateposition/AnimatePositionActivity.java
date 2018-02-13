package es.situm.gettingstarted.animateposition;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 3096;
    private static final String TAG = "AnimatePositionActivity";

    private static final double DISTANCE_CHANGE_TO_ANIMATE = 0.2;
    private static final int BEARING_CHANGE_TO_ANIMATE = 1;

    private static final int DURATION_POSITION_ANIMATION = 500;
    private static final int DURATION_BEARING_ANIMATION = 200;


    private GoogleMap map;
    private Marker prev;

    private GetBuildingCaseUse getBuildingCaseUse = new GetBuildingCaseUse();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location current;
    private Location lastLocation;
    private String buildingId;
    private Building building;
    private LatLng destinationLatLng;
    private LatLng lastLatLng;

    private float lastBearing;
    private float destinationBearing;




    private FloatingActionButton button;
    private ProgressBar progressBar;

    private ValueAnimator locationAnimator = new ValueAnimator();
    private ValueAnimator locationBearingAnimator = new ValueAnimator();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_building);
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
                if (prev == null){
                    Bitmap bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.pose);
                    Bitmap arrowScaled = Bitmap.createScaledBitmap(bitmapArrow, bitmapArrow.getWidth() / 4,bitmapArrow.getHeight() / 4, false);

                    prev = map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .zIndex(100)
                            .icon(BitmapDescriptorFactory.fromBitmap(arrowScaled)));
                }


                if (location.getQuality() == Location.Quality.LOW) {
                    prev.setPosition(latLng);
                    prev.setRotation((float) location.getBearing().degrees());

                } else {
                    animate(prev, current);
                }


            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                if(!locationManager.isRunning()){
                    locationManager.removeUpdates(locationListener);
                }
            }

            @Override
            public void onError(@NonNull Error error) {
                Log.e(TAG, "onError: " + error);
            }

        };

        LocationRequest locationRequest = new LocationRequest.Builder()
                .buildingIdentifier(buildingId)
                .build();
        SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
    }

    private void stopLocation(){
        if (!locationManager.isRunning()){
            return;
        }
        locationManager.removeUpdates(locationListener);
        current = null;
        prev.remove();
    }

    synchronized void animate(final Marker marker, final Location location) {
        Coordinate toCoordinate = location.getCoordinate();
        final LatLng toLatLng = new LatLng(toCoordinate.getLatitude(), toCoordinate.getLongitude());
        final float toBearing = (float) location.getBearing().degrees();

        if (lastLocation == null) { //First location
            marker.setRotation(toBearing);
            marker.setPosition(toLatLng);

            lastLocation = location;
            lastLatLng = toLatLng;
            return;
        }

        animatePosition(marker, location);
        animateBearing(marker, location);
    }

    private void animatePosition(final Marker marker, Location toLocation){
        Coordinate toCoordinate = toLocation.getCoordinate();
        final LatLng toLatLng = new LatLng(toCoordinate.getLatitude(), toCoordinate.getLongitude());

        if ( destinationLatLng != null) {
            float[] results = new float[1];
            android.location.Location.distanceBetween(toCoordinate.getLatitude(), toCoordinate.getLongitude(),
                    destinationLatLng.latitude, destinationLatLng.longitude, results);
            float distance = results[0];
            if (distance < DISTANCE_CHANGE_TO_ANIMATE) {
                return;
            }
        }
        if (destinationLatLng == toLatLng) {
            return;
        }

        locationAnimator.cancel();
        if (lastLocation != toLocation) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                //hardfix for crash in API 19 at PropertyValuesHolder.setupSetterAndGetter()
                marker.setPosition(toLatLng);
            } else {

                locationAnimator = new ObjectAnimator();
                locationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    LatLng startLatLng = lastLatLng;

                    @Override
                    public synchronized void onAnimationUpdate(ValueAnimator animation) {
                        float t = animation.getAnimatedFraction();
                        lastLatLng = interpolateLatLng(t, startLatLng, toLatLng);

                        marker.setPosition(lastLatLng);
                    }
                });
                locationAnimator.setFloatValues(0, 1); //Ignored
                locationAnimator.setDuration(DURATION_POSITION_ANIMATION);
                locationAnimator.start();
            }
            destinationLatLng = toLatLng;
        }
    }

    private LatLng interpolateLatLng(float fraction, LatLng a, LatLng b) {
        double lat = (b.latitude - a.latitude) * fraction + a.latitude;
        double lng = (b.longitude - a.longitude) * fraction + a.longitude;
        return new LatLng(lat, lng);
    }

    private float normalizeAngle(float degrees) {
        degrees = degrees % 360;
        return (degrees + 360) % 360;
    }

    private void animateBearing(final Marker marker, Location location) {
        float degrees = (float) location.getBearing().degrees();

        //Normalize angle
        degrees = normalizeAngle(degrees);
        final float toBearing = degrees;

        if (Math.abs(destinationBearing - toBearing) < 1) {
            return;
        }


        locationBearingAnimator.cancel();

        lastBearing =  normalizeAngle(lastBearing);

        //Avoid turning in the wrong direction
        if (lastBearing - toBearing > 180) {
            lastBearing -= 360;
        } else if (toBearing - lastBearing > 180) {
            lastBearing += 360;
        }


        float diffBearing = Math.abs(toBearing - lastBearing);
        if (diffBearing < BEARING_CHANGE_TO_ANIMATE) {
            return;
        }



        if (lastBearing != toBearing) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                //hardfix for crash in API 19 at PropertyValuesHolder.setupSetterAndGetter()
                marker.setRotation(toBearing);
            } else {

                locationBearingAnimator = new ObjectAnimator();
                locationBearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public synchronized void onAnimationUpdate(ValueAnimator animation) {
                        lastBearing = (Float) animation.getAnimatedValue();
                        marker.setRotation(lastBearing);
                    }
                });
                locationBearingAnimator.setFloatValues(lastBearing, toBearing);
                locationBearingAnimator.setDuration(DURATION_BEARING_ANIMATION);
                locationBearingAnimator.start();
            }
            destinationBearing = toBearing;
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
