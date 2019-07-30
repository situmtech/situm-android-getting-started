package es.situm.gettingstarted;

import androidx.multidex.MultiDexApplication;

import es.situm.sdk.SitumSdk;

public class GettingStartedApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //You must initialize the Situm SDK before using it
        SitumSdk.init(this);

    }
}
