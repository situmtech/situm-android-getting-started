package es.situm.gettingstarted.realtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.concurrent.TimeUnit;

import es.situm.gettingstarted.R;
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
        extends AppCompatActivity
        implements OnMapReadyCallback {

    private GetBuildingsUseCase getBuildingsUseCase = new GetBuildingsUseCase();
    private GoogleMap googleMap;
    private ProgressBar progressBar;
    private TextView noDevicesTV;
    private List<Marker>markers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);
        setup();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        getBuildingsUseCase.cancel();
        SitumSdk.realtimeManager().removeRealTimeUpdates();
        super.onDestroy();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        getBuildingsUseCase.get(new GetBuildingsUseCase.Callback() {
            @Override
            public void onSuccess(List<Building> buildings) {
                hideProgress();
                if (buildings != null && !buildings.isEmpty()) {
                    Building building = buildings.get(0);
                    realtime(building);
                }
            }

            @Override
            public void onError(String error) {
                hideProgress();
                Toast.makeText(RealTimeActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void realtime(Building building) {
        RealTimeRequest realTimeRequest = new RealTimeRequest.Builder()
                .pollTimeMs(3000)
                .building(building)
                .build();
        SitumSdk.realtimeManager().requestRealTimeUpdates(realTimeRequest, new RealTimeListener() {
            @Override
            public void onUserLocations(RealTimeData realTimeData) {
                if(realTimeData.getLocations().isEmpty()){
                    noDevicesTV.setVisibility(View.VISIBLE);
                    for (Marker marker : markers) {
                        marker.remove();
                    }
                    markers.clear();
                }else {
                    noDevicesTV.setVisibility(View.GONE);
                    for (Marker marker : markers) {
                        marker.remove();
                    }
                    markers.clear();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Location location : realTimeData.getLocations()) {
                        LatLng latLng = new LatLng(location.getCoordinate().getLatitude(),
                                location.getCoordinate().getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(location.getDeviceId());
                        Marker marker = googleMap.addMarker(markerOptions);
                        markers.add(marker);
                        builder.include(latLng);
                    }
                    try {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    } catch (IllegalStateException e) {
                    }
                }
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(RealTimeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setup() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noDevicesTV = (TextView) findViewById(R.id.rt_nodevices_tv);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }
}
