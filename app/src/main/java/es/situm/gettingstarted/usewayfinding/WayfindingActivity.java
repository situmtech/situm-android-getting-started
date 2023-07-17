package es.situm.gettingstarted.usewayfinding;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.LocationPermissions;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.wayfinding.MapView;
import es.situm.sdk.wayfinding.MapViewConfiguration;
import es.situm.sdk.wayfinding.MapViewController;

public class WayfindingActivity extends SampleActivity {
    private static final String TAG = es.situm.gettingstarted.positioning.PositioningActivity.class.getSimpleName();
    private String selectedBuildingId;
    private MapViewController mapViewController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wayfinding);
        selectedBuildingId = getBuildingFromIntent().getIdentifier();

        // You can set the credentials in the AndroidManifest.xml or with:
        // SitumSdk.configuration().setApiKey("USER_EMAIL", "API_KEY");

        // Check permissions and start positioning. This is an optional step, you can integrate
        // MapView without positioning.
        startPositioning();
        // Load the MapView:
        MapView mapView = findViewById(R.id.exampleMapView);
        // More settings coming soon.
        MapViewConfiguration mapViewConfiguration = new MapViewConfiguration.Builder()
                .setBuildingIdentifier(selectedBuildingId)
                .build();
        mapView.load(mapViewConfiguration, new MapView.MapViewCallback() {
            @Override
            public void onLoad(@NonNull MapViewController mapViewController) {
                // Keep the controller to interact with the map.
                WayfindingActivity.this.mapViewController = mapViewController;
            }

            @Override
            public void onError(@NonNull Error error) {
                Log.e(TAG, "Situm> wayfinding> Error loading wayfinding: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop positioning.
        SitumSdk.locationManager().removeUpdates();
    }

    /**
     * Check permissions and start positioning.
     */
    private void startPositioning() {
        LocationPermissions permissions = new LocationPermissions(this);
        permissions.request(new LocationPermissions.Callback() {
            @Override
            public void onPermissionsGranted() {
                Log.i(TAG, "Situm> sdk> Permissions granted, start positioning.");
                LocationRequest locationRequest = new LocationRequest.Builder()
                        .buildingIdentifier(selectedBuildingId)
                        .useDeadReckoning(false)
                        .build();
                SitumSdk.locationManager().requestLocationUpdates(locationRequest);
            }

            @Override
            public void onSomePermissionDenied() {
                Log.e(TAG, "Situm> sdk> Some permission was denied!");
            }
        });
    }
}
