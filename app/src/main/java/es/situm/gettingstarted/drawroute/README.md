## <a name="drawroute"><a/> Show routes between POIs in Google Maps
This funcionality will allow you to draw a route between two points inside a `Building`. As in the previous examples, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

In this example, we will show a route between two `POI`s of a `Building`. Therefore, in the first place you will need to get a `Building` and its `POI`s using the `CommunicationManager`. Please refer to the
[Show POIs over Google Maps](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawpois) example in order to retrieve this information.

After obtaining the basic information, you can request a route between two of the retrieved `POI`s to the `DirectionsManager`. The route will be received on the `onSuccess` callback of the `DirectionsManager`. At this point, you will be able to draw a Google Maps polyline to represent the route.

For convenience we have created a class called GetPoisUseCase that helps us to obtain the `POI`s for a `Building`.
```java
GetPoisUseCase getPoisUseCase = new GetPoisUseCase();
getPoisUseCase.get(new GetPoisUseCase.Callback() {
        @Override
        public void onSuccess(Building building, Collection<Poi> pois) {
            if (pois.size() < 2){
                Toast.makeText(DrawRouteActivity.this,
                        "Its mandatory to have at least two pois in a building: " + building.getName() + " to start directions manager",
                        Toast.LENGTH_LONG)
                        .show();
            }else {
                Iterator<Poi>iterator = pois.iterator();
                final Point from = iterator.next().getPosition();
                final Point to = iterator.next().getPosition();
                DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                        .from(from, null)
                        .to(to)
                        .build();
                SitumSdk.directionsManager().requestDirections(directionsRequest, new Handler<Route>() {
                    @Override
                    public void onSuccess(Route route) {
                        PolylineOptions polyLineOptions = new PolylineOptions().color(Color.GREEN).width(4f);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        List<Point> routePoints = route.getPoints();
                        for (Point point:routePoints){
                            LatLng latLng = new LatLng(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude());
                            builder.include(latLng);
                            polyLineOptions.add(latLng);
                        }
                        builder.include(new LatLng(from.getCoordinate().getLatitude(), from.getCoordinate().getLongitude()));
                        builder.include(new LatLng(to.getCoordinate().getLatitude(), to.getCoordinate().getLongitude()));
                        googleMap.addPolyline(polyLineOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }

                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(DrawRouteActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void onError(String error) {
            Toast.makeText(DrawRouteActivity.this, error, Toast.LENGTH_LONG).show();
        }
    });
```
