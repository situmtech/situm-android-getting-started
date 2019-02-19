## <a name="positionevents"></a> Calculate if the user is inside en event
In order to determine if the user is inside the trigger area of a `SitumEvent`, you should intersect every new location with the `trigger` area of every event in the building.
This can be done by following the next example (Please note that minimun Android SDK version is 2.25.0):

```java
···
//This is a method of LocationListener, which provides information
//about the user's location
@Override
public void onLocationChanged(@NonNull Location location) {
    SitumEvent event = getEventForLocation(location);
    if (event != null) {
        Log.d("Event", "User inside event: " + event.getName());
    }
}

···
private SitumEvent getEventForLocation(Location location) {
    for (SitumEvent event : buildingInfo.getEvents()) {
        if (isLocationInsideEvent(location, event)) {
            return event;
       }
   }
   return null;
}

private boolean isLocationInsideEvent(Location location, SitumEvent situmEvent) {
    if (!location.getFloorIdentifier()
    		.equals(String.valueOf(situmEvent.getFloor_id()))) {
        return false;
    }
   CartesianCoordinate eventCenter = situmEvent.getTrigger().getCenter().getCartesianCoordinate();

   return location.getCartesianCoordinate()
   		.distanceTo(eventCenter) <= situmEvent.getRadius();
}
```
