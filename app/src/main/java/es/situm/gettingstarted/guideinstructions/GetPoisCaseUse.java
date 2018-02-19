package es.situm.gettingstarted.guideinstructions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Poi;
import es.situm.sdk.utils.Handler;

/**
 *
 * Created by alejandro.trigo on 19/01/18.
 */

public class GetPoisCaseUse {

    private static final String TAG = GetPoisCaseUse.class.getSimpleName();

    interface Callback{
        void onSuccess(List<Poi> pois);
        void onError(Error error);
    }

    private Callback callback;

    void get(final Building building, final Callback callback ){
        if(hasCallback()){
            return;
        }
        this.callback = callback;
        SitumSdk.communicationManager().fetchIndoorPOIsFromBuilding(building, new Handler<Collection<Poi>>() {
            @Override
            public void onSuccess(Collection<Poi> pois) {

                List<Poi> buildPois = new ArrayList<>();
                buildPois.addAll(pois);
                if(hasCallback()) {

                    callback.onSuccess(buildPois);
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

    void cancel (){
        callback = null;
    }

    private boolean hasCallback(){
        return callback != null;
    }

    private void clearCallback(){
        callback = null;
    }

}
