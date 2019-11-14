Situm Android SDK Sample app
=======================
  
# Table of contents
#### [Introduction](#introduction)
#### [Setup](#configureproject)
1. [Step 1: Configure our SDK in your Android project](#configureproject)
2. [Step 2: Initialize the SDK](#init)
3. [Step 3: Set your credentials](#config)
4. [Step 4: Setup Google Maps](#mapsapikey)
5. [Optional step 5: location and runtime permissions](#locationpermissions)
6. [Optional step 6: Setup indoor positioning](#indoorpositioning)

#### Samples

1. [Positioning](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/positioning):
Download the buildings in your account and how to start the positioning in a building.
2. [Indoor-Outdoor](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/indooroutdoor):
Use the indoor-outdoor positioning.
3. [Draw building](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawbuilding):
Draw the floorplan of a building over a map.
4. [Draw position](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawposition):
Draw the position you obtain from the SDK in the map.
5. [Draw pois](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawpois):
Draw the pois of a building over the map
6. [Draw route](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawroute):
Draw a route between to points over the map
7. [Show realtime](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/realtime):
Draw the users that are position inside a building over a map.
8. [Building events](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/buildingevents):
Get the events of a building.
9. [User inside event](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/userinsideevent):
Calculate if the user is inside a event.
10. [Poi filtering](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/poifiltering):
Filter the pois with a especific key-value.
11. [Guide instructions](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/guideinstructions):
Give indications when you are going to a point.
12. [Animate position](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/animateposition):
Animate the position and the camera.
13. [Point inside geofence](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/pointinsidegeofence):
Draw geofences and calculate if a point is inside them.
14. [Update location parameters](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/updatelocationparams):
Update the parameters of the location on the fly.

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
    implementation ('es.situm:situm-sdk:2.46.2@aar') {
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

## <a name="moreinfo"></a> More information

More info is available at our [Developers Page](https://des.situm.es/developers/pages/android/).

## <a name="supportinfo"></a> Support information

For any question or bug report, please send an email to [support@situm.es](mailto:support@situm.es)
