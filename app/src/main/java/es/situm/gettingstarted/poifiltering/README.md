## <a name="filterpois"></a> Filter Building's POIs
You can filter your `Building` `POIs` by adding to the `POI` a Key-Value pair. You can add the Key-Value pair in the [Dashboard](https://dashboard.situm.es) when creating or updating a `POI` in the last section of the form.
In order to get the `Building` `POIs`, you have to fetch your buildings and select the one you want to work with. After that fetch the `Building's` `POIs` with the `CommunicationManager()`
```java
SitumSdk.communicationManager().fetchIndoorPOIsFromBuilding(building, new Handler<Collection<Poi>>(){

                    @Override
                    public void onSuccess(Collection<Poi> pois) {
                        for(Poi poi : pois){
                            Log.i(TAG, "onSuccess: poi: " + poi);
                        }
                        poiList.clear();
                        poiList.addAll(pois);
                        showPoiDataView();
                        filteringAdapter.setSearchData(poiList);
                        pbSearchLoading.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Error error) {
                        Log.e(TAG, "onFailure: fetching pois: " + error);
                        showErrorMessage();
                    }
                });
```

With the `POIs` in an `ArrayList` you can now make your own function to get the `POIs` filtered by the Key and Value you want, here in the example we are providing a layout with a form to select the Key and Value you want:
```java
public void filter (String key, String value){

        List<Poi> poiListTemp = new ArrayList<>();
        Map<String, String> kVList;


        for(Poi p : poiList){
            kVList = p.getCustomFields();
            Log.d(TAG, kVList.toString());
            if(!kVList.isEmpty()) {
                if(!kVList.containsKey(key)){;
                    continue;
                }
                if(!kVList.containsValue(value)){
                    continue;
                }
                if (kVList.get(key).equals(value)) {
                    poiListTemp.add(p);
                }

            }
            else{
                continue;
            }
        }
        if(!poiListTemp.isEmpty()){
            filteringAdapter.setSearchData(poiListTemp);
        }else{
            showErrorMessage();
        }
}
```

If you want to know more about filtering `POIs` you can check the [SDK documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.31.3/).
