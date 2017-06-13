package es.situm.gettingstarted.drawbuilding;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collection;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.utils.Handler;

/**
 * Created by alberto.penas on 13/06/17.
 */

class GetBuildingImageUseCase {


    interface Callback{
        void onSuccess(Building building, Bitmap bitmap);
        void onError(Error error);
    }


    private Callback callback;


    void get(final Callback callback){
        if (hasCallback()){
            Log.d("GetBuildingImageUseCase", "already running");
            return;
        }
        this.callback = callback;
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {
            @Override
            public void onSuccess(Collection<Building> buildings) {
                if (!buildings.isEmpty()){
                    final Building building = buildings.iterator().next();
                    SitumSdk.communicationManager().fetchFloorsFromBuilding(building, new Handler<Collection<Floor>>() {
                        @Override
                        public void onSuccess(Collection<Floor> floors) {
                            if (!floors.isEmpty()){
                                Floor floor = floors.iterator().next();
                                SitumSdk.communicationManager().fetchMapFromFloor(floor, new Handler<Bitmap>() {
                                    @Override
                                    public void onSuccess(Bitmap bitmap) {
                                        if (hasCallback()){
                                            callback.onSuccess(building, bitmap);
                                        }
                                        clearCallback();
                                    }

                                    @Override
                                    public void onFailure(Error error) {
                                        if (hasCallback()){
                                            callback.onError(error);
                                        }
                                        clearCallback();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Error error) {
                            if (hasCallback()){
                                callback.onError(error);
                            }
                            clearCallback();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Error error) {
                if (hasCallback()){
                    callback.onError(error);
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
