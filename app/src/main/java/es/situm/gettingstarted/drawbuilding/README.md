## <a name="drawbuilding"></a>Show a building in Google Maps
Another interesting functionality is to show the floorplan of a building on top of your favorite GIS provider. In this example, we will show you how to do it by using Google Maps, but you might use any other of your choosing, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

<p align="center">
    <img src="/img/draw-building.gif" />
</p>

As a required step, you will need to complete the steps in the [Setup Google Maps](https://github.com/situmtech/situm-android-getting-started#mapsapikey) section. Once this is done, you should obtain the floors of the target `Building`. For this purpose, you may refer to the
[GetBuildingImageUseCase.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/drawbuilding/GetBuildingImageUseCase.java) file.
After that, you can get the floorplan (bitmap) of each floor using the Situm `CommunicationManager` and calling `fetchMapFromFloor`.

Once you have the bitmap of the `Building` floor you can draw it on top of Google Maps.
You can see how to do it in the method `drawBuilding` of the class `DrawBuildingActivity`
