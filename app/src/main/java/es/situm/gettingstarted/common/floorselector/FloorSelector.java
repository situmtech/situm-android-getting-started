package es.situm.gettingstarted.common.floorselector;

import com.google.android.gms.maps.GoogleMap;
import es.situm.sdk.model.cartography.Building;

public class FloorSelector {

    private Building building;
    private GoogleMap map;

    public FloorSelector(Building building, GoogleMap map){
        this.building = building;
        this.map = map;
    }

    Building getBuilding() {
        return building;
    }

    GoogleMap getGoogleMap() {
        return map;
    }
}
