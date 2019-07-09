## Draw geofences and calculate if a point is inside them

To calculate this we are gonna be using the JTS library. To add this you just need to put this in your build.gradle file.
```groovy
    implementation 'org.locationtech.jts:jts-core:1.16.0'
```
You can create polygons with the points a geofence and intersect this polygons with the locations to know if its inside.