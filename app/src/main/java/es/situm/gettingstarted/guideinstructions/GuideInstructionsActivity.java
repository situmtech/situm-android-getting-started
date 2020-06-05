package es.situm.gettingstarted.guideinstructions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.directions.DirectionsRequest;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.location.util.CoordinateConverter;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.directions.Route;
import es.situm.sdk.model.directions.RouteSegment;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.CartesianCoordinate;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.model.location.Location;
import es.situm.sdk.model.navigation.NavigationProgress;
import es.situm.sdk.navigation.NavigationListener;
import es.situm.sdk.navigation.NavigationRequest;
import es.situm.sdk.utils.Handler;

/**
 *
 * Created by alejandro.trigo on 19/01/18.
 */

public class GuideInstructionsActivity extends SampleActivity implements OnMapReadyCallback {

    private final static String TAG = GuideInstructionsActivity.class.getSimpleName();
    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 3096;

    private GoogleMap googleMap;
    private NavigationRequest navigationRequest;
    private List<Polyline> polylines = new ArrayList<>();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location current;
    private Point to;

    private Building building;
    private ProgressBar progressBar;
    private RelativeLayout navigationLayout;
    private TextView mNavText;

    private Circle prev;
    private Marker markerDestination;

    private boolean navigation = false;
    private String floorId;

    boolean isMapShow;
    private CoordinateConverter coordinateConverter;



    /**
     * Getting the permisions we need about localization.
     *
     */
    private void requestPermisions(){
        ActivityCompat.requestPermissions(GuideInstructionsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ACCESS_FINE_LOCATION_REQUEST_CODE);
    }

    /**
     * Checking if we have the requested permissions
     *
     */
    private void checkPermisions(){
        if(ContextCompat.checkSelfPermission(GuideInstructionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(GuideInstructionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){

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



    /**
     *
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_indications);
        locationManager = SitumSdk.locationManager();
        building = getBuildingFromIntent();
        setup();
        checkLocationManager();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void checkLocationManager() {
        if (locationManager.isRunning()) {
            stopLocation();
            SitumSdk.locationManager().removeUpdates(locationListener);
        } else {
            startLocation();

        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        checkPermisions();
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy: ");
        SitumSdk.navigationManager().removeUpdates();
        SitumSdk.locationManager().removeUpdates(locationListener);
        stopLocation();
        super.onDestroy();
    }


    /**
     * LOCATION
     */

    private void startLocation(){
        if(locationManager.isRunning()){
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (prev != null) prev.remove();
                LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                        location.getCoordinate().getLongitude());
                prev = googleMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(1d)
                        .strokeWidth(0f)
                        .fillColor(Color.BLUE)
                        .zIndex(100));
                current = location;
                floorId = current.getPosition().getFloorIdentifier();
                if (!isMapShow) {
                    drawMap();
                    isMapShow = true;
                }

                if(to != null){
                    if(navigation) {

                        SitumSdk.navigationManager().updateWithLocation(current);
                    }

                    navigationCreation();
                }
            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                Log.d(TAG, "onStatusChanged: " + locationStatus.toString());
            }

            @Override
            public void onError(@NonNull Error error) {
                Log.e(TAG, "onError: " + error.getMessage());
            }
        };
        LocationRequest locationRequest = new LocationRequest.Builder()
                .buildingIdentifier(building.getIdentifier())
                .useWifi(true)
                .useBle(true)
                .useForegroundService(true)
                .build();
        SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
    }

    private void stopLocation(){
        if (!locationManager.isRunning()){
            return;
        }
        locationManager.removeUpdates(locationListener);
        current = null;
        stopNavigation();
        if (prev != null)
            prev.remove();

        removePolylines();
    }

    private void removePolylines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
    }

    /*
     *
     *  END LOCATION
     */

