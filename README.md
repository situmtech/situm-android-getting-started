situm-android-getting-started
=======================


### Introduction

This is a sample Android applicacion built with Situm SDK. Situm SDK is a set of utilitites that allow any developer to build location based apps that use Situm's indoor positioning system. Among many other capabilities, apps developed with Situm SDK will be able to:

1. Obtain information related to buildings where Situm's positioning system is already configured: floorplans, points of interest, geotriggered events, etc.
2. Retrieve the location of the smartphone inside these buildings (position, orientation, and floor where the smartphone is).
3. Compute a route from a point A (e.g. where the smartphone is) to a point B (e.g. any point of interest within the building).
4. Trigger notifications when the user enters a certain area.

In this tutorial, we will guide you step by step to set up your first Android application using Situm SDK. Before starting to write code, we recommend you set up an account in our Dashboard (https://dashboard.situm.es), retrieve your APIKEY and configure your first building.

1. Go to http://dashboard.situm.es/accounts/register and enter your username and password to sign in. 
2. Go to http://dashboard.situm.es/accounts/users/apps and click on "Are you a developer?" to generate your APIKEY and download the full SDK documentation, including its javadoc.
3. Go to http://dashboard.situm.es/buildings and create your first building.
4. Download SitumMaps at https://play.google.com/store/apps/details?id=es.situm.maps. With this application, you will be able to configure and test Situm's indoor positioning system in your buildings.



### Step 1: Configure Situm SDK in the project

First of all, you must configure Situm SDK in your Android project. This has been already done for you in the sample application, but nonetheless we will walk you to the process.

1. Add *SitumSDK.jar* file to *app/libs* folder.  
2. Inside Gradle Scripts, in the build.gradle (Module:app), import the following library into the section "dependencies":

```
compile files('libs/SitumSDK.jar')
```

3. Add SitumSDK dependencies into the same section of the build.gradle file. 

```
compile 'org.altbeacon:android-beacon-library:2.1.4'
compile 'es.usc.citius.hipster:hipster-core:1.0.0-rc2'
compile 'com.loopj.android:android-async-http:1.4.9'
```

We recommend you to re-synchronize the project after adding this.

4. Import the SitumSDK service by adding the following line within the section <application></application> of the AndroidManifest file (main/AndroidManifest.xml):

```
<service android:name="es.situm.sdk.v1.SitumService" android:exported="false"/>
```

5. Grant the app access permissions to sensors and other services to guarantee the correct behaviour of Situm SDK. This also requires adding the following access permissions to AndroidManifest:

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

###Step 2: Get an instance of SitumDataManager

Now that you have correctly configured your Android project, you can start writting your application's code. To do so, the first step is to configure your SitumDataManager class, which will give you access to your building's data. There are two ways of doing this:

#### Option 1: Using your email address and APIKEY.

```        
SitumDataManager dataManager = SitumLogin.login("test@situm.es","api_key");
```
Remember to add the following dependencies in the same file:

```
import es.situm.sdk.v1.SitumDataManager;
import es.situm.sdk.v1.SitumLogin;
```

#### Option 1: Using your email address and password. 

In this case we will receive a valid instance of SitumDataManager into the callback SitumLoginResponseHandler. You can consult the attached javadocs for more information.
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
Once we have the SitumDataManager we can ask the server for data: buildings, images, etc.

Step 3: Download building data
In this step is shown a sample of how to request the list of available buildings to a user account.
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
We have to add the following lines to the head for the correct functioning of this instructions:
import android.preference.PreferenceActivity;
import java.util.List;
import org.apache.http.Header;

import es.situm.sdk.v1.SitumBuilding;
Through this call we will have a list of the buildings that the server is giving back, or an error, if that were the case. 

Step 4: Request buildings data
Once we have the buildings is very simple to get other information of the building. This example shows how to obtain all the floors. We just have to select the required building and make a call like the following one:
selectedBuilding = buildings.get(7);

dataManager.fetchLevelsForBuilding(selectedBuilding, new SitumResponseHandler() {
  	   @Override
          public void onListReceived(List list) {
       	       // received levels for  selectedBuilding
            List<SitumLevel> levels = new ArrayList<>(list);
            Log.i(TAG, String.format("Received %s levels for %s", levels.size(),
            selectedBuilding.getName()));
    
                   }
    
 	@Override
    	   Public void onErrorReceived(int i, Header[] h, byte[] bytes, Throwable t) {
         
              // manage errors
          }
});
ATTENTION: This code has to be included into the function onListReceived() of the call to the fetchBuildings method. 
In addition, we have to add the appropriate class of import to the head:
import es.situm.sdk.v1.SitumLevel;
As we can see, petitions are very similar, and remain being so for the other resources offered, like events, level images, etc... In the attached app GettingStartedSitum we can see a sample downloading the image of a selected floor.

Step 5: Activating the positioning
Initiate the positioning on a building. As in the previous case this code has to be included into the function onListReceived() of the call to the fetchBuildings method.
1. We create an instance of SitumIPSManager,:
	SitumIPSManager ipsManager = new SitumIPSManager(getApplicationContext());
2. We pass a callback that will inform us of possible errors that may happen.
ipsManager.setSensorErrorListener(new SitumSensorErrorListener() {
    	    @Override
    	    public void onError(SitumError situmError) {
//Manage error
	Log.e(TAG, situmError.name);
	txtLocation.setText(situmError.name);   
    
     }
});
3.  We initiate positioning on a specific building:
ipsManager.start(selectedBuilding, true, true, true);
ipsManager.setPoseReceiver(new SitumPoseReceiver() {
    @Override
    	    public void onPoseReceived(SitumLocation situmLocation) {
	      Log.i(TAG, String.format("x %s y %s", situmLocation.x, situmLocation.y));
	      txtLocation.setText(String.format("x %s y %s", situmLocation.x,  situmLocation.y));
            
}
});
We have to add the import of the class used into the head for the correct functioning of this instructions:
import es.situm.sdk.v1.SitumError;
import es.situm.sdk.v1.SitumIPSManager;
import es.situm.sdk.v1.SitumSensorErrorListener;

Android 6.0 compilation
Due to changes made to Android 6.0, the library support to Apache HTTP client has been deleted. To continue using the Apache HTTP API, we have to declare the following dependences into the file build.gradle (Module: App):	
	useLibrary	'org.apache.http.legacy'
In addition, we have to add the dependencies to a target of android 23:
compile	'com.android.support:appcompat-v7:23.1.1'
We also recommend you to ask the user for permision in order to have access to its location, because Android 6.0 does not allow WiFi scanning without this permission:
int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
           startLocation();
       } else {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
     requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_RESULT);
           }
       }

Then, you can check the result of this action by overwriting the method:
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)

More information
The attached javadoc shows in detail all the available functionalities in Situm SDK.
For any other question, contact us by mail or send us your comments and suggestions to our website www.situmtechnologies.com
