## <a name="fetchresources"><a/> Fetch Resources using CommunicationManager

This functionality will allow you to request specific information from the SDK and configure how those requests are made.

The basic configuration includes the following options:
* Cache Strategy - Defines how the cache will be used. For more information, please refer to the [Javadoc](http://developers.situm.es/sdk_documentation/android/javadoc/latest/es/situm/sdk/configuration/network/NetworkOptions.CacheStrategy.html)
* Preload Images - If a cartography request has associated images and this value is active, those images will be downloaded to cache.

In this example we will show you how to execute some basic requests and how to configure them.

Firstly you must create a CommunicationConfig object. We provide a CommunicationConfigImpl in our SDK that you may use.
This object contains a NetworkOptions object inside. We also provide a NetworkOptionsImpl which can be created with it's own Builder.
You can see how to do this in `FetchResourcesActivity` 

Note: You don't have to use the object we provide, as long as the objects you use implement the required interfaces.

Secondly you just include the configuration object in the desired CommunicationManager request.
Note: Every request can be executed without a CommunicationConfig object. In this case, the SDK will assume it's default behaviour.