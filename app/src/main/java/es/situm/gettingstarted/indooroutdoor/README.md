## <a name="indoor-outdoor-positioning"><a/> Indoor-outdoor positioning

Situm SDK can also work in hybrid mode, providing the indoor location when the smartphone is inside a building that has the Situm's technology configured, and the Google Play services location otherwise. In addition, the Situm SDK handles the indoor-outdoor transition seamlessly.

In order to enable the positioning mode to operate in indoor-outdoor mode it is mandatory to use the LocationManager without indicating a specific building.

In Android 9 the location does not have modes, is just enabled or disabled, but before Android 9 there were three location modes: High accuracy, Battery saving and device only.
To use the indoor-outdoor location the user must set the location mode to High Accuracy. Otherwise the location won't start if the device can't get an outdoor position.

- The High Accuracy mode uses GPS, Wi-Fi, Bluetooth and mobile networks to get the location. This mode will return the most accurate location possible
- The Battery Saving mode uses Wi-Fi, Bluetooth and mobile networks. It does not use GPS so the positioning won't work because the indoor-outdoor mode needs the GPS signal to be available.
- The Device Only mode uses the GPS, so the positioning will not start if there is no GPS signal.

You can know more about the location modes [here](https://developer.android.com/training/location/change-location-settings#location-request)

1. Build a `LocationRequest` without indicating the `Building` id. The `LocationRequest` can be configured
   with many more options, but this is outside of the scope of this example. Check the
   [Javadoc](http://developers.situm.es/sdk_documentation/android/javadoc/latest) for more information.
2. Build a `LocationListener` in order to receive location updates.
3. After the creation of the required objects, request location updates to the `LocationManager`.

Also, do not forget to stop the service in the `onStop` or any other method you might consider for this purpose.

Remember: location permissions are needed to run this example.
