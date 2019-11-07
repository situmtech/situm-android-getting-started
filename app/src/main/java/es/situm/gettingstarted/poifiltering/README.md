## <a name="filterpois"></a> Filter Building's POIs
You can filter your `Building` `POI`s by adding to the `POI` a Key-Value pair. You can add the Key-Value pair in the [Dashboard](https://dashboard.situm.es) when creating or updating a `POI` in the last section of the form.
Like you did in the [draw pois](https://github.com/situmtech/situm-android-getting-started/tree/master/app/src/main/java/es/situm/gettingstarted/drawpois) sample you need to get the list of your `Building` `POI`s


Now, with the `POI`s in an `ArrayList` you can make your own function to get the `POI`s filtered by the Key and Value you want.
In the method `filter` of the class `FilteringActivity` you can see how to do this.

If you want to know more about filtering `POI`s you can check the [SDK documentation](http://developers.situm.es/sdk_documentation/android/javadoc/latest).
