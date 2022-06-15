package es.situm.gettingstarted.drawroutegeojson;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.directions.DirectionsRequest;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.util.CoordinateConverter;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.directions.Route;
import es.situm.sdk.model.location.Angle;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.CartesianCoordinate;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.utils.Handler;

public class DrawRouteGeojsonActivity
        extends SampleActivity
        implements OnMapReadyCallback {

    private final String TAG = getClass().getSimpleName();

    private ProgressBar progressBar;
    private FloatingActionButton fabDisplaySource;
    private GoogleMap googleMap;
    private Building building;
    private Marker markerDestination;
    private Marker markerOrigin;
    private String floorId;
    private Point pointOrigin;
    private CoordinateConverter coordinateConverter;
    private GeoJsonLayer routeLayer;
    private JSONObject routeLayerSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_route_geojson);
        getSupportActionBar().setSubtitle(R.string.tv_select_points);
        building = getBuildingFromIntent();
        setup();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        SitumSdk.navigationManager().removeUpdates();
        super.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        this.googleMap = googleMap;
        fetchFirstFloorImage(building, new Callback() {
            @Override
            public void onSuccess(Bitmap floorImage) {
                coordinateConverter = new CoordinateConverter(building.getDimensions(), building.getCenter(), building.getRotation());
                drawBuilding(floorImage);
            }

            @Override
            public void onError(Error error) {
                Snackbar.make(findViewById(R.id.container), error.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
            }
        });

        this.googleMap.setOnMapClickListener(latLng -> {
            if (pointOrigin == null) {
                if (markerOrigin != null) {
                    clearMap();
                }
                markerOrigin = googleMap.addMarker(new MarkerOptions().position(latLng).title("origin"));
                pointOrigin = createPoint(latLng);
            } else {
                markerDestination = googleMap.addMarker(new MarkerOptions().position(latLng).title("destination"));
                calculateRoute(latLng);
            }
        });

        fabDisplaySource.setOnClickListener(view -> {
            try {
                new AlertDialog.Builder(this)
                        .setTitle("GeoJson")
                        .setMessage(routeLayerSource.toString(4))
                        .create()
                        .show();
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void calculateRoute(LatLng latLng) {
        Point pointDestination = createPoint(latLng);
        DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                .from(pointOrigin, new Angle(0))
                .to(pointDestination)
                .build();
        SitumSdk.directionsManager().requestDirections(directionsRequest, new Handler<Route>() {
            @Override
            public void onSuccess(Route route) {
                drawRoute(route);
                centerCamera(route);
                hideProgress();
                pointOrigin = null;
            }

            @Override
            public void onFailure(Error error) {
                hideProgress();
                clearMap();
                pointOrigin = null;
                Toast.makeText(DrawRouteGeojsonActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearMap() {
        markerOrigin.remove();
        markerDestination.remove();
        removeRouteLayer();
    }

    private Point createPoint(LatLng latLng) {
        Coordinate coordinate = new Coordinate(latLng.latitude, latLng.longitude);
        CartesianCoordinate cartesianCoordinate = coordinateConverter.toCartesianCoordinate(coordinate);
        return new Point(building.getIdentifier(), floorId, coordinate, cartesianCoordinate);
    }

    private void removeRouteLayer() {
        if (routeLayer != null) {
            routeLayer.removeLayerFromMap();
        }
    }

    private void drawRoute(Route route) {
        try {
            routeLayerSource = Converter.geoJsonFromRoute(route);
            Log.d(TAG, routeLayerSource.toString());
            routeLayer = new GeoJsonLayer(googleMap, routeLayerSource);
            routeLayer.addLayerToMap();
            fabDisplaySource.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.container), e.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

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
                .positionFromBounds(latLngBounds));

        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    private void centerCamera(Route route) {
        Coordinate from = route.getFrom().getCoordinate();
        Coordinate to = route.getTo().getCoordinate();

        LatLngBounds.Builder builder = new LatLngBounds.Builder()
                .include(new LatLng(from.getLatitude(), from.getLongitude()))
                .include(new LatLng(to.getLatitude(), to.getLongitude()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    private void setup() {
        progressBar = findViewById(R.id.progressBar);
        fabDisplaySource = findViewById(R.id.fabDisplaySource);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    void fetchFirstFloorImage(Building building, Callback callback) {
        SitumSdk.communicationManager().fetchFloorsFromBuilding(building.getIdentifier(), new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> floorsCollection) {
                List<Floor> floors = new ArrayList<>(floorsCollection);
                Floor firstFloor = floors.get(0);
                floorId = firstFloor.getIdentifier();
                SitumSdk.communicationManager().fetchMapFromFloor(firstFloor, new Handler<Bitmap>() {
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

    interface Callback {
        void onSuccess(Bitmap floorImage);

        void onError(Error error);
    }
}
