## <a name="rt"><a/> Show the location of other devices in real time over Google Maps
This functionality will allow you to show the real-time location of devices that are being positioned inside a `Building` over Google Maps. Remember that you can use any other GIS of your choosing, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

First of all, you will need to retrieve the information of the desired `Building`. In the following example, we will just get the first `Building` returned.

The next step is to obtain the `RealtimeManager` and retrieve the real-time location updates of all the smartphones inside the selected `Building`. This method requires a `RealTimeRequest` object with the following information: milliseconds between location updates and identifier of the target `Building`. Additionally, this method requires the listener where the location updates should be directed.

In the following example, we are also going to remove/place markers and restrict the camera to the markers bounds.
You can see how to do it in the method `realtime` of the class `RealTimeActivity`
