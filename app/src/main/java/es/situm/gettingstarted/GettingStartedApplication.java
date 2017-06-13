package es.situm.gettingstarted;

import android.app.Application;
import android.util.Log;

import es.situm.sdk.SitumSdk;

public class GettingStartedApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //You must initialize the Situm SDK before using it
        SitumSdk.init(this);

    }
}
