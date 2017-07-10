package es.situm.gettingstarted.drawpois;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;

import es.situm.gettingstarted.R;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Poi;

/**
 * Created by alberto.penas on 10/07/17.
 */

public class DrawPoisActivity
        extends AppCompatActivity
    implements OnMapReadyCallback {

    private GetPoisUseCase getPoisUseCase = new GetPoisUseCase();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pois);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        getPoisUseCase.cancel();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        getPois(googleMap);
    }

    private void getPois(final GoogleMap googleMap){
        getPoisUseCase.get(new GetPoisUseCase.Callback() {
            @Override
            public void onSuccess(Building building, Collection<Poi> pois) {
                if (pois.isEmpty()){
                    Toast.makeText(DrawPoisActivity.this, "There isnt any poi in the building: " + building.getName() + ". Go to the situm dashboard and create at least one poi before execute again this example", Toast.LENGTH_LONG).show();
                }else {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Poi poi : pois) {
                        LatLng latLng = new LatLng(poi.getCoordinate().getLatitude(),
                                poi.getCoordinate().getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(poi.getName()));
                        builder.include(latLng);
                    }
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DrawPoisActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
