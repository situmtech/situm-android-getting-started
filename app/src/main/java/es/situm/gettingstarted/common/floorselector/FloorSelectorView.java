package es.situm.gettingstarted.common.floorselector;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import es.situm.gettingstarted.R;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.utils.Handler;

public class FloorSelectorView extends ConstraintLayout {

    private Building building;
    private GoogleMap map;
    private GroundOverlay groundOverlay;

    private FloorAdapter floorAdapter;
    private List<Floor> floorList;

    private RecyclerView selector;
    private ImageView selectorMarkTop;
    private ImageView selectorMarkBottom;

    private boolean isFirstCameraAnimation = true;

    public FloorSelectorView(Context context) {
        super(context);
        setup();
    }

    public FloorSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public FloorSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    /**
     * Instances the FloorSelector components
     */
    public void setup(){
        LayoutInflater.from(getContext()).inflate(R.layout.situm_level_list, this);

        selectorMarkTop = findViewById(R.id.situm_floor_selector_mark_top);
        selectorMarkBottom = findViewById(R.id.situm_floor_selector_mark_bottom);
        selector = findViewById(R.id.recycler_level_list);
        selector.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));// When reverseOrder is set to true, automatically scrolls to top.
    }

    /**
     * Reset the RecyclerView
     *
     */
    public void reset(){
        floorAdapter.positioningFloorChangedTo(null);
        fetchFloorsFromBuilding();
    }
    /**
     * Gets the GoogleMap and Building from the FloorSelector
     *
     * @param selector FloorSelector
     */
    public void loadSelector(FloorSelector selector) {
        this.building = selector.getBuilding();
        this.map = selector.getGoogleMap();

        fetchFloorsFromBuilding();
    }

    /**
     * Iterates the collection building's floors with the method fetchFloorsFromBuilding() of SitumSdk, stores them in reverse order.
     */
    void fetchFloorsFromBuilding() {

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
     * Prepares the FloorSelectorView.
     */
    void prepareSelector() {
        // Secondly, if the building has more than 3 floors, display the scroll marks to indicate it
        if (floorList.size() > 3) {
            selectorMarkTop.setVisibility(View.VISIBLE);
            selectorMarkBottom.setVisibility(View.VISIBLE);
        }
        floorAdapter = new FloorAdapter(floorList, this::onSelectFloor, getContext());

        // Finally we set the adapter to the RecyclerView
        selector.setAdapter(floorAdapter);
        selector.scrollToPosition(floorList.size() - 1);

        // The next method selects by default the first item of the List<>
        onSelectFloor(floorList.get(floorList.size() - 1));
    }

    /**
     * Listener of the RecyclerView, that draws the respective floor and indicates it on the selector.
     */
    void onSelectFloor(Floor newFloor) {

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

        if(isFirstCameraAnimation){
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
            isFirstCameraAnimation = false;
        }
    }

    void onError(Error error) {
        Log.e("onError()", error.getMessage());
        Snackbar.make(findViewById(R.id.container), error.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
    }

    /**
     * Searches the new floor we are positioning and indicates the change in the selector
     *
     * @param positioningFloorId String
     */
    public void updatePositioningFloor(String positioningFloorId) {
        Floor newFloor = searchFloorById(positioningFloorId);

        SitumSdk.communicationManager().fetchMapFromFloor(newFloor, new Handler<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                floorAdapter.positioningFloorChangedTo(newFloor);
            }

            @Override
            public void onFailure(Error error) {
                onError(error);
            }
        });
    }

    /**
     * Finds the floor by the Identifier provided in the parameters
     *
     * @param floorId Identifier of the floor we want to get
     * @return Floor
     */
    private Floor searchFloorById(String floorId) {
        Floor floorFound = null;

        for (Floor f:floorList) {
            if(f.getIdentifier().equals(floorId)){
                floorFound = f;
            }
        }

        return floorFound;
    }

    /**
     * Asks to FloorAdapter what is the floor selected
     *
     * @return String
     */
    public String getSelectedFloorId() {
        return floorAdapter.getSelected() != null ? floorAdapter.getSelected().getIdentifier() : null;
    }
}
