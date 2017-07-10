package es.situm.gettingstarted.drawroute;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.drawpois.GetPoisUseCase;
import es.situm.sdk.SitumSdk;
import es.situm.sdk.directions.DirectionsRequest;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Poi;
import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.directions.Route;
import es.situm.sdk.model.navigation.NavigationProgress;
import es.situm.sdk.navigation.NavigationListener;
import es.situm.sdk.navigation.NavigationRequest;
import es.situm.sdk.utils.Handler;

/**
 * Created by alberto.penas on 10/07/17.
 */

public class DrawRouteActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {

    private final String TAG = getClass().getSimpleName();
    private GetPoisUseCase getPoisUseCase = new GetPoisUseCase();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_route);
        setup();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        getPoisUseCase.cancel();
        SitumSdk.navigationManager().removeUpdates();
        super.onDestroy();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        getPoisUseCase.get(new GetPoisUseCase.Callback() {
            @Override
            public void onSuccess(Building building, Collection<Poi> pois) {
                if (pois.size() < 2){
                    hideProgress();
                    Toast.makeText(DrawRouteActivity.this,
                            "Its mandatory to have at least two pois in a building: " + building.getName() + " to start directions manager",
                            Toast.LENGTH_LONG)
                            .show();
                }else {
                    Iterator<Poi>iterator = pois.iterator();
                    final Point from = iterator.next().getPosition();
                    final Point to = iterator.next().getPosition();
                    DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                            .from(from, null)
                            .to(to)
                            .build();
                    SitumSdk.directionsManager().requestDirections(directionsRequest, new Handler<Route>() {
                        @Override
                        public void onSuccess(Route route) {
                            PolylineOptions polyLineOptions = new PolylineOptions().color(Color.GREEN).width(4f);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            List<Point> routePoints = route.getPoints();
                            for (Point point:routePoints){
                                LatLng latLng = new LatLng(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude());
                                builder.include(latLng);
                                polyLineOptions.add(latLng);
                            }
                            builder.include(new LatLng(from.getCoordinate().getLatitude(), from.getCoordinate().getLongitude()));
                            builder.include(new LatLng(to.getCoordinate().getLatitude(), to.getCoordinate().getLongitude()));
                            googleMap.addPolyline(polyLineOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

                            hideProgress();
                            startNav(route);
                        }

                        @Override
                        public void onFailure(Error error) {
                            hideProgress();
                            Toast.makeText(DrawRouteActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                hideProgress();
                Toast.makeText(DrawRouteActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void startNav(Route route){
        NavigationRequest navigationRequest = new NavigationRequest.Builder()
                .route(route)
                .distanceToGoalThreshold(3d)
                .outsideRouteThreshold(50d)
                .build();
        SitumSdk.navigationManager().requestNavigationUpdates(navigationRequest, new NavigationListener() {
            @Override
            public void onDestinationReached() {
                Log.d(TAG, "onDestinationReached: ");
            }

            @Override
            public void onProgress(NavigationProgress navigationProgress) {
                Log.d(TAG, "onProgress: ");
            }

            @Override
            public void onUserOutsideRoute() {
                Log.d(TAG, "onUserOutsideRoute: ");
            }
        });
    }

    private void setup() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }
}
