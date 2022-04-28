package es.situm.gettingstarted.drawbuilding;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.floorselector.FloorSelectorView;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.model.cartography.Building;

public class DrawBuildingActivity
        extends SampleActivity
        implements OnMapReadyCallback {

    private Building building;

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
        FloorSelectorView floorSelectorView = findViewById(R.id.situm_floor_selector);
        floorSelectorView.setFloorSelector(building, googleMap);
    }

}