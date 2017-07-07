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
        circle = googleMap.addCircle(new CircleOptions()
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






## <a name="moreinfo"></a> More information

More info is available at our [Developers Page](https://des.situm.es/developers/pages/android/).
For any other question, contact us in https://situm.es/contact.
