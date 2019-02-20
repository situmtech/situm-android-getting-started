## <a name="positionevents"></a> Calculate if the user is inside en event
In order to determine if the user is inside the trigger area of a `SitumEvent`, you should intersect every new location with the `trigger` area of every event in the building.

You can see how to do this in `UserInsideEventActivity.java` (Please note that minimun Android SDK version is 2.25.0):

