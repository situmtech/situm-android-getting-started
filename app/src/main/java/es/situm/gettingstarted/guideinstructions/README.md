## <a name="guideinstructions"></a> Instructions while going from one point to another
Situm SDK provides a way to show the indications while you are going from one point to another. Since we have already seen how to get your location and how to plan a route between two points, here we will talk only about how to get the indications. This is a two-steps-functionallity, first we have to tell the route we have planned to do and then update every time we move our position in the route.

**This example only allows navigation on the floor where you are positioned.**

In the method `startNavigation` of the class `GuideInstructionsActivity` you can se how to do it.

And don't forget to update the location to your NavigationManager calling the method `SitumSdk.navigationManager().updateWithLocation(currentLocation)` every time the SDK returns a location.

If you want to know more about the indications, you can check the [SDK Documentation](http://developers.situm.es/sdk_documentation/android/javadoc/latest).
