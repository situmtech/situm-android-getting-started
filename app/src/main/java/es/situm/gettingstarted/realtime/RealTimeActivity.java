package es.situm.gettingstarted.realtime;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.location.Location;
import es.situm.sdk.model.realtime.RealTimeData;
import es.situm.sdk.realtime.RealTimeListener;
import es.situm.sdk.realtime.RealTimeRequest;

/**
 * Created by alberto.penas on 10/07/17.
 */

public class RealTimeActivity
        extends SampleActivity
        implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private List<Marker> markers = new ArrayList<>();
    private Building building;

    @Nullable
    private Snackbar snackbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        building = getBuildingFromIntent();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        SitumSdk.realtimeManager().removeRealTimeUpdates();
        super.onDestroy();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        startRealtime();
    }

    private void startRealtime() {
        RealTimeRequest realTimeRequest = new RealTimeRequest.Builder()
                .pollTimeMs(3000)
                .building(building)
                .build();
        SitumSdk.realtimeManager().requestRealTimeUpdates(realTimeRequest, new RealTimeListener() {
            @Override
            public void onUserLocations(RealTimeData realTimeData) {
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();

                if (realTimeData.getLocations().isEmpty()) {
                    showNoDevicesMessage();
                } else {
                    hideNoDevicesMessage();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Location location : realTimeData.getLocations()) {
                        LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                                location.getCoordinate().getLongitude());
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(location.getDeviceId())
                        );
                        markers.add(marker);

                        builder.include(latLng);
                    }
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                }
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(RealTimeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void hideNoDevicesMessage() {
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    void showNoDevicesMessage() {
        if (snackbar != null) {
            return;
        }
        snackbar = Snackbar.make(findViewById(R.id.container), "There are no devices positioning inside this building", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }
}
