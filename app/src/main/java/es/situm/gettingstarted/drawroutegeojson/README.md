## <a name="drawroutegeojson"></a> Draw GeoJson route
This functionality will allow you to:
1. Calculate a route between two points inside a `Building`.
2. Convert route to a **GeoJson** object.
3. Draw the GeoJson object in a map.
4. Analyze the GeoJson source in a dialog.

As in the previous examples, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

In this example, we will show a route between two points of a `Building`. Therefore, in the first place you will need to get a `Building` and its floor plan using the `CommunicationManager`. Please refer to the [Draw Building](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawbuilding) example in order to retrieve this information.

Next, you can request a route between two points using the `DirectionsManager`. The route will be received on the `onSuccess` callback of the `DirectionsManager`.

Once you receive the `Route` object, you can use the `Converter` utility provided with this example to convert it to a JSON object in **GeoJson** format. 

Finally you will be able to draw a `GeoJsonLayer` in the Google Map to represent the route, or use the map provider of your choice.

<p align="center">
    <img src="/img/geojson-route-map.png" />
    <img src="/img/geojson-route-source.png" />
</p>

You can see how to do this in `DrawRouteGeoJsonActivity`
