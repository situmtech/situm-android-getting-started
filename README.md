Situm Android SDK Sample app 
=======================

## Table of contents

[Introduction](#introduction)

[Step 1: Configure our SDK in your Android project](#configureproject)

[Step 2: Get an instance of SitumDataManager](#situmdatamanager)

[Step 3: Download your buildings](#download_buildings)

[Step 4: Download building data](#download_data)

[Step 5: Activate the positioning](#positioning)

[Android 6.0 compilation](#android6)

[More information](#moreinfo)


### Introduction

This is a sample Android applicacion built with Situm SDK. Situm SDK is a set of utilitites that allow any developer to build location based apps using Situm's indoor positioning system. Among many other capabilities, apps developed with Situm SDK will be able to:

1. Obtain information related to buildings where Situm's positioning system is already configured: floorplans, points of interest, geotriggered events, etc.
2. Retrieve the location of the smartphone inside these buildings (position, orientation, and floor where the smartphone is).
3. Compute a route from a point A (e.g. where the smartphone is) to a point B (e.g. any point of interest within the building).
4. Trigger notifications when the user enters a certain area.

In this tutorial, we will guide you step by step to set up your first Android application using Situm SDK. Before starting to write code, we recommend you to set up an account in our Dashboard (https://dashboard.situm.es), retrieve your APIKEY and configure your first building.

1. Go to the [sign in form](http://dashboard.situm.es/accounts/register) and enter your username and password to sign in. 
2. Go to the [apps section](http://dashboard.situm.es/accounts/users/apps) and click on "Are you a developer?" to generate your APIKEY and download the full SDKs and its documentation, including its javadoc.
3. Go to the [buildings section](http://dashboard.situm.es/buildings) and create your first building.
4. Download [SitumMaps](https://play.google.com/store/apps/details?id=es.situm.maps). With this application, you will be able to configure and test Situm's indoor positioning system in your buildings.

Perfect! Now you are ready to develop your first indoor positioning application.

### <a name="configureproject"></a> Step 1: Configure our SDK in your Android project

First of all, you must configure Situm SDK in your Android project. This has been already done for you in the sample application, but nonetheless we will walk you to the process.

* Add *SitumSDK.jar* file to *app/libs* folder. Note that the sample app already includes the lastest version of this jar file. However, you can also download it from the [apps section](http://dashboard.situm.es/accounts/users/apps) of the Dashboard, as explained in the previous section.
* Inside Gradle Scripts, in the *build.gradle* (Module:app), import the following library into the section "dependencies":

```
compile files('libs/SitumSDK.jar')
```

* Add Situm SDK dependencies into the same section of the *build.gradle* file. 

```
compile 'org.altbeacon:android-beacon-library:2.1.4'
compile 'es.usc.citius.hipster:hipster-core:1.0.0-rc2'
compile 'com.loopj.android:android-async-http:1.4.9'
```

We recommend you to re-synchronize the project at this point.

*  Import the SitumSDK service by adding the following line within the section *<application></application>* of the AndroidManifest file (*main/AndroidManifest.xml*):

```
<service android:name="es.situm.sdk.v1.SitumService" android:exported="false"/>
```

* Grant the app access permissions to sensors and other services to guarantee the correct behaviour of Situm SDK. This also requires adding the following access permissions to *AndroidManifest*:

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### <a name="situmdatamanager"></a> Step 2: Get an instance of SitumDataManager

Now that you have correctly configured your Android project, you can start writting your application's code. In this sample project, all this code has been included in the file [SitumMainActivity.java] (https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/SitumMainActivity.java)

First of all, you must configure your SitumDataManager class, which will give you access to your building's data (list of buildings, floorplans, points of interest, etc.). There are two ways of doing this:

##### Option 1: Using your email address and APIKEY.

This is the recommended option and the one that we have implemented in this project. To do so, include the following code in your application (e.g. in your main activity):

```        
SitumDataManager dataManager = SitumLogin.login("test@situm.es","api_key");
```
Remember to add the following dependencies in the same file:

```
import es.situm.sdk.v1.SitumDataManager;
import es.situm.sdk.v1.SitumLogin;
```

##### Option 2: Using your email address and password. 

To do so, we should pass the email and password to the *SitumLogin* object, and implement a *SitumLoginResponseHandler* callback to receive a valid instance of *SitumDataManager*. 

```
SitumLogin.login("user_email","password", new SitumLoginResponseHandler() {
	@Override
        public void onLogin(SitumDataManager situmDataManager) {
        }
        
	@Override
	public void onWrongLogin() {
	}
	
	@Override
	public void onConnectionError() {
	}
});
```


This mechanism has not been implemented in the sample app. For more information, please refer to the javadocs in the [apps section](http://dashboard.situm.es/accounts/users/apps).

###<a name="download_buildings"></a> Step 3: Download your buildings

At this point, we should be able to retrieve the list of buildings associated with our user's account. To do so, include the following code snippet, that will also receive an error callback in case the retrieve operation fails.

```
dataManager.fetchBuildings(new SitumResponseHandler() {
    @Override
    public void onListReceived(List list) {
        // received buildings
	final List<SitumBuilding> buildings = new ArrayList<>(list);
	for (SitumBuilding building : buildings) {
		Log.i(TAG, "Received building" + building.getName());
	}
    }
    
    @Override
    public void onErrorReceived(int i, Header[] h, byte[] bytes, Throwable t) {
    // manage errors
    	Log.e(TAG, "Error receiving buildings");   
    }
}); 	

```

Again, remember to add the corresponding dependencies: 

```
import android.preference.PreferenceActivity;
import java.util.List;
import org.apache.http.Header;
import es.situm.sdk.v1.SitumBuilding;
```



### <a name="download_data"> Step 4: Download building data

Once we have the buildings, it is straightforward to get their information. For instance, in order to obtain all the floors of a building, we just have to select the required building:

```
selectedBuilding = buildings.get(7);
```

and call *SitumDataManager* to fetch its levels:

```
dataManager.fetchLevelsForBuilding(selectedBuilding, new SitumResponseHandler() {
  	@Override
        public void onListReceived(List list) {
	       	// received levels for  selectedBuilding
	        List<SitumLevel> levels = new ArrayList<>(list);
	        Log.i(TAG, String.format("Received %s levels for %s", levels.size(),
	        selectedBuilding.getName()));
        }
    
 	@Override
    	public void onErrorReceived(int i, Header[] h, byte[] bytes, Throwable t) {
              // manage errors
        }
});
```

NOTE: This code has to be included into the function *onListReceived()* of the call to the fetchBuildings method (see Step 3). 


In addition, we have to add the following import:

```
import es.situm.sdk.v1.SitumLevel;
```

As we can see, all the petitions are very similar, and remain being so for the other resources (events, points of interest, floorplans, etc.). In  [SitumMainActivity.java](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/SitumMainActivity.java) we also show how to dowload the image floorplan of a floor. 

### <a name="positioning"></a>Step 5: Activate the positioning

The last step is to initiate the indoor positioning on a certain building. This will allow the app to retrieve the location of the smartphone within this building. As in the previous case,  this code has to be included into the function *onListReceived()* of the call to the *fetchBuildings* method.

First of all, create an instance of SitumIPSManager:
```
SitumIPSManager ipsManager = new SitumIPSManager(getApplicationContext());
```

Then, pass a callback that will inform us of possible errors that may happen.
```
ipsManager.setSensorErrorListener(new SitumSensorErrorListener() {
    	@Override
    	public void onError(SitumError situmError) {
		//Manage error
		Log.e(TAG, situmError.name);
		txtLocation.setText(situmError.name);   
     	}
});

```

Initiate the positioning on a specific building:
```
ipsManager.start(selectedBuilding, true, true, true);
ipsManager.setPoseReceiver(new SitumPoseReceiver() {
	@Override
   	public void onPoseReceived(SitumLocation situmLocation) {
		Log.i(TAG, String.format("x %s y %s", situmLocation.x, situmLocation.y));
	      	txtLocation.setText(String.format("x %s y %s", situmLocation.x,  situmLocation.y));
	}
});

```
Again, remember to add the required imports:

```
import es.situm.sdk.v1.SitumError;
import es.situm.sdk.v1.SitumIPSManager;
import es.situm.sdk.v1.SitumSensorErrorListener;
```

## <a name="android6"></a>Android 6.0 compilation

Due to changes in Android 6.0, the library support to the Apache HTTP client, used by Situm SDK, has been deleted. To allow our SDK to use Apache HTTP API and maintain the compatibility with Android 6.0, we have to declare the following dependences into the file *build.gradle* (Module: App):	

```
useLibrary	'org.apache.http.legacy'
```
	
In addition, we have to add the dependencies to a target of *Android 23*:
```
compile	'com.android.support:appcompat-v7:23.1.1'
```

We also recommend you to ask the user for permision in order to have access to its location, because Android 6.0 does not allow WiFi scanning without this permission:

```
int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        startLocation();
} else {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
     	        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_RESULT);
	}
}
```


Then, you can check the result of this action by overwriting the method:
```
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
```


## <a name="moreinfo"></a> More information

Go to the developers section of the dashboard and download the full documentation of the SDK, including the javadoc with all the available functionalities. 


For any other question, contact us by mail at situm@situm.es or send us your comments and suggestions to our website www.en.situm.es
