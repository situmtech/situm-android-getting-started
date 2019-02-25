## <a name="drawpois"><a/> Show POIs (Points of Interest) in Google Maps
This functionality allows to show the list of `POI`s of a `Building` over Google Maps. Instead, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

First of all, we need to retrieve the list of `POI`s of our `Building` using the `CommunicationManager` method `fetchIndoorPOIsFromBuilding`.

Now, you can draw the `POI`s over the map,to do this we created a method `drawPoi` in the example.

You can see how to do this in `DrawPoisActivity`
