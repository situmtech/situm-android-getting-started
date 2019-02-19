## <a name="drawposition"><a/> Show the current position in Google Maps
This functionality will allow you to represent the current position of your device using Google Maps. Instead, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

Note: you are required to configure [Optional step 5: location and runtime permissions](https://github.com/situmtech/situm-android-getting-started#locationpermissions) before proceed with this sample.

First of all, you will need to perform all the steps required to start receiving location updates, as shown in the section [Indoor-outdoor positioning](#indoor-outdoor-positioning).

Then, in the listener callback method `onLocationChanged`, you can insert the code required to draw the circle that represents the position of the device.

```java

LocationListener locationListener = new LocationListener(){

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                location.getCoordinate().getLongitude());
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(1d)
                .strokeWidth(0f)
                .fillColor(Color.BLUE));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    }

    @Override
    public void onStatusChanged(@NonNull LocationStatus locationStatus) {

    }

    @Override
    public void onError(@NonNull Error error) {
        Toast.makeText(DrawPositionActivity.this, error.getMessage() , Toast.LENGTH_LONG).show();
    }
};
LocationRequest locationRequest = new LocationRequest.Builder()
        .build();
SitumSdk().locationManager().requestLocationUpdates(locationRequest, locationListener);
```

Also,  do not forget to stop the service in the onDestroy or any method you might consider.
```java
@Override
protected void onDestroy() {¡
    SitumSdk.locationManager().removeUpdates(locationListener);
    super.onDestroy();
}
```
