## <a name="buildingevents"></a> List Building Events
In order to know all the `Event` you have in your `Building`, the first thing you have to do is to fetch your buildings and select the one you want to check. This SDK allows you to know the exact position of the `Event` and to know where the message in your smartphone will be shown. In the following example we will show you how to fetch the `Building's` `Events` and how to list them in order to know the details for each one.

First of all, to fetch the events you will have to use the `communicationManager()` again and call the method `fetchEventsFromBuilding`. This method returns a list of `SitumEvent`.

If you want to show the `Event` data, you will have to use the `SitumEvent`variable that provides a method called `getHtml()` and `getName()`, the first method returns the value that the `Event` will display in the Smartphone when over the `Event's` conversion area, the second one returns the name of the event.

You can see how to get the events and show them in a list in the classes `GetBuildingEvents.java` and `EventAdapter`.

You can get more information about `Event` in the [SDK documentation](http://developers.situm.es/sdk_documentation/android/javadoc/2.31.3/).
