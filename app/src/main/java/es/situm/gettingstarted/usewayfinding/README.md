## <a name="use-wayfinding"></a> Wayfinding

Starting on Situm SDK 3.0.0, you can also integrate a fully interactive visual component called `MapView`.

Checkout the example code at [WayfindingActivity.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/usewayfinding/WayfindingActivity.java).

`MapView` is a standard Android view that you can integrate into your application. You will need to **load** it so you can obtain the associated `MapViewController`, which will let you interact with the map:

```java
MapView mapView = findViewById(R.id.exampleMapView);
MapViewConfiguration mapViewConfiguration = new MapViewConfiguration.Builder()
        .setBuildingIdentifier(selectedBuildingId)
        .build();
mapView.load(mapViewConfiguration, new MapView.MapViewCallback() {
    @Override
    public void onLoad(@NonNull MapViewController mapViewController) {
        // Keep the controller to interact with the map.
        WayfindingActivity.this.mapViewController = mapViewController;
    }

    @Override
    public void onError(@NonNull Error error) {
        Log.e(TAG, "Situm> wayfinding> Error loading wayfinding: " + error.getMessage());
    }
});
```

Once the positioning has been started, `MapView` will draw the user position automatically.

Remember: location permissions are needed to run this example.
