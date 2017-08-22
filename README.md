Situm Android SDK Sample app
=======================

This is a sample Android application built with the Situm SDK. With the sample app you will be able 
able to: 
  1. List all the buildings of your account and start the positioning for a selected building, showing 
 the image of the first floor of the building and the locations received.
  2. Draw the building's floor over the Google Maps view.

# Table of contents
#### [Introduction](#introduction)
#### [Setup](#configureproject)
1. [Step 1: Configure our SDK in your Android project](#configureproject)
2. [Step 2: Initialize the SDK](#init)
3. [Step 3: Set your credentials](#config)
4. [Step 4: Setup Google Maps](#mapsapikey)
5. [Optional step 5: location and runtime permissions](#locationpermissions)

#### [Samples](#samples)

1. [Get buildings' information](#communicationmanager)
2. [Start the positioning](#positioning)
3. [Indoor-outdoor positioning](#indoor-outdoor-positioning)
4. [Show a building in Google Maps](#drawbuilding)
5. [Show the current position in Google Maps](#drawposition)
6. [Show POIs (Points of Interest) in Google Maps](#drawpois)
7. [Show routes between POIs in Google Maps](#drawroute)
8. [Show the location of other devices in real time](#rt)

#### [More information](#moreinfo)

### Introduction <a name="introduction"></a>

Situm SDK is a set of utilities that allow any developer to build location based apps using Situm's indoor positioning system. 
Among many other capabilities, apps developed with Situm SDK will be able to:

1. Obtain information related to buildings where Situm's positioning system is already configured: 
floor plans, points of interest, geotriggered events, etc.
2. Retrieve the location of the smartphone inside these buildings (position, orientation, and floor 
where the smartphone is).
3. Compute a route from a point A (e.g. where the smartphone is) to a point B (e.g. any point of 
interest within the building).
4. Trigger notifications when the user enters a certain area.

In this tutorial, we will guide you step by step to set up your first Android application using Situm SDK. 
Before starting to write code, we recommend you to set up an account in our Dashboard 
(https://dashboard.situm.es), retrieve your API KEY and configure your first building.

1. Go to the [sign in form](http://dashboard.situm.es/accounts/register) and enter your username 
and password to sign in.
2. Go to the [account section](https://dashboard.situm.es/accounts/profile) and on the bottom, click 
on "generate one" to generate your API KEY.
3. Go to the [buildings section](http://dashboard.situm.es/buildings) and create your first building.
4. Download [Situm Mapping Tool](https://play.google.com/store/apps/details?id=es.situm.maps) 
Android application. With this application you will be able to configure and test Situm's indoor 
positioning system in your buildings.

Perfect! Now you are ready to develop your first indoor positioning application.

### <a name="configureproject"></a> Step 1: Configure our SDK in your Android project

First of all, you must configure Situm SDK in your Android project. This has been already done for 
you in the sample application, but nonetheless we will walk you through the process.

* Add the maven repository to the project *build.gradle*:

```groovy
allprojects {
    repositories {
        maven { url "https://repo.situm.es/artifactory/libs-release-local" }
    }
}
```

* Then add the Situm SDK library dependency into the section *dependencies* of the app *build.gradle*.
It's important to add the `transitive = true` property to download the Situm SDK dependencies.

```groovy
    compile ('es.situm:situm-sdk:2.7.0@aar') {
        transitive = true
    }
```

### <a name="init"></a> Step 2: Initialize the SDK

You must initialize the SDK in the `onCreate()` method of your Application:

```java
@Override
public void onCreate() {
    super.onCreate();
    SitumSdk.init(this);

}
```

### <a name="config"></a> Step 3: Set your credentials

There are two ways to set the credentials, in the `AndroidManifest.xml` file or programmatically.

##### Option 1: `AndroidManifest.xml` file

You can set the credentials (user and API key) in the `AndroidManifest.xml` file adding the next `meta-data` fields:

```xml
<meta-data
    android:name="es.situm.sdk.API_USER"
    android:value="API_USER_EMAIL" />
<meta-data
    android:name="es.situm.sdk.API_KEY"
    android:value="API_KEY" />
```

##### Option 2: Programmatically

In the code, you can set the the user and API key with:

```java
SitumSdk.configuration().setApiKey("USER_EMAIL", "API_KEY");
```

or you can set the user and password with:

```java
SitumSdk.configuration().setUserPass("USER_EMAIL", "PASSWORD");
```




### <a name="mapsapikey"><a/> Step 4: Setup Google Maps 
This step is only necessary if you want to run the sample that draws the buildings' floorplan over the 
Google Maps map. Otherwise, you can skip it and continue with the [code samples](#samples).

First of all, you need to add the Google Services dependency to the project. If you need more info: 
[Setup Google Play Services](https://developers.google.com/android/guides/setup). To do this, paste the dependency in 
your module *build.gradle* as usual:
```
compile 'com.google.android.gms:play-services-maps:10.0.1'
```
Add in the app manifest the version of the Google Play Services that we have imported. To do this, paste
the next snippet inside the application tag:
```
<meta-data
    android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />
```

An api key is needed to add Google Maps Services and use them in an application. If you need more info,
you can visit the [official Google Maps documentation](https://developers.google.com/maps/documentation/android-api/intro). 
To obtain a Google api key please refer to the same documentation: 
[obtain Google Maps api key](https://developers.google.com/maps/documentation/android-api/signup), 
then go back to your app module file *build.gradle* and replace 'gmaps_api_key' with the value of the 
obtained key from Google.
```
resValue 'string', 'google_maps_key', "YOUR_API_KEY"
```




### <a name="locationpermissions"><a/> Optional step 5: location and runtime permissions 
When we work on features that involve the use of the smartphone location, we need to add fine location permission to the manifest:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```
Also, ensure to check the Android runtime permissions. [More info](https://developer.android.com/training/permissions/requesting.html) .





## Samples <a name="samples"></a>
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



## <a name="indoor-outdoor-positioning"><a/> Indoor-outdoor positioning
Situm SDK can also work in hybrid mode, providing the indoor location when the smartphone is inside a building that has the Situm's technology configured, and the GPS location otherwise. In addition, the Situm SDK handles the indoor-outdoor transition seamlessly.

In order to enable the positioning mode to operate in indoor-outdoor mode it is mandatory to use the LocationManager
without indicating a specific building.

Note: you are required to configure [Optional step 5: location and runtime permissions](#locationpermissions) before proceed with this sample.


1. Build a `LocationRequest` without indicating the `Building` id. The `LocationRequest` can be configured
with many more options, but this is outside of the scope of this example. Check the 
[Javadoc](http://developers.situm.es/pages/android/api_documentation.html) for more information.
2. Build a `LocationListener` in order to receive location updates.
3. After the creation of the required objects, request location updates to the `LocationManager`.

```java
LocationRequest locationRequest = new LocationRequest.Builder().build();
LocationListener locationListener = new LocationListener(){

            @Override
            public void onLocationChanged(@NonNull Location location) {
                //location result
            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                //location manager status, check the codes
            }

            @Override
            public void onError(@NonNull Error error) {
                //an error using location manager, check the code and message to debug operations
            }
        };
SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
```


Also, do not forget to stop the service in the `onDestroy` or any other method you might consider for this purpose.
```java
@Override
protected void onDestroy() {¡
    SitumSdk.locationManager().removeUpdates(locationListener);
    super.onDestroy();
}
```

You can check the complete sample in the [indooroutdoor](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/indooroutdoor)  package.





## <a name="drawbuilding"><a/> Show a building in Google Maps
Another interesting functionality is to show the floorplan of a building on top of your favorite GIS provider. In this example, we will show you how to do it by using Google Maps, but you might use any other of your choosing, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

As a required step, you will need to complete the steps in the [Setup Google Maps](#mapsapikey) section. Once this is done, you should
 obtain the floors of the target `Building`. For this purpose, you may refer to the 
[GetBuildingImageUseCase.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/drawbuilding/GetBuildingImageUseCase.java) Java file.
After that, you can get the floorplan (bitmap) of each floor using the Situm `CommunicationManager`.
```
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
```
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
You can check the complete sample in the [drawbuilding](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawbuilding) package.





## <a name="drawposition"><a/> Show the current position in Google Maps
This functionality will allow you to represent the current position of your device using Google Maps. Instead, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

First of all, you will need to perform all the steps required to start receiving location updates, as shown in the section [Indoor-outdoor positioning](#indoor-outdoor-positioning).

Note: you are required to configure [Optional step 5: location and runtime permissions](#locationpermissions) before proceed with this sample.

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

You can check the complete sample in the [drawposition](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawposition) package.




## <a name="drawpois"><a/> Show POIs (Points of Interest) in Google Maps
This functionality allows to show the list of `POI`s of a `Building` over Google Maps. Instead, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

First of all, we need to retrieve the list of `POI`s of our `Building` using the `CommunicationManager`. As an example, in the next code snippet we use the first `Building` retrieved. 

```java
SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {
    @Override
    public void onSuccess(Collection<Building> buildings) {
        if (!buildings.isEmpty()) {
            final Building building = buildings.iterator().next();
            SitumSdk.communicationManager().fetchIndoorPOIsFromBuilding(building, new Handler<Collection<Poi>>() {
                @Override
                public void onSuccess(Collection<Poi> pois) {
                    if (hasCallback()) {
                        callback.onSuccess(building, pois);
                    }
                    clearCallback();
                }

                @Override
                public void onFailure(Error error) {
                    if (hasCallback()) {
                        callback.onError(error.getMessage());
                    }
                    clearCallback();
                }
            });
        }else{
            if (hasCallback()) {
                callback.onError("There isnt any building in your account. Go to the situm dashboard and create a new one with some pois before execute again this example");
            }
            clearCallback();
        }
    }

    @Override
    public void onFailure(Error error) {
        if (hasCallback()) {
            callback.onError(error.getMessage());
        }
        clearCallback();
    }
});
```

Finally, you can draw the `POI`s over the map.

For convenience we have created a class called GetPoisUseCase that helps us to obtain the `POI`s for a `Building`.
```java
GetPoisUseCase getPoisUseCase = new GetPoisUseCase();
getPoisUseCase.get(new GetPoisUseCase.Callback() {
    @Override
    public void onSuccess(Building building, Collection<Poi> pois) {
        if (pois.isEmpty()){
            Toast.makeText(DrawPoisActivity.this, "There isnt any poi in the building: " + building.getName() + ". Go to the situm dashboard and create at least one poi before execute again this example", Toast.LENGTH_LONG).show();
        }else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Poi poi : pois) {
                LatLng latLng = new LatLng(poi.getCoordinate().getLatitude(),
                        poi.getCoordinate().getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(poi.getName()));
                builder.include(latLng);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }

    @Override
    public void onError(String error) {
        Toast.makeText(DrawPoisActivity.this, error, Toast.LENGTH_LONG).show();
    }
});

```

You can check the complete sample in [drawpois](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawpois) package.




## <a name="drawroute"><a/> Show routes between POIs in Google Maps
This funcionality will allow you to draw a route between two points inside a `Building`. As in the previous examples, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

In this example, we will show a route between two `POI`s of a `Building`. Therefore, in the first place you will need to get a `Building` and its `POI`s using the `CommunicationManager`. Please refer to the 
[Show POIs over Google Maps](#drawpois) example in order to retrieve this information.

After obtaining the basic information, you can request a route between two of the retrieved `POI`s to the `DirectionsManager`. The route will be received on the `onSuccess` callback of the `DirectionsManager`. At this point, you will be able to draw a Google Maps polyline to represent the route.

For convenience we have created a class called GetPoisUseCase that helps us to obtain the `POI`s for a `Building`.
```java
GetPoisUseCase getPoisUseCase = new GetPoisUseCase();
getPoisUseCase.get(new GetPoisUseCase.Callback() {
        @Override
        public void onSuccess(Building building, Collection<Poi> pois) {
            if (pois.size() < 2){
                Toast.makeText(DrawRouteActivity.this,
                        "Its mandatory to have at least two pois in a building: " + building.getName() + " to start directions manager",
                        Toast.LENGTH_LONG)
                        .show();
            }else {
                Iterator<Poi>iterator = pois.iterator();
                final Point from = iterator.next().getPosition();
                final Point to = iterator.next().getPosition();
                DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                        .from(from, null)
                        .to(to)
                        .build();
                SitumSdk.directionsManager().requestDirections(directionsRequest, new Handler<Route>() {
                    @Override
                    public void onSuccess(Route route) {
                        PolylineOptions polyLineOptions = new PolylineOptions().color(Color.GREEN).width(4f);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        List<Point> routePoints = route.getPoints();
                        for (Point point:routePoints){
                            LatLng latLng = new LatLng(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude());
                            builder.include(latLng);
                            polyLineOptions.add(latLng);
                        }
                        builder.include(new LatLng(from.getCoordinate().getLatitude(), from.getCoordinate().getLongitude()));
                        builder.include(new LatLng(to.getCoordinate().getLatitude(), to.getCoordinate().getLongitude()));
                        googleMap.addPolyline(polyLineOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }
    
                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(DrawRouteActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    
        @Override
        public void onError(String error) {
            Toast.makeText(DrawRouteActivity.this, error, Toast.LENGTH_LONG).show();
        }
    });
```

Additionally, you can request navigation updates to the SDK. These updates will inform you of several navigation events: destination reached, user went off the planned route, etc. 

```java
NavigationRequest navigationRequest = new NavigationRequest.Builder()
            .route(route)
            .distanceToGoalThreshold(3d)
            .outsideRouteThreshold(50d)
            .build();
    SitumSdk.navigationManager().requestNavigationUpdates(navigationRequest, new NavigationListener() {
        @Override
        public void onDestinationReached() {
            Log.d(TAG, "onDestinationReached: ");
        }

        @Override
        public void onProgress(NavigationProgress navigationProgress) {
            Log.d(TAG, "onProgress: ");
        }

        @Override
        public void onUserOutsideRoute() {
            Log.d(TAG, "onUserOutsideRoute: ");
        }
    });
```
Do not forget to stop the navigation when destroying your activity/fragment or whenever you might consider.
```java
@Override
protected void onDestroy() {¡
    SitumSdk.locationManager().removeUpdates(locationListener);
    super.onDestroy();
}
```

You can check the complete sample in the [drawroute](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawroute) package.




## <a name="rt"><a/> Show the location of other devices in real time over Google Maps
This functionally will allow you to show the real-time location of devices that are being positioned inside a `Building` over Google Maps. Remember that you can use any other GIS of your choosing, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

First of all, you will need to retrieve the information of the desired `Building`. In the following example, we will just get the first `Building` returned. 

For convenience we have created a class called GetBuildingsUseCase that helps us to obtain a list of `Building`s.
```java
GetBuildingsUseCase getBuildingsUseCase = new GetBuildingsUseCase();
getBuildingsUseCase.get(new GetBuildingsUseCase.Callback() {
            @Override
            public void onSuccess(List<Building> buildings) {
                Building building = buildings.get(0);
                realtime(building);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RealTimeActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
```


The next step is to obtain the `RealtimeManager` and retrieve the  real-time location updates of all the smartphones inside the selected `Building`. This method requires a `RealTimeRequest` object with the following information: milliseconds between location updates and identifier of the target `Building`. Additionally, this method requires the listener where the location updates should be directed. 

In the following example, we are also going to remove/place markers and restrict the camera to the markers bounds.
```java
void realtime(Building building) {
    RealTimeRequest realTimeRequest = new RealTimeRequest.Builder()
            .pollTimeMs(3000)
            .building(building)
            .build();
    SitumSdk.realtimeManager().requestRealTimeUpdates(realTimeRequest, new RealTimeListener() {
        @Override
        public void onUserLocations(RealTimeData realTimeData) {
            if(realTimeData.getLocations().isEmpty()){
                noDevicesTV.setVisibility(View.VISIBLE);
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();
            }else {
                noDevicesTV.setVisibility(View.GONE);
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Location location : realTimeData.getLocations()) {
                    LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                            location.getCoordinate().getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(location.getDeviceId());
                    Marker marker = googleMap.addMarker(markerOptions);
                    markers.add(marker);
                    builder.include(latLng);
                }
                try {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                } catch (IllegalStateException e) {
                }
            }
        }

        @Override
        public void onError(Error error) {
            Toast.makeText(RealTimeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    });
}
```

Do not forget to remove real time updates when destroying the activity/fragment or whenever you might consider.
```java
@Override
protected void onDestroy() {¡
    SitumSdk.locationManager().removeUpdates(locationListener);
    super.onDestroy();
}

You can check the complete sample in the [realtime](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/realtime) package.




## <a name="moreinfo"></a> More information

More info is available at our [Developers Page](https://des.situm.es/developers/pages/android/).
For any other question, contact us in https://situm.es/contact.
