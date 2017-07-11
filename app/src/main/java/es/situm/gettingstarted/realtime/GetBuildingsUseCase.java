package es.situm.gettingstarted.realtime;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.utils.Handler;

/**
 * Created by alberto.penas on 13/06/17.
 */

class GetBuildingsUseCase {


    interface Callback{
        void onSuccess(List<Building> building);
        void onError(String error);
    }


    private Callback callback;


    void get(final Callback callback){
        if (hasCallback()){
            Log.d("GetBuildingsUseCase", "already running");
            return;
        }
        this.callback = callback;
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {
            @Override
            public void onSuccess(Collection<Building> buildings) {
                if (buildings.isEmpty()){
                    if (hasCallback()) {
                        callback.onError("There isnt any building, before execute this example is needed that you go to dashboard an create a new one");
                    }
                }else{
                    if (hasCallback()) {
                        List<Building>buildingList = new ArrayList<>();
                        buildingList.addAll(buildings);
                        callback.onSuccess(buildingList);
                    }
                }
                clearCallback();
            }

            @Override
            public void onFailure(Error error) {
                if (hasCallback()){
                    callback.onError(error.getMessage());
                }
                clearCallback();
            }
        });
    }

    void cancel(){
        callback = null;
    }

    private boolean hasCallback(){
        return callback != null;
    }

    private void clearCallback(){
        callback = null;
    }
}
