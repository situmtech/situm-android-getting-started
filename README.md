<div style="text-align:center">

# Situm Android SDK Sample app
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

</div>
<div style="float:right; margin-left: 1rem;">

[![](https://situm.com/wp-content/themes/situm/img/logo-situm.svg)](https://www.situm.es)
</div>

[Situm SDK](https://situm.com/docs/01-introduction/) is a set of utilities that allow any developer to build location based apps using Situm's indoor positioning system. 

This project contains an app to test some basic examples using this SDK, so that you can get an idea of what you can achieve with [Situm's tecnology](https://situm.com/en/).

---
## Table of contents
1. [How to run this app](#how-to-run-this-app)
2. [Examples](#examples)
3. [Submitting contributions](#submitting-contributions)
4. [License](#license)
5. [More information](#more-information)
6. [Support information](#support-information)

---
## How to run this app

In order to get this examples working you must follow this steps:

1. Create a Situm account, an Api key and a building. Just follow the steps in [this link](https://situm.com/docs/01-introduction/#3-toc-title)

2. Set your credentials in the app. Go to the `AndroidManifest.xml` file and edit this two fields

``` xml
<meta-data
    android:name="es.situm.sdk.API_USER"
    android:value="API_USER_EMAIL" />
<meta-data
    android:name="es.situm.sdk.API_KEY"
    android:value="API_KEY" />
```

3. Set your [Google Maps api key](https://developers.google.com/maps/documentation/android-sdk/get-api-key) in order to run the examples that show a map. Go to the `AndroidManifest.xml` file and edit this field

``` xml
<meta-data 
    android:name="com.google.android.geo.API_KEY"
    android:value="@string/google_maps_key"/>
```

Perfect! You can now test all the examples in this apps.

---

## Examples

1. [Positioning](https://github.com/situmtech/situm-android-getting-started/blob/master/app/src/main/java/es/situm/gettingstarted/positioning): Download the buildings in your account and how to start the positioning in a building.
2. [Indoor-Outdoor](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/indooroutdoor): Use the indoor-outdoor positioning.
3. [Draw building](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawbuilding): Draw the floorplan of a building over a map.
4. [Draw position](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawposition): Draw the position you obtain from the SDK in the map.
5. [Animate position](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/animateposition): Animate the position and the camera.
6. [Draw route](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawroute): Draw a route between to points over the map.
7. [Guide instructions](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/guideinstructions): Give indications when you are going to a point.
8. [Draw GeoJSON route](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawroutegeojson): Draw Route as GeoJSON.
9. [Draw pois](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawpois): Draw the pois of a building over the map
10. [Point inside geofence](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/pointinsidegeofence): Draw geofences and calculate if a point is inside them.
11. [Show realtime](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/realtime): Draw the users that are position inside a building over a map.
12. [Poi filtering](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/poifiltering): Filter the pois with a especific key-value.
13. [Update location parameters](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/updatelocationparams): Update the parameters of the location on the fly.
14. [Fetch resources](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/fetchresources): Download resources like building info, images, POIs, etc.

---
## Versioning

Please refer to [CHANGELOG.md](./CHANGELOG.md) for a list of notables changes for each version of the plugin.

You can also see the [tags on this repository](https://github.com/situmtech/situm-android-getting-started/tags).

---

## Submitting contributions

You will need to sign a Contributor License Agreement (CLA) before making a submission. [Learn more here](https://situm.com/contributions/). 

---
## License
This project is licensed under the MIT - see the [LICENSE](./LICENSE) file for further details.

---

## More information

More info is available at our [Developers Page](https://situm.com/docs/01-introduction/).

---

## Support information

For any question or bug report, please send an email to [support@situm.es](mailto:support@situm.es)