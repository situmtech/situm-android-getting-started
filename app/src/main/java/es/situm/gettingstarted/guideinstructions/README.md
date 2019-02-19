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

If you want to know more about the indications, you can check the [SDK Documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.31.3/).
