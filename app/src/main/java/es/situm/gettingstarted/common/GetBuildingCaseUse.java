package es.situm.gettingstarted.common;


import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collection;
import java.util.List;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.cartography.Geofence;
import es.situm.sdk.utils.Handler;

/**
 * Created by alejandro.trigo on 31/01/18.
 *
 */

public class GetBuildingCaseUse {

    private static final String TAG = GetBuildingCaseUse.class.getSimpleName();

    private Building currBuilding;
    private Floor currFloor;

    public interface Callback{
        void onSuccess(Building building, Floor floor, Bitmap bitmap);
        void onError(Error error);
    }

    public interface GeofencesCallback {
        void onSuccess(Floor floor, Bitmap bitmap, List<Geofence> geofences);
        void onError(Error error);
    }

    private Callback callback;

    public Building get(final String buildingId, final Callback callback){
        if(hasCallback()){
            Log.d(TAG, "get: already running");
            return null;
        }
        this.callback = callback;
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>(){

            @Override
            public void onSuccess(Collection<Building> buildings) {
                if(!buildings.isEmpty()){
                    for(Building bui : buildings){
                        Log.d(TAG, "onSuccess: " + bui.toString());
                        if (bui.getIdentifier().equals(buildingId)){
                            currBuilding = bui;
                        }
                    }

                    SitumSdk.communicationManager().fetchFloorsFromBuilding(currBuilding.getIdentifier(), new Handler<Collection<Floor>>() {
                        @Override
                        public void onSuccess(Collection<Floor> floors) {
                            if(!floors.isEmpty()){
                                currFloor = floors.iterator().next();
                                SitumSdk.communicationManager().fetchMapFromFloor(currFloor, new Handler<Bitmap>() {
                                    @Override
                                    public void onSuccess(Bitmap bitmap) {
                                        if(hasCallback()){
                                            callback.onSuccess(currBuilding, currFloor, bitmap);
                                        }
                                        clearCallback();
                                    }

                                    @Override
                                    public void onFailure(Error error) {
                                        if(hasCallback()){
                                            callback.onError(error);
                                        }
                                        clearCallback();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Error error) {
                            if(hasCallback()){
                                callback.onError(error);
                            }
                            clearCallback();
                        }

                    });
                }
            }

            @Override
            public void onFailure(Error error) {
                if(hasCallback()){
                    callback.onError(error);
                }
                clearCallback();
            }
        });

        return currBuilding;
    }

    public void getGeofences(Building building, GeofencesCallback geofencesCallback) {
        SitumSdk.communicationManager().fetchFloorsFromBuilding(building.getIdentifier(), new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> floors) {
                if(!floors.isEmpty()){
                    Floor floor = floors.iterator().next();
                    SitumSdk.communicationManager().fetchMapFromFloor(floor, new Handler<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            SitumSdk.communicationManager().fetchGeofencesFromBuilding(building, new Handler<List<Geofence>>() {
                                @Override
                                public void onSuccess(List<Geofence> geofences) {
                                    geofencesCallback.onSuccess(floor, bitmap, geofences);
                                }

                                @Override
                                public void onFailure(Error error) {
                                    geofencesCallback.onError(error);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Error error) {
                            geofencesCallback.onError(error);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Error error) {
                geofencesCallback.onError(error);
            }

        });

    }

    public void cancel (){
        callback = null;
    }

    private boolean hasCallback(){
        return callback != null;
    }

    private void clearCallback(){
        callback = null;
    }

}
