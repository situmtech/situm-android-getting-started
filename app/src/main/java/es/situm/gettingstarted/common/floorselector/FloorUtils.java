package es.situm.gettingstarted.common.floorselector;

import es.situm.sdk.model.cartography.Floor;

public class FloorUtils {

    public static String getNameOrLevel(Floor floor) {
        String levelName = floor.getName();
        return levelName.isEmpty() ? String.valueOf(floor.getLevel()) : levelName;
    }
}
