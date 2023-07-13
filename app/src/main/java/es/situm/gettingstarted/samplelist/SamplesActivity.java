package es.situm.gettingstarted.samplelist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.gettingstarted.drawbuilding.DrawBuildingActivity;
import es.situm.gettingstarted.drawpois.DrawPoisActivity;
import es.situm.gettingstarted.drawposition.DrawPositionActivity;
import es.situm.gettingstarted.drawroute.DrawRouteActivity;
import es.situm.gettingstarted.drawroutegeojson.DrawRouteGeojsonActivity;
import es.situm.gettingstarted.fetchresources.FetchResourcesActivity;
import es.situm.gettingstarted.guideinstructions.GuideInstructionsActivity;
import es.situm.gettingstarted.indooroutdoor.IndoorOutdoorActivity;
import es.situm.gettingstarted.poifiltering.ListBuildingsActivity;
import es.situm.gettingstarted.pointinsidegeofence.PointInsideGeofenceActivity;
import es.situm.gettingstarted.positioning.PositioningActivity;
import es.situm.gettingstarted.realtime.RealTimeActivity;
import es.situm.gettingstarted.updatelocationparams.UpdateLocationParamsActivity;
import es.situm.gettingstarted.usewayfinding.WayfindingActivity;

public class SamplesActivity
        extends AppCompatActivity
        implements SamplesAdapter.OnSampleSelectedCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples);

        List<Sample> items = new ArrayList<>();

        items.add(new Sample("Indoor positioning", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, PositioningActivity.class)));
        items.add(new Sample("Indoor-Outdoor positioning", new Intent(this, IndoorOutdoorActivity.class)));
        items.add(new Sample("Draw building over the map", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, DrawBuildingActivity.class)));
        items.add(new Sample("Draw position over the map", new Intent(this, DrawPositionActivity.class)));
        items.add(new Sample("Animate the position while walking", new Intent(this, es.situm.gettingstarted.animateposition.SelectBuildingActivity.class)));
        items.add(new Sample("Draw Route between two points over the map", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, DrawRouteActivity.class)));
        items.add(new Sample("Instructions while going to a destination", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, GuideInstructionsActivity.class)));
        items.add(new Sample("Draw GeoJSON route", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, DrawRouteGeojsonActivity.class)));
        items.add(new Sample("Draw POIs over the map", new Intent(this, DrawPoisActivity.class)));
        items.add(new Sample("Show if point is inside a geofence", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, PointInsideGeofenceActivity.class)));
        items.add(new Sample("Draw realtime devices over the map", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, RealTimeActivity.class)));
        items.add(new Sample("Key-Value POIs Filtering", new Intent(this, ListBuildingsActivity.class)));
        items.add(new Sample("Update location parameters on the fly", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, UpdateLocationParamsActivity.class)));
        items.add(new Sample("Fetch Resources using CommunicationManager", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, FetchResourcesActivity.class)));
        items.add(new Sample("Use Wayfinding", es.situm.gettingstarted.common.selectbuilding.SelectBuildingActivity.createIntent(this, WayfindingActivity.class)));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SamplesActivity.this));
        recyclerView.setAdapter(new SamplesAdapter(items, this));
    }

    @Override
    public void onSampleSelected(Sample sample) {
        startActivity(sample.getIntent());
    }

}
