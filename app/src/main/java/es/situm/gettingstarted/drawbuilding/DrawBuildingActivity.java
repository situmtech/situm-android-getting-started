package es.situm.gettingstarted.drawbuilding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.utils.Handler;

public class DrawBuildingActivity
        extends SampleActivity
        implements OnMapReadyCallback {

    private GoogleMap map;
    private Building building;
    private FloorAdapter floorAdapter;
    private List<Floor> floorList;

    private RecyclerView selector;
    private ImageView selectorMarkTop;
    private ImageView selectorMarkBottom;
    private GroundOverlay groundOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        building = getBuildingFromIntent();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Method triggered after the map is ready
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        fetchFloorsFromBuilding(building);

        // Declaration of the visual components of the selector

        selectorMarkTop = findViewById(R.id.situm_floor_selector_mark_top);
        selectorMarkBottom = findViewById(R.id.situm_floor_selector_mark_bottom);

        selector = findViewById(R.id.recycler_level_list);
        selector.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));// When reverseOrder is set to true, automatically scrolls to top.

    }

    /**
     * Iterates the collection building's floors with the method fetchFloorsFromBuilding() of SitumSdk, stores them in reverse order.
     */
    void fetchFloorsFromBuilding(Building building) {

        SitumSdk.communicationManager().fetchFloorsFromBuilding(building.getIdentifier(), new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> floorsCollection) {

                // First, store in the reverse order the collection
                floorList = new ArrayList<>(floorsCollection);
                Collections.reverse(floorList);

                // Once we got the floors we prepare de selector
                prepareSelector();
            }

            @Override
            public void onFailure(Error error) {
                onError(error);
            }

        });

    }

    /**
     * Prepares the RecyclerView parameters of the selector.
     */
    void prepareSelector() {
        // Secondly, if the building has more than 3 floors, display the scroll marks to indicate it
        if (floorList.size() > 3) {
            selectorMarkTop.setVisibility(View.VISIBLE);
            selectorMarkBottom.setVisibility(View.VISIBLE);
        }
        floorAdapter = new FloorAdapter(floorList, this::onSelectFloor);

        // Finally we set the adapter to the RecyclerView
        selector.setAdapter(floorAdapter);
        selector.scrollToPosition(floorList.size() - 1);

        // The next method selects by default the first item of the List<>
        onSelectFloor(floorList.get(floorList.size() - 1));
    }

    /**
     * Listener of the RecyclerView, that draws the respective floor and selects the item touched.
     */
    synchronized void onSelectFloor(Floor newFloor) {

        if (floorAdapter.getSelected() != null && floorAdapter.getSelected().equals(newFloor))
            return;

        SitumSdk.communicationManager().fetchMapFromFloor(newFloor, new Handler<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                drawFloor(bitmap);
                floorAdapter.select(newFloor);
            }

            @Override
            public void onFailure(Error error) {
                onError(error);
            }
        });
    }

    /**
     * Receives a floor plan to paint it inside the building bounds.
     */
    void drawFloor(Bitmap bitmap) {
        Bounds drawBounds = building.getBounds();
        Coordinate coordinateNE = drawBounds.getNorthEast();
        Coordinate coordinateSW = drawBounds.getSouthWest();
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

        if (groundOverlay != null) {
            groundOverlay.remove();
        }

        groundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .bearing((float) building.getRotation().degrees())
                .positionFromBounds(latLngBounds));

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    void onError(Error error) {
        Snackbar.make(findViewById(R.id.container), error.getMessage(), Snackbar.LENGTH_INDEFINITE)
                .show();
    }
}