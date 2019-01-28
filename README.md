Situm Android SDK Sample app
=======================

This is a sample Android application built with the Situm SDK. With the sample app you will be able 
able to: 
  1. List all the buildings of your account and start the positioning for a selected building, showing 
 the image of the first floor of the building and the locations received.
  2. Draw the building's floor over the Google Maps view.
  3. List all the POIs(points of interest) of a building.
  4. Show routes between two POIs.
  5. Show the location of other devices in realtime.
 
  
# Table of contents
#### [Introduction](#introduction)
#### [Setup](#configureproject)
1. [Step 1: Configure our SDK in your Android project](#configureproject)
2. [Step 2: Initialize the SDK](#init)
3. [Step 3: Set your credentials](#config)
4. [Step 4: Setup Google Maps](#mapsapikey)
5. [Optional step 5: location and runtime permissions](#locationpermissions)
6. [Optional step 6: Setup indoor positioning](#indoorpositioning)

#### [Samples](#samples)

1. [Get buildings' information](#communicationmanager)
2. [Start the positioning](#positioning)
3. [Indoor-outdoor positioning](#indoor-outdoor-positioning)
4. [Show a building in Google Maps](#drawbuilding)
5. [Show the current position in Google Maps](#drawposition)
6. [Show POIs (Points of Interest) in Google Maps](#drawpois)
7. [Show routes between POIs in Google Maps](#drawroute)
8. [Show the location of other devices in real time](#rt)
9. [List all the events in a building](#buildingevents)
10. [Calculate if the user is inside an event](#positionevents)
11. [Filter building's POIs](#filterpois)
12. [Instructions while going from one point to another](#guideinstructions)
13. [Animate the position arrow while walking](#animateposition)

#### [More information](#moreinfo)
#### [Support information](#supportinfo)

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

### <a name="configureproject"></a> Step 1: configure our SDK in your Android project

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
    implementation ('es.situm:situm-sdk:2.31.2@aar') {
        transitive = true
    }
```

### <a name="init"></a> Step 2: initialize the SDK

You must initialize the SDK in the `onCreate()` method of your Application:

```java
@Override
public void onCreate() {
    super.onCreate();
    SitumSdk.init(this);

}
```

### <a name="config"></a> Step 3: set your credentials

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

##### Option 2: programmatically

In the code, you can set the the user and API key with:

```java
SitumSdk.configuration().setApiKey("USER_EMAIL", "API_KEY");
```

or you can set the user and password with:

```java
SitumSdk.configuration().setUserPass("USER_EMAIL", "PASSWORD");
```




### <a name="mapsapikey"><a/> Step 4: setup Google Maps 
This step is only necessary if you want to run the sample that draws the buildings' floorplan over the 
Google Maps map. Otherwise, you can skip it and continue with the [code samples](#samples).

First of all, you need to add the Google Services dependency to the project. If you need more info: 
[Setup Google Play Services](https://developers.google.com/android/guides/setup). To do this, paste the dependency in 
your module *build.gradle* as usual:
```
implementation 'com.google.android.gms:play-services-maps:16.0.0'
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





### <a name="locationpermissions"><a/> Optional step 5: location runtime permissions
	
All the necessary permissions are already declared by the SDK, but the `ACCESS_COARSE_LOCATION` is a dangerous permission, so your app must prompt the user to grant permission at runtime. More info at [Android Developers](https://developer.android.com/training/permissions/requesting.html) .

If you want to use indoor-outdoor positioning using GPS, you must declare `ACCESS_FINE_LOCATION` permission in your `AndroidManifest.xml` and request the permission at runtime. 



### <a name="indoorpositioning"></a> Optional step 6: setup indoor positioning
In order to use indoor positioning, you must define which building you want to position in. This can be achieved by editing the value of `private static final String BUILDING_ID = "YOUR_BUILDING_ID";` in PositioningActivity





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
Situm SDK can also work in hybrid mode, providing the indoor location when the smartphone is inside a building that has the Situm's technology configured, and the Google Play services location otherwise. In addition, the Situm SDK handles the indoor-outdoor transition seamlessly.

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
You can check the complete sample in the [drawbuilding](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawbuilding) package.





## <a name="drawposition"><a/> Show the current position in Google Maps
This functionality will allow you to represent the current position of your device using Google Maps. Instead, you can also use another GIS provider, such as OpenStreetMaps, Carto, ESRI, Mapbox, etc.

Note: you are required to configure [Optional step 5: location and runtime permissions](#locationpermissions) before proceed with this sample.

First of all, you will need to perform all the steps required to start receiving location updates, as shown in the section [Indoor-outdoor positioning](#indoor-outdoor-positioning).

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
```

You can check the complete sample in the [realtime](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/realtime) package.


## <a name="buildingevents"></a> List Building Events
In order to know all the `Event` you have in your `Building`, the first thing you have to do is to fetch your buildings and select the one you want to check. This SDK allows you to know the exact position of the `Event` and to know where the message in your smartphone will be shown. In the following example we will show you how to fetch the `Building's` `Events` and how to list them in order to know the details for each one.

First of all, to fetch the events you will have to use the `communicationManager()` again:
```java
SitumSdk.communicationManager().fetchEventsFromBuilding(building, new Handler<Collection<SitumEvent>>(){

	@Override
	public void onSuccess(Collection<SitumEvent> situmEvents) {
		events.clear();
		events.addAll(situmEvents);
		showEventDataView();
		eventAdapter.setEventData(events);
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onFailure(Error error) {
		Log.e(TAG, "onFailure: fetching events: " + error);
		showErrorMessage();
	}
});
```

I you want to show the `Event` data, you will have to use the `SitumEvent`variable that provides a method called `getHtml()` and `getName()`, the first method returns the value that the `Event` will display in the Smartphone when over the `Event's` conversion area, the second one returns the name of the event.

Here you can see a method to display this values in your code after fetching the `Events` in your `Building` with a viewholder for the `RecyclerView` and a `WebView`:
```java
@Override
public void onClick(View view) {
    int adapterPosition = getAdapterPosition();
    if (mWebView.getVisibility() == (View.VISIBLE)){
        mWebView.setVisibility(View.GONE);
    }else{
        mWebView.setVisibility(View.VISIBLE);
    }

    mWebView.loadDataWithBaseURL(null, mEventData.get(adapterPosition).getHtml(), "text/html", "utf-8", null);

    mClickHandler.onClick("");

}
```

You can get more information about `Event` in the [SDK documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.26.2/) and check the full example in the [getbuildingevents](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/buildingevents) package.

## <a name="positionevents"></a> Calculate if the user is inside en event
In order to determine if the user is inside the trigger area of a `SitumEvent`, you should intersect every new location with the `trigger` area of every event in the building. 
This can be done by following the next example (Please note that minimun Android SDK version is 2.25.0):

```java
···
//This is a method of LocationListener, which provides information 
//about the user's location
@Override
public void onLocationChanged(@NonNull Location location) {
    SitumEvent event = getEventForLocation(location);
    if (event != null) {
        Log.d("Event", "User inside event: " + event.getName());
    }
}

···
private SitumEvent getEventForLocation(Location location) {
    for (SitumEvent event : buildingInfo.getEvents()) {
        if (isLocationInsideEvent(location, event)) {
            return event;
       }
   }
   return null;
}

private boolean isLocationInsideEvent(Location location, SitumEvent situmEvent) {
    if (!location.getFloorIdentifier()
    		.equals(String.valueOf(situmEvent.getFloor_id()))) {
        return false;
    }
   CartesianCoordinate eventCenter = situmEvent.getTrigger().getCenter().getCartesianCoordinate();

   return location.getCartesianCoordinate()
   		.distanceTo(eventCenter) <= situmEvent.getRadius();
}
```
You can check the complete sample in the [userinsideevent](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/userinsideevent) package.

## <a name="filterpois"></a> Filter Building's POIs
You can filter your `Building` `POIs` by adding to the `POI` a Key-Value pair. You can add the Key-Value pair in the [Dashboard](https://dashboard.situm.es) when creating or updating a `POI` in the last section of the form.
In order to get the `Building` `POIs`, you have to fetch your buildings and select the one you want to work with. After that fetch the `Building's` `POIs` with the `CommunicationManager()`
```java
SitumSdk.communicationManager().fetchIndoorPOIsFromBuilding(building, new Handler<Collection<Poi>>(){

                    @Override
                    public void onSuccess(Collection<Poi> pois) {
                        for(Poi poi : pois){
                            Log.i(TAG, "onSuccess: poi: " + poi);
                        }
                        poiList.clear();
                        poiList.addAll(pois);
                        showPoiDataView();
                        filteringAdapter.setSearchData(poiList);
                        pbSearchLoading.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Error error) {
                        Log.e(TAG, "onFailure: fetching pois: " + error);
                        showErrorMessage();
                    }
                });
```

With the `POIs` in an `ArrayList` you can now make your own function to get the `POIs` filtered by the Key and Value you want, here in the example we are providing a layout with a form to select the Key and Value you want:
```java
public void filter (String key, String value){

        List<Poi> poiListTemp = new ArrayList<>();
        Map<String, String> kVList;


        for(Poi p : poiList){
            kVList = p.getCustomFields();
            Log.d(TAG, kVList.toString());
            if(!kVList.isEmpty()) {
                if(!kVList.containsKey(key)){;
                    continue;
                }
                if(!kVList.containsValue(value)){
                    continue;
                }
                if (kVList.get(key).equals(value)) {
                    poiListTemp.add(p);
                }

            }
            else{
                continue;
            }
        }
        if(!poiListTemp.isEmpty()){
            filteringAdapter.setSearchData(poiListTemp);
        }else{
            showErrorMessage();
        }
}
```

If you want to know more about filtering `POIs` you can check the [SDK documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.26.2/). You can also see the full example in the [poifiltering](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/poifiltering) package.

## <a name="guideinstructions"></a> Instructions while going from one point to another
Situm SDK provides a way to show the indications while you are going from one point to another. Since we have already seen how to get your location and how to plan a route between two points, here we will talk only about how to get the indications. This is a two-steps-functionallity, first we have to tell the route we have planned to do and then update every time we move our position in the route.
  * The route planned:
  ```java
  ...
  navigationRequest = new NavigationRequest.Builder()
  	.route(bestRoute)
        .distanceToGoalThreshold(3d)
        .outsideRouteThreshold(50d)
        .build();

  startNavigation();
  ...
  void startNavigation(){

        Log.d(TAG, "startNavigation: ");
        SitumSdk.navigationManager().requestNavigationUpdates(navigationRequest, new NavigationListener() {
            @Override
            public void onDestinationReached() {
                Log.d(TAG, "onDestinationReached: ");
                mNavText.setText("Arrived");
                polyline.remove();
            }

            @Override
            public void onProgress(NavigationProgress navigationProgress) {
                Context context = getApplicationContext();
                Log.d(TAG, "onProgress: " + navigationProgress.getCurrentIndication().toText(context));
                mNavText.setText(navigationProgress.getCurrentIndication().toText(context));
            }

            @Override
            public void onUserOutsideRoute() {
                Log.d(TAG, "onUserOutsideRoute: ");
                mNavText.setText("Outside of the route");
            }
        });
    }

  ```

  * Updating your position in the route:
  ```java
  private void startLocation(){
        if(locationManager.isRunning()){
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
	    
	    	...
	    	
	    	SitumSdk.navigationManager().updateWithLocation(current);
	    
	    	...

  ```

If you want to know more about the indications, you can check the [SDK Documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.26.2/). If you want to see the full example you can check the [guideinstructions](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/guideinstructions) package.

## <a name="animateposition"></a>Animate position while walking
Sometimes the difference between a good work and a nice one relies on the little things. Here we want to show you how to animate the arrow position while walking, this feature will make the user experience much better with just a few changes, lets dive in!
First we are going to optimize the positioning not creating a point everytime we update the location but changing the position of the marker.
```java 
		public void onLocationChanged(@NonNull Location location) {
                        current = location;
                        LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                                location.getCoordinate().getLongitude());
                        if (prev == null){
                            Bitmap bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.position);
                            Bitmap arrowScaled = Bitmap.createScaledBitmap(bitmapArrow, bitmapArrow.getWidth() / 4,bitmapArrow.getHeight() / 4, false);
        
                            prev = map.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .zIndex(100)
                                    .anchor(0.5f,0.5f)
                                    .icon(BitmapDescriptorFactory.fromBitmap(arrowScaled)));
                        }
```
The second step is to animate the arrow everytime we walk. For this purpose we can use the ObjectAnimator class provided by android. We will need to create an UpdateListener in order to make the transition smoother everytime the position changes:
```java
			locationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    LatLng startLatLng = lastLatLng;

                    @Override
                    public synchronized void onAnimationUpdate(ValueAnimator animation) {
                        float t = animation.getAnimatedFraction();
                        lastLatLng = interpolateLatLng(t, startLatLng, toLatLng);

                        marker.setPosition(lastLatLng);
                    }
                }); 
```
Finally just set the animation duration and that is all!
```java 
				locationAnimator.setDuration(DURATION_POSITION_ANIMATION);
                locationAnimator.start();
```

Once we have the animation for the position, we will see that it works much smoother than before, but we are missing something, the bearing. In order to make the arrow show us our direction, we will need to implement a new animation for this purpose. This is almost the same as the animation we did before. Everytime we update our position we get a message from our SDK with our exact position ¡and bearing!. Here we will have to deal with the angles in order to rotate always in the shortest way, after that just create another animation:
```java
				locationBearingAnimator = new ObjectAnimator();
                locationBearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public synchronized void onAnimationUpdate(ValueAnimator animation) {
                        lastBearing = (Float) animation.getAnimatedValue();
                        marker.setRotation(lastBearing);
                    }
                });
                locationBearingAnimator.setFloatValues(lastBearing, toBearing);
                locationBearingAnimator.setDuration(DURATION_BEARING_ANIMATION);
                locationBearingAnimator.start();

``` 

For a better and more fluid results, you have to set the `useDeadReckoning` option to true when starting the positioning as follows:
```java
		private void startLocation(){
			...
			LocationRequest locationRequest = new LocationRequest.Builder()
                .buildingIdentifier(buildingId)
                .useDeadReckoning(true)
                .build();
			...
		}
	
```
This option allows you to get fast position updates using only the inertial sensors (compass, gyro...) without a decrease in the battery duration.


If you want to know more about location you can check the [SDK Documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.26.2/) and, if you want to see the full code example check the [animateposition](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/animateposition) package. 

## <a name="moreinfo"></a> More information

More info is available at our [Developers Page](https://des.situm.es/developers/pages/android/).

## <a name="supportinfo"></a> Support information

For any question or bug report, please send an email to [support@situm.es](mailto:support@situm.es)
