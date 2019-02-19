## <a name="drawbuilding"><a/> Show a building in Google Maps
Another interesting functionality is to show the floorplan of a building on top of your favorite GIS provider. In this example, we will show you how to do it by using Google Maps, but you might use any other of your choosing, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

As a required step, you will need to complete the steps in the [Setup Google Maps](https://github.com/situmtech/situm-android-getting-started#mapsapikey) section. Once this is done, you should
 obtain the floors of the target `Building`. For this purpose, you may refer to the
[GetBuildingImageUseCase.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/drawbuilding/GetBuildingImageUseCase.java) Java file.
After that, you can get the floorplan (bitmap) of each floor using the Situm `CommunicationManager`.
```java
SitumSdk
    .communicationManager()
    .fetchMapFromFloor(floor, new Handler<Bitmap>() {
                                    @Override
                                    public void onSuccess(Bitmap bitmap) {
                                        drawBuilding(building, bitmap);
                                    }

                                    @Override
                                    public void onFailure(Error error) {
                                        //handle error
                                    }
                                });
```

Once you have the bitmap of the `Building` floor you can draw it on top of Google Maps.
```java
void drawBuilding(Building building, Bitmap bitmap){
        Bounds drawBounds = building.getBounds();
        Coordinate coordinateNE = drawBounds.getNorthEast();
        Coordinate coordinateSW = drawBounds.getSouthWest();
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

        map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .bearing((float) building.getRotation().degrees())
                .positionFromBounds(latLngBounds));

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }
```
