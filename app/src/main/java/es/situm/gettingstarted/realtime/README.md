## <a name="rt"><a/> Show the location of other devices in real time over Google Maps
This functionally will allow you to show the real-time location of devices that are being positioned inside a `Building` over Google Maps. Remember that you can use any other GIS of your choosing, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

First of all, you will need to retrieve the information of the desired `Building`. In the following example, we will just get the first `Building` returned.

For convenience we have created a class called GetBuildingsUseCase that helps us to obtain a list of `Building`s.
```java
GetBuildingsUseCase getBuildingsUseCase = new GetBuildingsUseCase();
getBuildingsUseCase.get(new GetBuildingsUseCase.Callback() {
            @Override
            public void onSuccess(List<Building> buildings) {
                Building building = buildings.get(0);
                realtime(building);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RealTimeActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
```


The next step is to obtain the `RealtimeManager` and retrieve the  real-time location updates of all the smartphones inside the selected `Building`. This method requires a `RealTimeRequest` object with the following information: milliseconds between location updates and identifier of the target `Building`. Additionally, this method requires the listener where the location updates should be directed.

In the following example, we are also going to remove/place markers and restrict the camera to the markers bounds.
```java
void realtime(Building building) {
    RealTimeRequest realTimeRequest = new RealTimeRequest.Builder()
            .pollTimeMs(3000)
            .building(building)
            .build();
    SitumSdk.realtimeManager().requestRealTimeUpdates(realTimeRequest, new RealTimeListener() {
        @Override
        public void onUserLocations(RealTimeData realTimeData) {
            if(realTimeData.getLocations().isEmpty()){
                noDevicesTV.setVisibility(View.VISIBLE);
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();
            }else {
                noDevicesTV.setVisibility(View.GONE);
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Location location : realTimeData.getLocations()) {
                    LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                            location.getCoordinate().getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(location.getDeviceId());
                    Marker marker = googleMap.addMarker(markerOptions);
                    markers.add(marker);
                    builder.include(latLng);
                }
                try {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                } catch (IllegalStateException e) {
                }
            }
        }

        @Override
        public void onError(Error error) {
            Toast.makeText(RealTimeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    });
}
```

Do not forget to remove real time updates when destroying the activity/fragment or whenever you might consider.
```java
@Override
protected void onDestroy() {¡
    SitumSdk.locationManager().removeUpdates(locationListener);
    super.onDestroy();
}
```

