package es.situm.gettingstarted.pointinsidegeofence;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.PolygonOptions;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.GetBuildingCaseUse;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.cartography.Geofence;
import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.location.Bounds;

public class PointInsideGeofenceActivity extends SampleActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private Building building;
    private Floor floor;
    private GoogleMap map;
    private GetBuildingCaseUse getBuildingCaseUse = new GetBuildingCaseUse();
    private GeometryFactory geometryFactory = new GeometryFactory();
    private Marker marker;
    private TextView tvPointStatus;

    private Map<Geofence, Polygon> geofencePolygonMap = new HashMap<>();

    private void setFloor(Floor floor) {
        this.floor = floor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_inside_geofence);
        tvPointStatus = (TextView) findViewById(R.id.tv_point_status);
        building = getBuildingFromIntent();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        getBuildingCaseUse.getGeofences(building, new GetBuildingCaseUse.GeofencesCallback() {
            @Override
            public void onSuccess(Floor floor, Bitmap bitmap, List<Geofence> geofences) {
                drawBuilding(building, bitmap);
                setFloor(floor);
                drawGeofences(geofences);
                createAndAssignPolygonsToGeofences(geofences);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void createAndAssignPolygonsToGeofences(List<Geofence> geofences) {
        if (geofences.isEmpty()) {
            return;
        }
        for (Geofence geofence: geofences) {
            List<Coordinate> jtsCoordinates = new ArrayList<>();
            for (Point point: geofence.getPolygonPoints()) {
                Coordinate coordinate = new Coordinate(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude());
                jtsCoordinates.add(coordinate);
            }
            if (!jtsCoordinates.isEmpty()) {
                Polygon polygon = geometryFactory.createPolygon(jtsCoordinates.toArray(new Coordinate[0]));
                geofencePolygonMap.put(geofence,polygon);
            }
        }
    }

    private void drawGeofences(List<Geofence> geofences) {
        for (Geofence geofence: geofences) {
            if (!geofence.getFloorIdentifier().equals(floor.getIdentifier())) {
                continue;
            }
            createPolygonAndDrawInTheMap(geofence);
        }
    }

    private void createPolygonAndDrawInTheMap(Geofence geofence) {
        PolygonOptions polygonOptions = new PolygonOptions();
        List<LatLng> latLngs = new ArrayList<>();
        for (Point point: geofence.getPolygonPoints()) {
            latLngs.add(new LatLng(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude()));
        }
        polygonOptions.addAll(latLngs);
        polygonOptions.strokeColor(Color.BLACK);
        polygonOptions.strokeWidth(5);
        polygonOptions.fillColor(0x7Ff3ff00);
        polygonOptions.zIndex(2);
        if (!latLngs.isEmpty()) {
            map.addPolygon(polygonOptions);
        }
    }

    private void checkIfPointIsInsideGeofence(org.locationtech.jts.geom.Point point) {
        if (geofencePolygonMap.isEmpty()) {
            return;
        }
        tvPointStatus.setText("");
        String geofenceName = "";
        for (Map.Entry<Geofence, Polygon> entry : geofencePolygonMap.entrySet()) {
            if (!entry.getKey().getFloorIdentifier().equals(floor.getIdentifier())) {
                continue;
            }
            if (point.within(entry.getValue())) {
                geofenceName = entry.getKey().getName();
                Log.i(PointInsideGeofenceActivity.class.getSimpleName(), "The point is inside the geofence: " + entry.getKey().getName());
            }
        }
        if (geofenceName.isEmpty()) {
            tvPointStatus.setText("The point is not inside a geofence");
        } else {
            tvPointStatus.setText("The point is inside the geofence: " + geofenceName);
        }
    }

    private void drawBuilding(Building building, Bitmap bitmap){
        Bounds drawBounds = building.getBounds();
        es.situm.sdk.model.location.Coordinate coordinateNE = drawBounds.getNorthEast();
        es.situm.sdk.model.location.Coordinate coordinateSW = drawBounds.getSouthWest();
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

    @Override
    public void onMapClick(LatLng latLng) {
        checkIfPointIsInsideGeofence(geometryFactory.createPoint(new Coordinate(latLng.latitude, latLng.longitude)));
        if (marker != null) {
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions().position(latLng));
    }
}
