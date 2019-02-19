## <a name="communicationmanager"></a> Get buildings' information

Now that you have correctly configured your Android project, you can start writing your application's code.
In this sample project, all the code has been included in the file
[PositioningActivity.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/positioning/PositioningActivity.java).

In order to access the buildings' info, first of all you need to get an instance of the `CommunicationManager` with `SitumSdk.communicationManager()`.
This object allows you to fetch your buildings' data (list of buildings, floorplans, points of interest, etc.):

For instance, in the next snippet we fetch all the buildings associated with our user's account and print them to the Logcat:

```java
 SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {
            @Override
            public void onSuccess(Collection<Building> buildings) {
                Log.d(TAG, "onSuccess: Your buildings: ");
                for (Building building : buildings) {
                    Log.i(TAG, "onSuccess: " + building.getIdentifier() + " - " + building.getName());
                }
            }

            @Override
            public void onFailure(Error error) {
                Log.e(TAG, "onFailure:" + error);
            }
        });
```

## <a name="positioning"></a> Start the positioning

In order to start the indoor positioning within a building, we will need to obtain this building first. In order to do that, please refer to the previous section: [Get buildings' information](#communicationmanager).

Then, in order to retrieve the location of the smartphone within this `Building`, you will need to create a `LocationRequest` indicating the `Building` where you want to start the positioning:

```java
LocationRequest locationRequest = new LocationRequest.Builder()
        .buildingIdentifier(selectedBuilding.getIdentifier())
        .build();
```

Also, you will need to implement the `LocationListener` that will receive the location updates, status and errors.

```java
private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged() called with: location = [" + location + "]");
        }

        @Override
        public void onStatusChanged(@NonNull LocationStatus status) {
            Log.i(TAG, "onStatusChanged() called with: status = [" + status + "]");
        }

        @Override
        public void onError(@NonNull Error error) {
            Log.e(TAG, "onError() called with: error = [" + error + "]");
        }
    };

```

In `onLocationChanged(Location)` your application will receive the location updates. This `Location` object contains
the building identifier, level identifier, cartesian coordinates, geographic coordinates, orientation,
accuracy, among other location information of the smartphone where the app is running.

In `onStatusChanged(int)` the app will receive changes in the status of the system: `STARTING`, `CALCULATING`,
`USER_NOT_IN_BUILDING`, etc.  Please refer to
[javadoc](http://developers.situm.es/pages/android/api_documentation.html) for a full explanation of
these states.

In `onError(Error)` you will receive updates only if an error has occurred. In this case, the positioning will stop.
Please refer to [javadoc](http://developers.situm.es/pages/android/api_documentation.html) for a full explanation of these errors.

From API 23 you need to ask your user for the location permissions at runtime. If the location permission is not
granted, an error with code `LocationErrorConstant.Code.MISSING_LOCATION_PERMISSION` will be received.
Also, the location permission must be enabled in order to scan Wifi and BLE. In other case, an error with code `LocationErrorConstant.Code.LOCATION_DISABLED`
will be received. In the code sample within this project you can see how to manage this errors.

Finally, you can start the positioning with:

```java
SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
```
and start receiving location updates.