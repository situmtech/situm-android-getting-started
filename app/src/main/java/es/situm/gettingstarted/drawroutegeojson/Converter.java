package es.situm.gettingstarted.drawroutegeojson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.directions.Route;

public class Converter {
    private static final String KEY_TYPE = "type";
    private static final String KEY_FEATURES = "features";
    private static final String KEY_PROPERTIES = "properties";
    private static final String VALUE_TYPE_FEATURE_COLLECTION = "FeatureCollection";
    private static final String VALUE_TYPE_FEATURE = "Feature";
    private static final String VALUE_TYPE_LINESTRING = "LineString";
    private static final String KEY_GEOMETRY = "geometry";
    private static final String KEY_COORDINATES = "coordinates";

    /**
     * Create a JSONObject in GeoJson format from the given route.
     *
     * @param route Route used to create de GeoJson.
     * @return JSONObject in GeoJson format.
     * @throws JSONException Forwarded from JSONObject.
     */
    public static JSONObject geoJsonFromRoute(Route route) throws JSONException {
        // Create and populate the array of lng/lat pairs representing the feature geometry.
        JSONArray coordinates = new JSONArray();
        List<Point> points = route.getPoints();
        for (Point point : points) {
            JSONArray latLng = new JSONArray();
            latLng.put(point.getCoordinate().getLongitude());
            latLng.put(point.getCoordinate().getLatitude());
            coordinates.put(latLng);
        }
        // Create the geometry object itself and put the coordinates:
        JSONObject geometry = new JSONObject();
        geometry.put(KEY_TYPE, VALUE_TYPE_LINESTRING);
        geometry.put(KEY_COORDINATES, coordinates);
        // Create and populate a single MultiLineString feature that will hold the geometry
        // already defined:
        JSONObject multiLineString = new JSONObject();
        multiLineString.put(KEY_TYPE, VALUE_TYPE_FEATURE);
        multiLineString.put(KEY_GEOMETRY, geometry);
        multiLineString.put(KEY_PROPERTIES, new JSONObject());
        // Create and populate the root GeoJson object. Type is "FeatureCollection", contains
        // an array of Features (a single MultiLineString in this case):
        JSONObject root = new JSONObject();
        root.put(KEY_TYPE, VALUE_TYPE_FEATURE_COLLECTION);
        JSONArray features = new JSONArray();
        features.put(multiLineString);
        root.put(KEY_FEATURES, features);
        return root;
    }
}
