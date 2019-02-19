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

Aside from animate the position it is also interesting to animate the camera so the user is always seeing where he is. To do this you just need to animate the camera when the SDK returns a location.
```java
        float bearing = (location.hasBearing()) && location.isIndoor() ? (float) (location.getBearing().degrees()) : map.getCameraPosition().bearing;

        LatLng latLng = new LatLng(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());

        //Skip if no change in location and little bearing change
        boolean skipAnimation = lastCameraLatLng != null && lastCameraLatLng.equals(latLng)
                && (Math.abs(bearing - lastCameraBearing)) < MIN_CHANGE_IN_BEARING_TO_ANIMATE_CAMERA;
        lastCameraLatLng = latLng;
        lastCameraBearing = bearing;
        CameraPosition cameraPosition = new CameraPosition.Builder(map.getCameraPosition())
                .target(latLng)
                .bearing(bearing)
                .tilt(40)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), UPDATE_LOCATION_ANIMATION_TIME, null);

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


If you want to know more about location you can check the [SDK Documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.31.3/).