    /**
     *
     * MAP
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        coordinateConverter = new CoordinateConverter(building.getDimensions(),building.getCenter(),building.getRotation());
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.setOnMapClickListener(latLng -> {
            getPoint(googleMap, latLng);
        });
    }

    private void getPoint(GoogleMap googleMap, LatLng latLng) {
        removePolylines();
        if (markerDestination != null) {
            markerDestination.remove();
        }
        to = createPoint(latLng);
        if (current == null || to == null) {
            return;
        }
        navigation = true;
        getRoute();
        markerDestination = googleMap.addMarker(new MarkerOptions().position(latLng).title("destination"));
    }


    void drawMap() {

        fetchCurrentFloorImage(building, new GuideInstructionsActivity.Callback() {
            @Override
            public void onSuccess(Bitmap floorImage) {

                drawBuilding(floorImage);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(GuideInstructionsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void fetchCurrentFloorImage(Building building, GuideInstructionsActivity.Callback callback) {
        SitumSdk.communicationManager().fetchFloorsFromBuilding(building.getIdentifier(), new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> floorsCollection) {
                List<Floor> floors = new ArrayList<>(floorsCollection);
                Floor currentFloor = floors.get(0);
                for (Floor floor : floors) {
                    if (floor.getIdentifier().equals(current.getPosition().getFloorIdentifier())) {
                        currentFloor = floor;
                    }
                }
                floorId = currentFloor.getIdentifier();
                SitumSdk.communicationManager().fetchMapFromFloor(currentFloor, new Handler<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        callback.onSuccess(bitmap);
                    }

                    @Override
                    public void onFailure(Error error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onFailure(Error error) {
                callback.onError(error);
            }
        });
    }

    private Point createPoint(LatLng latLng) {
        Coordinate coordinate = new Coordinate(latLng.latitude, latLng.longitude);
        CartesianCoordinate cartesianCoordinate= coordinateConverter.toCartesianCoordinate(coordinate);
        Point point = new Point(building.getIdentifier(), floorId,coordinate,cartesianCoordinate );
        return point;
    }

    interface Callback {
        void onSuccess(Bitmap floorImage);

        void onError(Error error);
    }

    /**
     * END MAP
     */

    private void setup(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        navigationLayout = (RelativeLayout) findViewById(R.id.navigation_layout);
        mNavText = (TextView) findViewById(R.id.tv_indication);
    }

    /**
     * DRAWING
     *
     */
    void drawBuilding(Bitmap bitmap) {
        Bounds drawBounds = building.getBounds();
        Coordinate coordinateNE = drawBounds.getNorthEast();
        Coordinate coordinateSW = drawBounds.getSouthWest();
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

        this.googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .bearing((float) building.getRotation().degrees())
                .positionFromBounds(latLngBounds)
                .zIndex(1));

        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    /**
     *  END DRAWING
     *
     */


    /**
     * ROUTE
     */

    void getRoute(){
        DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                .from(current.getPosition(), null)
                .to(to)
                .build();


        SitumSdk.directionsManager().requestDirections(directionsRequest, new Handler<Route>() {
            @Override
            public void onSuccess(Route route) {
                removePolylines();
                drawRoute(route);
                centerCamera(route);

                navigationRequest = new NavigationRequest.Builder()
                        .route(route)
                        .distanceToGoalThreshold(3d)
                        .outsideRouteThreshold(50d)
                        .build();

                startNavigation();


            }
            @Override
            public void onFailure(Error error) {

            }
        });
    }

    private void drawRoute(Route route) {
        for (RouteSegment segment : route.getSegments()) {
            //For each segment you must draw a polyline
            //Add an if to filter and draw only the current selected floor
            List<LatLng> latLngs = new ArrayList<>();
            for (Point point : segment.getPoints()) {
                latLngs.add(new LatLng(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude()));
            }

            PolylineOptions polyLineOptions = new PolylineOptions()
                    .color(Color.GREEN)
                    .width(4f)
                    .zIndex(3)
                    .addAll(latLngs);
            Polyline polyline = googleMap.addPolyline(polyLineOptions);
            polylines.add(polyline);
        }
    }

    private void centerCamera(Route route) {
        Coordinate from = route.getFrom().getCoordinate();
        Coordinate to = route.getTo().getCoordinate();

        LatLngBounds.Builder builder = new LatLngBounds.Builder()
                .include(new LatLng(from.getLatitude(), from.getLongitude()))
                .include(new LatLng(to.getLatitude(), to.getLongitude()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }


    /**
     * NAVIGATION
     */

    void navigationCreation(){
        navigationLayout.setVisibility(View.VISIBLE);
    }

    void startNavigation(){
        Log.d(TAG, "startNavigation: ");
        SitumSdk.navigationManager().requestNavigationUpdates(navigationRequest, new NavigationListener() {
            @Override
            public void onDestinationReached() {
                Log.d(TAG, "onDestinationReached: ");
                mNavText.setText("Arrived");
                removePolylines();
            }

            @Override
            public void onProgress(NavigationProgress navigationProgress) {
                Context context = getApplicationContext();
                Log.d(TAG, "onProgress: " + navigationProgress.getCurrentIndication().toText(context));
                mNavText.setText(navigationProgress.getCurrentIndication().toText(context));
            }

            @Override
            public void onUserOutsideRoute() {
                Log.d(TAG, "onUserOutsideRoute: ");
                mNavText.setText("Outside of the route");
            }
        });
    }

    void stopNavigation(){
        removePolylines();
        to = null;
        navigationRequest = null;
        navigationLayout.setVisibility(View.GONE);
        navigation = false;
        mNavText.setText("Navigation");
    }

}
