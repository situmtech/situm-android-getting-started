package es.situm.gettingstarted.positioning;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.LocationPermissions;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.model.location.CartesianCoordinate;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.model.location.Location;

public class PositioningActivity extends SampleActivity {
    private static final String TAG = PositioningActivity.class.getSimpleName();
    private String selectedBuildingId;
    private ToggleButton toggleButtonStart;
    private TextView tvLocation;
    private TextView tvLocationStatus;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "Situm> sdk> onLocationChanged() called with: location = [" + location + "]");
            CartesianCoordinate cartesianCoordinate = location.getCartesianCoordinate();
            Coordinate coordinate = location.getCoordinate();
            String locationMessage =
                    "building: " + location.getBuildingIdentifier() + "\n" +
                            "floor: " + location.getFloorIdentifier() + "\n" +
                            "x: " + cartesianCoordinate.getX() + "\n" +
                            "y: " + cartesianCoordinate.getY() + "\n" +
                            "lat: " + coordinate.getLatitude() + "\n" +
                            "lng: " + coordinate.getLongitude() + "\n" +
                            "yaw: " + location.getCartesianBearing() + "\n" +
                            "accuracy: " + location.getAccuracy();

            tvLocation.setText(locationMessage);
            tvLocationStatus.setText("");
        }

        @Override
        public void onStatusChanged(@NonNull LocationStatus status) {
            Log.i(TAG, "Situm> sdk> onStatusChanged() called with: status = [" + status + "]");
            tvLocationStatus.setText(String.valueOf(status));
        }

        @Override
        public void onError(@NonNull Error error) {
            Log.e(TAG, "Situm> sdk> onError() called with: error = [" + error + "]");
            toggleButtonStart.setChecked(false);
            tvLocationStatus.setText(error.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);

        toggleButtonStart = findViewById(R.id.toggleButtonStart);
        tvLocation = findViewById(R.id.location);
        tvLocationStatus = findViewById(R.id.location_status);

        // Request location permissions:
        requestLocationPermissions();

        // You can set the credentials in the AndroidManifest.xml or with:
        // SitumSdk.configuration().setUserPass("USER_EMAIL", "PASSWORD");
        // SitumSdk.configuration().setApiKey("USER_EMAIL", "API_KEY");

        selectedBuildingId = getBuildingFromIntent().getIdentifier();

        toggleButtonStart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startPositioning();
            } else {
                stopPositioning();
            }
        });
    }

    /**
     * Start the indoor positioning in the building
     */
    private void startPositioning() {
        if (selectedBuildingId == null) {
            toggleButtonStart.setChecked(false);
            Log.e(TAG, "Situm> sdk> onSuccess: building with id=" + selectedBuildingId + " not found");
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder()
                .buildingIdentifier(selectedBuildingId)
                .build();
        Log.i(TAG, "Situm> sdk> startPositioning: starting positioning in " + selectedBuildingId);
        SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
    }

    private void stopPositioning() {
        tvLocation.setText("");
        tvLocationStatus.setText("");
        SitumSdk.locationManager().removeUpdates(locationListener);
    }

    private void requestLocationPermissions() {
        new LocationPermissions(this).request(new LocationPermissions.Callback() {
            @Override
            public void onPermissionsGranted() {
                Log.i(TAG, "Situm> sdk> Permissions granted!");
            }

            @Override
            public void onSomePermissionDenied() {
                Log.e(TAG, "Situm> sdk> Some permission was denied!");
                tvLocationStatus.setText("Permissions must be granted to start positioning.");
            }
        });
    }
}
