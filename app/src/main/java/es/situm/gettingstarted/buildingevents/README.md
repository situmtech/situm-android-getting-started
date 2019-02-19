## <a name="buildingevents"></a> List Building Events
In order to know all the `Event` you have in your `Building`, the first thing you have to do is to fetch your buildings and select the one you want to check. This SDK allows you to know the exact position of the `Event` and to know where the message in your smartphone will be shown. In the following example we will show you how to fetch the `Building's` `Events` and how to list them in order to know the details for each one.

First of all, to fetch the events you will have to use the `communicationManager()` again:
```java
SitumSdk.communicationManager().fetchEventsFromBuilding(building, new Handler<Collection<SitumEvent>>(){

	@Override
	public void onSuccess(Collection<SitumEvent> situmEvents) {
		events.clear();
		events.addAll(situmEvents);
		showEventDataView();
		eventAdapter.setEventData(events);
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onFailure(Error error) {
		Log.e(TAG, "onFailure: fetching events: " + error);
		showErrorMessage();
	}
});
```

I you want to show the `Event` data, you will have to use the `SitumEvent`variable that provides a method called `getHtml()` and `getName()`, the first method returns the value that the `Event` will display in the Smartphone when over the `Event's` conversion area, the second one returns the name of the event.

Here you can see a method to display this values in your code after fetching the `Events` in your `Building` with a viewholder for the `RecyclerView` and a `WebView`:
```java
@Override
public void onClick(View view) {
    int adapterPosition = getAdapterPosition();
    if (mWebView.getVisibility() == (View.VISIBLE)){
        mWebView.setVisibility(View.GONE);
    }else{
        mWebView.setVisibility(View.VISIBLE);
    }

    mWebView.loadDataWithBaseURL(null, mEventData.get(adapterPosition).getHtml(), "text/html", "utf-8", null);

    mClickHandler.onClick("");

}
```

You can get more information about `Event` in the [SDK documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.31.3/).
