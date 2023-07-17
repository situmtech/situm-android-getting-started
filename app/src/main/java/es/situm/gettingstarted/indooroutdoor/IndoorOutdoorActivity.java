package es.situm.gettingstarted.indooroutdoor;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.common.LocationPermissions;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.model.location.Location;

public class IndoorOutdoorActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private TextView statusTV;
    private TextView resultTV;
    private ToggleButton toggleButton;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_outdoor);
        requestLocationPermissions();
        setup();
    }

    private void setup() {
        locationManager = SitumSdk.locationManager();
        resultTV = findViewById(R.id.textView);
        statusTV = findViewById(R.id.status_tv);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startPositioning();
            } else {
                stopPositioning();
            }
        });
    }

    private void startPositioning() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                resultTV.setText(location.toString());
            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                statusTV.setText("Status: " + locationStatus.name());
            }

            @Override
            public void onError(@NonNull Error error) {
                toggleButton.setChecked(false);
                Toast.makeText(IndoorOutdoorActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        LocationRequest locationRequest = new LocationRequest.Builder()
                // Do not set building identifier to start Global Mode.
                .useWifi(true)
                .useBle(true)
                .useForegroundService(true)
                .build();
        locationManager.requestLocationUpdates(locationRequest, locationListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPositioning();
    }

    private void stopPositioning() {
        if (!locationManager.isRunning()) {
            return;
        }
        locationManager.removeUpdates(locationListener);
        statusTV.setText("Status:");
        resultTV.setText("");
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
                resultTV.setText("Permissions must be granted to start positioning.");
            }
        });
    }
}
