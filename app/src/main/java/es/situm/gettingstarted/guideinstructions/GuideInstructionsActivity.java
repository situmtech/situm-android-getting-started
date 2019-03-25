package es.situm.gettingstarted.guideinstructions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.directions.DirectionsRequest;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.cartography.Poi;
import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.directions.Route;
import es.situm.sdk.model.directions.RouteSegment;
import es.situm.sdk.model.location.Bounds;
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

public class GuideInstructionsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = GuideInstructionsActivity.class.getSimpleName();
    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 3096;

    private GoogleMap googleMap;
    private NavigationRequest navigationRequest;
    private List<Polyline> polylines = new ArrayList<>();
    private GetBuildingsCaseUse getBuildingsCaseUse = new GetBuildingsCaseUse();
    private GetPoisCaseUse getPoisCaseUse = new GetPoisCaseUse();

    private String  buildingId;

    private List<Poi> poiList = new ArrayList<>();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location current;
    private Point to;
    private Building currentBuilding;


    private ProgressBar progressBar;
    private FloatingActionButton button;
    private RelativeLayout navigationLayout;
    private TextView mNavText;
    private Button mBtnNav;

    private Circle prev;
    private Marker destination;

    private boolean navigation = false;
    private String floorId;


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

        Intent intent = getIntent();
        if (intent != null)
            if (intent.hasExtra(Intent.EXTRA_TEXT))
                buildingId = intent.getStringExtra(Intent.EXTRA_TEXT);

        setup();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        getBuildingsCaseUse.cancel();
        getPoisCaseUse.cancel();
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

                if(destination != null){
                    if(navigation) {

                        SitumSdk.navigationManager().updateWithLocation(current);
                    }

                    navigationCreation();
                }
            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                Log.d(TAG, "onStatusChanged: " + locationStatus.toString());
                if(!locationManager.isRunning()){
                    locationManager.removeUpdates(locationListener);
                }
            }

            @Override
            public void onError(@NonNull Error error) {

            }
        };
        LocationRequest locationRequest = new LocationRequest.Builder()
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
        getBuildingsCaseUse.get(buildingId, new GetBuildingsCaseUse.Callback() {
            @Override
            public void onSuccess(Building building, Floor floor, Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
                floorId = floor.getIdentifier();
                currentBuilding = building;
                drawBuilding(building, bitmap);
                getPoisCaseUse.get(currentBuilding, new GetPoisCaseUse.Callback() {
                    @Override
                    public void onSuccess(List<Poi> pois) {
                        poiList.addAll(pois);
                        drawPois(poiList);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(GuideInstructionsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(GuideInstructionsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {
                removePolylines();
                destination = marker;
                Poi p;
                p = getPoiFromMarker(marker);
                to = p.getPosition();

                if((current != null) && (destination != null)){
                    getRoute();
                }

                return false;
            }
        });
    }

    /**
     * END MAP
     */

    private void setup(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button = (FloatingActionButton) findViewById(R.id.start_button);
        navigationLayout = (RelativeLayout) findViewById(R.id.navigation_layout);
        mBtnNav = (Button) findViewById(R.id.btn_start_navigation);
        mNavText = (TextView) findViewById(R.id.tv_indication);

        View.OnClickListener buttonListenerLocalization = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationManager.isRunning()){
                    stopLocation();
                    SitumSdk.locationManager().removeUpdates(locationListener);
                }else {
                    startLocation();
                }
            }
        };
        button.setOnClickListener(buttonListenerLocalization);


        View.OnClickListener onClickListenerNav = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBtnNav.getText().equals("Stop")){
                    stopNavigation();

                }else {
                    mBtnNav.setText("Stop");
                    navigation = true;
                    getRoute();
                }
            }
        };

        mBtnNav.setOnClickListener(onClickListenerNav);
    }

    /**
     * DRAWING
     *
     */

    void drawBuilding(Building building, Bitmap bitmap){
        Bounds drawBounds = building.getBounds();
        Coordinate coordinateNE = drawBounds.getNorthEast();
        Coordinate coordinateSW = drawBounds.getSouthWest();
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

        googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .bearing((float) building.getRotation().degrees())
                .positionFromBounds(latLngBounds)
                .zIndex(1));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    void drawPois(List<Poi> pois){

        if(pois.isEmpty()){
            Toast.makeText(GuideInstructionsActivity.this, "There is no POIs in this building", Toast.LENGTH_LONG).show();
        }else{
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Poi p : pois){
                Log.d(TAG, "drawPois: Poi floor id: " + p.getFloorIdentifier() + " curr foor id: " + floorId);
                if(!p.getFloorIdentifier().equals(floorId)){
                    continue;
                }
                LatLng latLng = new LatLng(p.getCoordinate().getLatitude(), p.getCoordinate().getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(p.getName())
                        .zIndex(10));
                builder.include(latLng);


            }
        }

    }

    /**
     *  END DRAWING
     *
     */


    Poi getPoiFromMarker(Marker marker){
        for(Poi p : poiList){
            if(p.getName().equals(marker.getTitle()))
                return p;
        }

        return null;
    }

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
        mBtnNav.setText("Start");
        removePolylines();
        destination = null;
        navigationRequest = null;
        navigationLayout.setVisibility(View.GONE);
        navigation = false;
        mNavText.setText("Navigation");
    }

}
