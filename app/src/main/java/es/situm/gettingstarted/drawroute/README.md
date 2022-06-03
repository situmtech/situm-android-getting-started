## <a name="drawroute"></a> Show routes between POIs in Google Maps
This functionality will allow you to draw a route between two points inside a `Building`. As in the previous examples, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

In this example, we will show a route between two `POI`s of a `Building`. Therefore, in the first place you will need to get a `Building` and its `POI`s using the `CommunicationManager`. Please refer to the
[Show POIs over Google Maps](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawpois) example in order to retrieve this information.

After obtaining the basic information, you can request a route between two of the retrieved `POI`s to the `DirectionsManager`. The route will be received on the `onSuccess` callback of the `DirectionsManager`. At this point, you will be able to draw a Google Maps polyline to represent the route.

<p align="center">
    <img src="/img/route-two-points.gif" />
</p>

You can see how to do this in `DrawRouteActivity`
