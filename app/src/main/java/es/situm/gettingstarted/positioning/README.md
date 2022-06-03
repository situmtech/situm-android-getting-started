## <a name="communicationmanager"></a> Get buildings' information

Now that you have correctly configured your Android project, you can start writing your application's code.
In this sample project, all the code has been included in the file
[PositioningActivity.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/positioning/PositioningActivity.java).

![indoor](/img/indoor.gif)

In order to access the buildings info, first of all you need to get an instance of the `CommunicationManager` with `SitumSdk.communicationManager()`.
This object allows you to fetch your buildings data (list of buildings, floorplans, points of interest, etc.):

To get the buildings you need to call the method `fetchBuildings`

## <a name="positioning"></a> Start the positioning

Then, in order to retrieve the location of the smartphone within a `Building`, you will need to create a `LocationRequest` indicating the `Building` where you want to start the positioning:

```java
LocationRequest locationRequest = new LocationRequest.Builder()
        .buildingIdentifier(selectedBuilding.getIdentifier())
        .build();
```

Also, you will need to implement the `LocationListener` that will receive the location updates, status and errors.

In `onLocationChanged(Location)` your application will receive the location updates. This `Location` object contains
the building identifier, level identifier, cartesian coordinates, geographic coordinates, orientation,
accuracy, among other location information of the smartphone where the app is running.

In `onStatusChanged(int)` the app will receive changes in the status of the system: `STARTING`, `CALCULATING`,
`USER_NOT_IN_BUILDING`, etc.  Please refer to
[javadoc](http://developers.situm.es/sdk_documentation/android/javadoc/latest) for a full explanation of
these states.

In `onError(Error)` you will receive updates only if an error has occurred. In this case, the positioning will stop.
Please refer to [javadoc](http://developers.situm.es/sdk_documentation/android/javadoc/latest) for a full explanation of these errors.

From API 23 you need to ask your user for the location permissions at runtime. If the location permission is not
granted, an error with code `LocationErrorConstant.Code.MISSING_LOCATION_PERMISSION` will be received.
Also, the location permission must be enabled in order to scan Wifi and BLE. In other case, an error with code `LocationErrorConstant.Code.LOCATION_DISABLED`
will be received. In the code sample within this project you can see how to manage this errors.

Finally, you can start the positioning with:

```java
SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
```
and start receiving location updates.