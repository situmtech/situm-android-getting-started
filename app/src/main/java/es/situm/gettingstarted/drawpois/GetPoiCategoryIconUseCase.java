package es.situm.gettingstarted.drawpois;

import android.graphics.Bitmap;

import java.util.Random;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Poi;
import es.situm.sdk.utils.Handler;

/**
 * Created by Cristina S. Barreiro on 21/09/2018.
 */
public class GetPoiCategoryIconUseCase {

    public interface Callback{
        void onSuccess(Bitmap bitmap);
        void onError(String error);
    }

    public void getUnselectedIcon(Poi poi, final Callback callback) {
        SitumSdk.communicationManager().fetchPoiCategoryIcon(poi.getCategory(),false, new Handler<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                callback.onSuccess(bitmap);
            }

            @Override
            public void onFailure(Error error) {
                callback.onError(error.getMessage());
            }
        });

    }
}
