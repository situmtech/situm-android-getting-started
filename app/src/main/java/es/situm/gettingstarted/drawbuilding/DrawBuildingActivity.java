package es.situm.gettingstarted.drawbuilding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, Floor> buildingFloors;
    private RecyclerView selector;
    private ImageView selectorMarkTop;
    private ImageView selectorMarkBottom;

    private FloorAdapter floorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        building = getBuildingFromIntent();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        fetchFloorsImages(building, new Callback() {
            @Override
            public void onSuccess(Bitmap floorImage) {
                drawBuilding(floorImage);
            }

            @Override
            public void onError(Error error) {
                Snackbar.make(findViewById(R.id.container), error.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .show();
            }
        });

        // Declaration of the visual components of the selector

        selectorMarkTop = findViewById(R.id.situm_floor_selector_mark_top);
        selectorMarkBottom = findViewById(R.id.situm_floor_selector_mark_bottom);

        floorAdapter = new FloorAdapter(Collections.<Floor>emptyList(), floor -> {/* do nothing */});
        selector = findViewById(R.id.recycler_level_list);
        selector.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

    }

    void drawBuilding(Bitmap bitmap) {
        Bounds drawBounds = building.getBounds();
        Coordinate coordinateNE = drawBounds.getNorthEast();
        Coordinate coordinateSW = drawBounds.getSouthWest();
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

        map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .bearing((float) building.getRotation().degrees())
                .positionFromBounds(latLngBounds));

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    void fetchFloorsImages(Building building, Callback callback) {
        SitumSdk.communicationManager().fetchFloorsFromBuilding(building.getIdentifier(), new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> floorsCollection) {
                List<Floor> floors = new ArrayList<>(floorsCollection);

                // First instance a new LinkedHashMap to store all the floors in the same order they came [FIFO]
                buildingFloors = new LinkedHashMap<>();
                for(int i=floors.size()-1;i>=0;i--){
                    Floor floor = floors.get(i);
                    buildingFloors.put(floor.getIdentifier(), floor);
                }

                // Secondly, if the building has more than 3 floors, display the scroll marks to indicate it
                if(buildingFloors.size() > 3){
                    selectorMarkTop.setVisibility(View.VISIBLE);
                    selectorMarkBottom.setVisibility(View.VISIBLE);
                }

                // Secondly instance the adapter of the selector
                floorAdapter = new FloorAdapter(new ArrayList<>(buildingFloors.values()), floor -> {

                    SitumSdk.communicationManager().fetchMapFromFloor(floor, new Handler<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            callback.onSuccess(bitmap);
                            floorAdapter.select(floor);
                            int selectedFloorIndex = floorAdapter.getSelectedFloorIndex();
                            //When the selected item naturally occupies one of the recycler's hidden positions, it
                            //needs to be moved (scrolled) to make it visible
                            LinearLayoutManager llm = (LinearLayoutManager) selector.getLayoutManager();
                            if (selectedFloorIndex < llm.findFirstCompletelyVisibleItemPosition() &&
                                    selectedFloorIndex > llm.findLastCompletelyVisibleItemPosition()) {
                                selector.scrollToPosition(selectedFloorIndex);
                            }
                        }

                        @Override
                        public void onFailure(Error error) {
                            callback.onError(error);
                        }
                    });

                });

                Floor defaultFloor = floors.get(0);

                // The next method selects by default the first item of the List<>
                SitumSdk.communicationManager().fetchMapFromFloor(defaultFloor, new Handler<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        callback.onSuccess(bitmap);
                        floorAdapter.select(defaultFloor);
                        selector.scrollToPosition(floorAdapter.getItemCount()-1);
                    }

                    @Override
                    public void onFailure(Error error) {
                        callback.onError(error);
                    }
                });

                // Finally we set the adapter to the RecyclerView
                selector.setAdapter(floorAdapter);

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
