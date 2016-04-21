package es.situm.gettingstarted;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.situm.sdk.v1.SitumBuilding;
import es.situm.sdk.v1.SitumDataManager;
import es.situm.sdk.v1.SitumError;
import es.situm.sdk.v1.SitumIPSManager;
import es.situm.sdk.v1.SitumLevel;
import es.situm.sdk.v1.SitumLocation;
import es.situm.sdk.v1.SitumLogin;
import es.situm.sdk.v1.SitumPoseReceiver;
import es.situm.sdk.v1.SitumResponseHandler;
import es.situm.sdk.v1.SitumSensorErrorListener;

public class SitumMainActivity extends AppCompatActivity {

    private static final String TAG = "SitumMainActivity";

    private SitumBuilding selectedBuilding;
    private SitumIPSManager ipsManager;
    private TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situm_main);
        final ImageView imageLevel = (ImageView) findViewById(R.id.image_level);
        txtLocation = (TextView) findViewById(R.id.location);

        //PLEASE INSERT HERE YOUR EMAIL AND YOUR API KEY
        final SitumDataManager situmDataManager = SitumLogin.login("test@situm.es", "YOUR_API_KEY_HERE");
        ipsManager = new SitumIPSManager(getApplicationContext());

        // get all buildings
        situmDataManager.fetchBuildings(new SitumResponseHandler() {
            @Override
            public void onListReceived(List list) {
                final List<SitumBuilding> buildings = new ArrayList<>(list);
                for (SitumBuilding building : buildings) {
                    Log.i(TAG, "Received building " + building.getName());
                }
                selectedBuilding = buildings.get(0);

                // init positioning
                startPositioning(selectedBuilding);

                // get all levels for the firs building in the list
                situmDataManager.fetchLevelsForBuilding(selectedBuilding, new SitumResponseHandler() {
                    @Override
                    public void onListReceived(List list) {
                        List<SitumLevel> levels = new ArrayList<>(list);
                        Log.i(TAG, String.format("Received %s levels for %s", levels.size(), selectedBuilding.getName()));

                        final SitumLevel selectedLevel = levels.get(0);

                        // get image for selectedLevel
                        fetchMapForLevel(selectedLevel, situmDataManager, imageLevel);
                    }

                    @Override
                    public void onErrorReceived(int i, Header[] h, byte[] bytes, Throwable t) {
                        Log.e(TAG, "Error receiving levels for building " + selectedBuilding.getName());
                    }
                });
            }

            @Override
            public void onErrorReceived(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e(TAG, "Error receiving buildings");
            }
        });

        ipsManager.setSensorErrorListener(new SitumSensorErrorListener() {
            @Override
            public void onError(SitumError situmError) {
                Log.e(TAG, situmError.name + " " + situmError.description);
                txtLocation.setText(situmError.name);
            }
        });

    }

    private void fetchMapForLevel(SitumLevel selectedLevel, SitumDataManager situmDataManager, final ImageView imageLevel) {
        situmDataManager.fetchMapForLevel(selectedLevel,
                new FileAsyncHttpResponseHandler(getApplicationContext()) {
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, File file) {
                        Log.e(TAG, "Error downloading image for level");
                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, File file) {
                        Bitmap mapImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imageLevel.setImageBitmap(mapImage);
                    }
                });
    }

    private void startPositioning(SitumBuilding building) {
        Log.i(TAG, "Initializing positioning in " + building.getName());

        // you can change the sensors for positioning and enable all
        ipsManager.start(building, false, true, true);

        ipsManager.setPoseReceiver(new SitumPoseReceiver() {
            @Override
            public void onPoseReceived(SitumLocation situmLocation) {
                Log.i(TAG, String.format("x %s y %s", situmLocation.x, situmLocation.y));
                txtLocation.setText(String.format("x %s y %s", situmLocation.x, situmLocation.y));
            }

            @Override
            public void onInvalidPoseReceived(SitumLocation situmLocation) {
                Log.w(TAG, String.format("x %s y %s", situmLocation.x, situmLocation.y));
            }
        });
    }
}
