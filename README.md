Situm Android SDK Sample app
=======================

This is a sample Android application built with the Situm SDK. With the sample app you will be able 
ablle to: 
  1. List all the buildings of your account and start the positioning for the selected building showing 
 the first floor image of the building and the locations received.
  2. Draw the buildings floor over the Google map view.

# Table of contents
#### [Introduction](#introduction)
#### [Setup](#configureproject)
1. [Step 1: Configure our SDK in your Android project](#configureproject)
2. [Step 2: Initialize the SDK](#init)
3. [Step 3: Set your credentials](#config)
4. [Step 4: Setup Google maps](#mapsapikey)
5. [Optional step 5: location and runtime permissions](#locationpermissions)

#### [Samples](#samples)

1. [Get buildings information](#communicationmanager)
2. [Start the positioning](#positioning)
3. [Indoor-Outdoor positioning](#indoor-outdoor-positioning)
4. [Draw the buildings floor over the Google maps](#drawbuilding)
5. [Draw current position over Google maps](#drawposition)
6. [Draw pois over Google maps](#drawpois)
7. [Draw route over Google maps](#drawroute)
8. [Draw realtime devices over Google maps](#rt)

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
you in the sample application, but nonetheless we will walk you to the process.

* Add the next line to the project *build.gradle* in the *repositories* section of *allprojects* to
add our maven repository to the project:

```groovy
maven { url "https://repo.situm.es/artifactory/libs-release-local" }
```

* Then add the Situm SDK library dependency into the section *dependencies* of the app *build.gradle*.
It's important to add the `transitive = true` property to download the Situm SDK dependencies.

```groovy
    compile ('es.situm:situm-sdk:2.6.0@aar') {
        transitive = true
    }
```

### <a name="init"></a> Step 2: Initialize the SDK

You must initialize the sdk in the `onCreate()` method of your Application:

```java
SitumSdk.init(this);
```

### <a name="config"></a> Step 3: Set your credentials

There are two ways to set the credentials, in the `AndroidManifest.xml` file or programmatically.

##### Option 1: `AndroidManifest.xml` file

You can set the credentials (user and API key)in the `AndroidManifest.xml` file adding the next `meta-data` fields:

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




### Step 4: Setup Google maps <a name="mapsapikey"><a/>
This step is only necessary if you want to run the sample that draws the buildings floor over the 
Google map, otherwise you can skip it and continue with the [Samples](#samples).

First of all, you need to add Google services dependency to the project, if you need more info: 
[Setup Google Play Services](https://developers.google.com/android/guides/setup). To do this paste the dependency in 
your module build.gradle as usual:
```
compile 'com.google.android.gms:play-services-maps:10.0.1'
```
Add in the app manifest the version of the Google Play Services that we have imported. To do this paste
the next snippet inside application tag:
```
<meta-data
    android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />
```

An api key is needed to add Google maps services and use it in an application, if you need more info 
you can visit the official documentation in [Google maps info](https://developers.google.com/maps/documentation/android-api/intro). 
To obtain a Google api key please refer to 
[Google maps obtain api key](https://developers.google.com/maps/documentation/android-api/signup), 
then go back to your app module file build.gradle and replace 'gmaps_api_key' with the value of the 
obtained key from Google.
```
resValue 'string', 'google_maps_key', "YOUR_API_KEY"
```




### Optional step 5: location and runtime permissions <a name="locationpermissions"><a/>
When we work on features that envolves location we need to add fine location permission to the manifest:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```
And ensure to check the Android Runtime permissions. [More info](https://developer.android.com/training/permissions/requesting.html) 





## Samples <a name="samples"></a>
### <a name="communicationmanager"></a> Get buildings information

Now that you have correctly configured your Android project, you can start writing your application's code. 
In this sample project, all this code has been included in the file 
[PositioningActivity.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/positioning/PositioningActivity.java)

First of all, you can get and instance of the `CommunicationManager` with `SitumSdk.communicationManager()`.
With this object you can fetch your building's data (list of buildings, floorplans, points of interest, etc.):

In the next example we fetch all the buildings associated with our user's account and print them to the Logcat:

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

### <a name="positioning"></a> Start the positioning

To start the positioning we need a building so, first you will need to obtain a building,
You can check the code to obtain a building in [Get buildings information](#communicationmanager).

Start positioning will allow the app to retrieve the location of the smartphone within this building.
To do this you need to create a location request indicating the building where you want to start
 the positioning:

```java
LocationRequest locationRequest = new LocationRequest.Builder()
        .buildingIdentifier(selectedBuilding.getIdentifier())
        .build();
```

Then, you need to implement the listener in where you will receive the location updates, status and errors.

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

In `onLocationChanged(Location)` you will be updated with the locations. This `Location` object contains
the building identifier, level identifier, cartesian coordinates, geographic coordinates, orientation,
accuracy, among others.

In `onStatusChanged(int)` you will received the changes in the status of the system: starting, initializing,
user not in building, compass need to be calibrated, etc.

In `onError(Error)` you will received the errors produced, the positioning will stop. 

From API 23 you need to ask for the location permissions at runtime, if the location permission is not 
granted, and error with code `LocationErrorConstant.Code.MISSING_LOCATION_PERMISSION` will be received.
Also, the location must be enabled in order to scan Wifi and BLE, an error with code `LocationErrorConstant.Code.LOCATION_DISABLED`
will be received in this case. In the example you can see how to manage this errors.

Finally, you start the positioning with:

```java
SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
```




## Indoor-Outdoor positioning <a name="indoor-outdoor-positioning"><a/>
To enable the positioning mode to operate both indoor and outdoor its mandatory to use the LocationManager
without indicating a specific building.

*Its mandatory to config [Optional step 5: location and runtime permissions](#locationpermissions).


1. First of all, build a LocationRequest without indicating the building id. This can be configured
with much more options, but is outside of the scope of this example. Check the 
[Javadoc](http://developers.situm.es/pages/android/api_documentation.html) for more information.
2. To receive location updates build a LocationListener.
3. After the creation of the needed objects, request location updates to the LocationManager.

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


And dont forget to stop the service in the onDestroy or any method you consider.
```java
SitumSdk.locationManager().removeUpdates(locationListener);
```

You can check the complete sample in [indooroutdoor package](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/indooroutdoor)





## Draw the buildings floor over the Google maps <a name="drawbuilding"><a/>
Drawing the floor of a building will allow us to see the floor plan.

Before this you will need to complete the [Setup Google maps](mapsapikey). Once this is done we must
need to obtain the floors of the target building, there is a sample in 
[Obtaining building floors](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/drawbuilding/GetBuildingImageUseCase.java).
When we have fetch the floors we need to choose a floor and get the bitmap of this floor through the
Situm CommunicationMananger.
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

Once we have the Bitmap of the building floor we will draw it in Google map.
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
You can check the complete sample in [drawbuilding package](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawbuilding)





## Draw current position over Google maps<a name="drawposition"><a/>
This functionality allow us to see the device current position over a Google map.

*Its mandatory to config [Optional step 5: location and runtime permissions](#locationpermissions).


1. First, we need to build a LocationRequest.
2. To receive location updates build a LocationListener.
3. After the creation of the needed objects, request location updates to the LocationManager.
4. In the listener callback method onLocationChanged, draw the circle that represents the device
position.

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

And dont forget to stop the service in the onDestroy or any method you consider.
```java
SitumSdk.locationManager().removeUpdates(locationListener);
```

You can check the complete sample in [drawposition package](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawposition)




## Draw pois over Google maps <a name="drawpois"><a/>
This funcionality allows to draw a list of points of interest that belongs to a building over a 
google map.

First of all we need the target building, then we must query to the communications manager to fetch 
the pois.
In this example we use the first building that arrives from the communications manager.
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

Finally draw the pois over the map.
```java
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

You can check the complete sample in [drawpois package](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawpois)




## Draw route over Google maps <a name="drawroute"><a/>
This funcionality allow us to draw a route between two points inside a building. 

First of all we need to get a building and its POIs(To run this example at least is needed a 
building with two POIs configured to make a route).
In this example we are going to use the first building and the two first POIs returned by the 
communication manager. To get this information you can use the 
[Draw pois over Google maps](#drawpois) example.

After obtaining the basic information, we are going to make a request to the directions manager.
To create a request its mandatory two indoor points that we got from the previous call to communication
manager. The directions manager will respond us through the callback. 
When we have the data processed we will draw a Google maps polyline to represent the route.
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

Extra: also we can add navigation between the points.
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
Dont forget to stop navigation when destroy activity/fragment or whenever you consider.

You can check the complete sample in [drawroute package](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawroute)




## Draw realtime devices over Google maps <a name="rt"><a/>
This functionally allow us to get the current devices that are positioning inside a building in 
real time.

In order to run this example its mandatory to get a building first. Then we will can query realtime 
manager to obtain devices inside this building.

Obtain  list of buildings and pick one. In this example we get the first returned building. Then we 
will invoke realtime method.
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


The next step is obtain the realtime manager and invoke real time updates. This method needs 
as parameter a request that contains the target building and the time between querys and a listener 
that will respond with the current devices inside the building.
In the response we are going to place/remove markers and animate the camera between to the bounds of them.
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

Dont forget to remove real time updates when destroy activity/fragment or whenever you consider.

You can check the complete sample in [realtime package](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/realtime)




## <a name="moreinfo"></a> More information

More info is available at our [Developers Page](https://des.situm.es/developers/pages/android/).
For any other question, contact us in https://situm.es/contact.
