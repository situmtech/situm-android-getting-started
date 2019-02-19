## <a name="drawpois"><a/> Show POIs (Points of Interest) in Google Maps
This functionality allows to show the list of `POI`s of a `Building` over Google Maps. Instead, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

First of all, we need to retrieve the list of `POI`s of our `Building` using the `CommunicationManager`. As an example, in the next code snippet we use the first `Building` retrieved.

```java
SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {
    @Override
    public void onSuccess(Collection<Building> buildings) {
        if (!buildings.isEmpty()) {
            final Building building = buildings.iterator().next();
            SitumSdk.communicationManager().fetchIndoorPOIsFromBuilding(building, new Handler<Collection<Poi>>() {
                @Override
                public void onSuccess(Collection<Poi> pois) {
                    if (hasCallback()) {
                        callback.onSuccess(building, pois);
                    }
                    clearCallback();
                }

                @Override
                public void onFailure(Error error) {
                    if (hasCallback()) {
                        callback.onError(error.getMessage());
                    }
                    clearCallback();
                }
            });
        }else{
            if (hasCallback()) {
                callback.onError("There isnt any building in your account. Go to the situm dashboard and create a new one with some pois before execute again this example");
            }
            clearCallback();
        }
    }

    @Override
    public void onFailure(Error error) {
        if (hasCallback()) {
            callback.onError(error.getMessage());
        }
        clearCallback();
    }
});
```

Finally, you can draw the `POI`s over the map.

For convenience we have created a class called GetPoisUseCase that helps us to obtain the `POI`s for a `Building`.
```java
GetPoisUseCase getPoisUseCase = new GetPoisUseCase();
getPoisUseCase.get(new GetPoisUseCase.Callback() {
    @Override
    public void onSuccess(Building building, Collection<Poi> pois) {
        if (pois.isEmpty()){
            Toast.makeText(DrawPoisActivity.this, "There isnt any poi in the building: " + building.getName() + ". Go to the situm dashboard and create at least one poi before execute again this example", Toast.LENGTH_LONG).show();
        }else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Poi poi : pois) {
                LatLng latLng = new LatLng(poi.getCoordinate().getLatitude(),
                        poi.getCoordinate().getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(poi.getName()));
                builder.include(latLng);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }

    @Override
    public void onError(String error) {
        Toast.makeText(DrawPoisActivity.this, error, Toast.LENGTH_LONG).show();
    }
});

```
