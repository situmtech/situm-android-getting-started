package es.situm.gettingstarted.animateposition;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.model.location.Location;

public class PositionAnimator {

    private static final String TAG = PositionAnimator.class.getSimpleName();
    private static final int DURATION_POSITION_ANIMATION = 500;
    private static final int DURATION_BEARING_ANIMATION = 200;
    private static final int DURATION_RADIUS_ANIMATION = 500;

    private static final double DISTANCE_CHANGE_TO_ANIMATE = 0.2;
    private static final int BEARING_CHANGE_TO_ANIMATE = 1;
    private static final double GROUND_OVERLAY_DIAMETER_CHANGE_TO_ANIMATE = 0.2;

    @Nullable
    private Location lastLocation;
    private LatLng lastLatLng;
    private LatLng destinationLatLng;

    private float lastBearing;
    private float destinationBearing;
    private float lastGroundOverlayDimensions;
    private float destinationGroundOverlayDimensions;

    private ValueAnimator locationAnimator = new ValueAnimator();
    private ValueAnimator locationBearingAnimator = new ValueAnimator();
    private ValueAnimator groundOverlayAnimator = new ValueAnimator();

    synchronized void animate(final Marker marker, final GroundOverlay groundOverlay, final Location location) {
        Coordinate toCoordinate = location.getCoordinate();
        final LatLng toLatLng = new LatLng(toCoordinate.getLatitude(), toCoordinate.getLongitude());
        final float toBearing = (float) location.getBearing().degrees();
        final float groundOverlayDiameter = location.getAccuracy() * 2;

        if (lastLocation == null) { //First location
            marker.setRotation(toBearing);
            marker.setPosition(toLatLng);

            groundOverlay.setPosition(toLatLng);
            groundOverlay.setDimensions(groundOverlayDiameter);

            lastLocation = location;
            lastLatLng = toLatLng;
            lastBearing = toBearing;
            lastGroundOverlayDimensions = groundOverlayDiameter;
            return;
        }

        animatePosition(marker, groundOverlay, location);
        animateBearing(marker, location);
        animateGroundOverlay(groundOverlay, groundOverlayDiameter);
    }

    private void animatePosition(final Marker marker, final GroundOverlay groundOverlay, Location toLocation){
        Coordinate toCoordinate = toLocation.getCoordinate();
        final LatLng toLatLng = new LatLng(toCoordinate.getLatitude(), toCoordinate.getLongitude());

        if ( destinationLatLng != null) {
            float[] results = new float[1];
            android.location.Location.distanceBetween(toCoordinate.getLatitude(), toCoordinate.getLongitude(),
                    destinationLatLng.latitude, destinationLatLng.longitude, results);
            float distance = results[0];
            if (distance < DISTANCE_CHANGE_TO_ANIMATE) {
                return;
            }
        }
        if (destinationLatLng == toLatLng) {
            return;
        }

        locationAnimator.cancel();
        if (lastLocation != toLocation) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                //hardfix for crash in API 19 at PropertyValuesHolder.setupSetterAndGetter()
                marker.setPosition(toLatLng);
                groundOverlay.setPosition(toLatLng);
            } else {

                locationAnimator = new ObjectAnimator();
                locationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    LatLng startLatLng = lastLatLng;

                    @Override
                    public synchronized void onAnimationUpdate(ValueAnimator animation) {
                        float t = animation.getAnimatedFraction();
                        lastLatLng = interpolateLatLng(t, startLatLng, toLatLng);

                        marker.setPosition(lastLatLng);
                        groundOverlay.setPosition(lastLatLng);
                    }
                });
                locationAnimator.setFloatValues(0, 1); //Ignored
                locationAnimator.setDuration(DURATION_POSITION_ANIMATION);
                locationAnimator.start();
            }
            destinationLatLng = toLatLng;
        }
    }

    private LatLng interpolateLatLng(float fraction, LatLng a, LatLng b) {
        double lat = (b.latitude - a.latitude) * fraction + a.latitude;
        double lng = (b.longitude - a.longitude) * fraction + a.longitude;
        return new LatLng(lat, lng);
    }

    private float normalizeAngle(float degrees) {
        degrees = degrees % 360;
        return (degrees + 360) % 360;
    }

    private void animateBearing(final Marker marker, Location location) {
        float degrees = (float) location.getBearing().degrees();

        //Normalize angle
        degrees = normalizeAngle(degrees);
        final float toBearing = degrees;

        if (destinationBearing == toBearing) {
            return;
        }

        locationBearingAnimator.cancel();

        lastBearing =  normalizeAngle(lastBearing);

        //Avoid turning in the wrong direction
        if (lastBearing - toBearing > 180) {
            lastBearing -= 360;
        } else if (toBearing - lastBearing > 180) {
            lastBearing += 360;
        }

        float diffBearing = Math.abs(toBearing - lastBearing);
        if (diffBearing < BEARING_CHANGE_TO_ANIMATE) {
            return;
        }

        if (lastBearing != toBearing) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                //hardfix for crash in API 19 at PropertyValuesHolder.setupSetterAndGetter()
                marker.setRotation(toBearing);
            } else {

                locationBearingAnimator = new ObjectAnimator();
                locationBearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public synchronized void onAnimationUpdate(ValueAnimator animation) {
                        lastBearing = (Float) animation.getAnimatedValue();
                        marker.setRotation(lastBearing);
                    }
                });
                locationBearingAnimator.setFloatValues(lastBearing, toBearing);
                locationBearingAnimator.setDuration(DURATION_BEARING_ANIMATION);
                locationBearingAnimator.start();
            }
            destinationBearing = toBearing;
        }
    }

    private synchronized void animateGroundOverlay(final GroundOverlay groundOverlay, float toGroundOverlayDiameter) {
        if (destinationGroundOverlayDimensions == toGroundOverlayDiameter) {
            return;
        }

        groundOverlayAnimator.cancel();

        float diffDimensions = Math.abs(toGroundOverlayDiameter - lastGroundOverlayDimensions);
        if (diffDimensions < GROUND_OVERLAY_DIAMETER_CHANGE_TO_ANIMATE) {
            return;
        }
        if (lastGroundOverlayDimensions != toGroundOverlayDiameter) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                //hardfix for crash in API 19 at PropertyValuesHolder.setupSetterAndGetter()
                groundOverlay.setDimensions(toGroundOverlayDiameter);
            } else {

                groundOverlayAnimator = new ObjectAnimator();
                groundOverlayAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public synchronized void onAnimationUpdate(ValueAnimator animation) {
                        lastGroundOverlayDimensions = (Float) animation.getAnimatedValue();
                        groundOverlay.setDimensions(lastGroundOverlayDimensions);
                    }
                });
                groundOverlayAnimator.setFloatValues(lastGroundOverlayDimensions, toGroundOverlayDiameter);
                groundOverlayAnimator.setDuration(DURATION_RADIUS_ANIMATION);
                groundOverlayAnimator.start();
            }
            destinationGroundOverlayDimensions = toGroundOverlayDiameter;
        }
    }

    synchronized void clear() {
        lastLocation = null;
    }
}
