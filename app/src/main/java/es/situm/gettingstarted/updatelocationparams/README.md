## <a name="updatelocationparams"></a> Update the parameters of the location on the fly

When you are following the indications of a route to get to a spot, the locations update indicator might zig zag and not be shown right over the path. When the `LocationRequest` receives the route updated, this behaviour can be fixed. Keep in mind that you can set the limit when location update is considered out of the route with the method `NavigationRequest.Builder().outsideRouteThreshold`.

**This example only allows navigation on the floor where you are positioned.**

In the methods `updateLocationParams` and `stopUpdateParams` of the class `UpdateLocationParamsActivity` you can see how to do it.

In order to get the method working properly you have to get the points of the Route with `Route.points` and set the `routeId` so that it is increased every time the params are updated.

If you want to know more about it, you can check the interface [LocationManager](http://developers.situm.es/sdk_documentation/android/javadoc/latest/es/situm/sdk/location/LocationManager.html) which is part of the Situm's SDK.