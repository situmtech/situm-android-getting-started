## <a name="communicationmanager"></a> Get buildings' information

Now that you have correctly configured your Android project, you can start writing your application's code.
In this sample project, all the code has been included in the file
[PositioningActivity.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/positioning/PositioningActivity.java).

The following image shows the sample app on the left, with the user's position data on the screen. On the right, the Situm dashboard representing the information on the map.

<p align="center">
    <img src="/img/indoor.gif" />
</p>

## <a name="positioning"></a> Start the positioning

In order to retrieve the location of the smartphone within a `Building`, you will need to create a `LocationRequest` indicating the `Building` where you want to start the positioning:

```java
LocationRequest locationRequest = new LocationRequest.Builder()
        .buildingIdentifier(selectedBuildingId)
        .build();
```

Also, you will need to implement the `LocationListener` that will receive the location updates, status and errors.

In `onLocationChanged(Location)` your application will receive the location updates. This `Location` object contains
the building identifier, level identifier, cartesian coordinates, geographic coordinates, orientation,
accuracy, among other location information of the smartphone where the app is running.

In `onStatusChanged(int)` the app will receive changes in the status of the system: `STARTING`, `CALCULATING`,
`USER_NOT_IN_BUILDING`, etc. Please refer to [javadoc](http://developers.situm.es/sdk_documentation/android/javadoc/latest) for a full explanation of these states.

In `onError(Error)` you will receive updates only if an error has occurred. In this case, the positioning will stop.
Please refer to [javadoc](http://developers.situm.es/sdk_documentation/android/javadoc/latest) for a full explanation of these errors.

Finally, you can start the positioning with:

```java
SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
```

and start receiving location updates.

Remember: location permissions are needed to run this example.
