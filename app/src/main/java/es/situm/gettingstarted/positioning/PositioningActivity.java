package es.situm.gettingstarted.positioning;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.Collection;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.SampleActivity;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.location.CartesianCoordinate;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.model.location.Location;
import es.situm.sdk.utils.Handler;

public class PositioningActivity extends SampleActivity {
    private static final String TAG = PositioningActivity.class.getSimpleName();

    private String buildingId;

    private ToggleButton toggleButtonStart;
    private TextView tvLocation;
    private TextView tvLocationStatus;

    private Building selectedBuilding;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged() called with: location = [" + location + "]");
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
            Log.i(TAG, "onStatusChanged() called with: status = [" + status + "]");
            tvLocationStatus.setText(String.valueOf(status));
        }

        @Override
        public void onError(@NonNull Error error) {
            Log.e(TAG, "onError() called with: error = [" + error + "]");
            toggleButtonStart.setChecked(false);
            tvLocationStatus.setText(error.toString());

            switch (error.getCode()) {
                case LocationManager.Code.MISSING_LOCATION_PERMISSION:
                    requestLocationPermission();
                    break;
                case LocationManager.Code.LOCATION_DISABLED:
                    showLocationSettings();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);

        toggleButtonStart = (ToggleButton) findViewById(R.id.toggleButtonStart);
        tvLocation = (TextView) findViewById(R.id.location);
        tvLocationStatus = (TextView) findViewById(R.id.location_status);

        // You can set the credentials in the AndroidManifest.xml or with:
//        SitumSdk.configuration().setUserPass("USER_EMAIL", "PASSWORD");
//        SitumSdk.configuration().setApiKey("USER_EMAIL", "API_KEY");

        buildingId = getBuildingFromIntent().getIdentifier();

        toggleButtonStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startPositioning();
                } else {
                    stopPositioning();
                }
            }
        });

        //Get all the buildings of the account
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {
            @Override
            public void onSuccess(Collection<Building> buildings) {
                Log.d(TAG, "onSuccess: Your buildings: ");
                for (Building building : buildings) {
                    Log.i(TAG, "onSuccess: " + building.getIdentifier() + " - " + building.getName());

                    if (buildingId.equals(building.getIdentifier())) {
                        selectedBuilding = building;
                    }
                }

                if (buildings.isEmpty()) {
                    Log.e(TAG, "onSuccess: you have no buildings. Create one in the Dashboard");
                }
            }

            @Override
            public void onFailure(Error error) {
                Log.e(TAG, "onFailure:" + error);
            }
        });

    }

    /**
     * Start the indoor positioning in the building
     */
    private void startPositioning() {
        if (selectedBuilding == null) {
            toggleButtonStart.setChecked(false);
            Log.e(TAG, "onSuccess: building with id=" + buildingId + " not found");
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder()
                .buildingIdentifier(selectedBuilding.getIdentifier())
                .build();
        Log.i(TAG, "startPositioning: starting positioning in " + selectedBuilding.getName());
        SitumSdk.locationManager().requestLocationUpdates(locationRequest, locationListener);
    }

    private void stopPositioning() {
        tvLocation.setText("");
        tvLocationStatus.setText("");
        SitumSdk.locationManager().removeUpdates(locationListener);
    }

    /**
     * Open the location settings (for API >= 23)
     */
    private void showLocationSettings() {
        Toast.makeText(this, "You must enable location", Toast.LENGTH_LONG).show();
        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
    }

    /**
     * Open the dialog to request location permission (for API >= 23)
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(PositioningActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                0);
    }
}
