## <a name="drawposition"><a/> Show the current position in Google Maps
This functionality will allow you to represent the current position of your device using Google Maps. Instead, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.
Note: you are required to configure [Optional step 5: location and runtime permissions](https://github.com/situmtech/situm-android-getting-started#locationpermissions) before proceed with this sample.

First of all, you will need to perform all the steps required to start receiving location updates, as shown in the section [Indoor-outdoor positioning](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/indooroutdoor#indoor-outdoor-positioning).

Then, in the listener callback method `onLocationChanged`, you can insert the code required to draw the circle that represents the position of the device.

You can see how to do this in `DrawPositionActivity`


Also,  do not forget to stop the service in the onDestroy or any method you might consider.
