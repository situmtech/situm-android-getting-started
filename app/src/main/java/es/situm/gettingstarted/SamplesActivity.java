package es.situm.gettingstarted;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.situm.gettingstarted.buildingevents.BuildingListActivity;
import es.situm.gettingstarted.drawbuilding.DrawBuildingActivity;
import es.situm.gettingstarted.drawpois.DrawPoisActivity;
import es.situm.gettingstarted.drawposition.DrawPositionActivity;
import es.situm.gettingstarted.drawroute.SelectionBuildingListActivity;
import es.situm.gettingstarted.guideinstructions.SelectBuildingActivity;
import es.situm.gettingstarted.indooroutdoor.IndoorOutdoorActivity;
import es.situm.gettingstarted.poifiltering.ListBuildingsActivity;
import es.situm.gettingstarted.positioning.PositioningActivity;
import es.situm.gettingstarted.realtime.RealTimeActivity;

/**
 *
 * Created by alberto.penas on 13/06/17.
 */

public class SamplesActivity
        extends AppCompatActivity
        implements View.OnClickListener {


    private RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples);
        setup();
    }

    private void setup() {
        List<Sample> items = new ArrayList<>();
        items.add(new Sample("Indoor positioning", PositioningActivity.class));
        items.add(new Sample("Indoor-Outdoor positioning", IndoorOutdoorActivity.class));
        items.add(new Sample("Draw building over the map", DrawBuildingActivity.class));
        items.add(new Sample("Draw position over the map", DrawPositionActivity.class));
        items.add(new Sample("Draw POIs over the map", DrawPoisActivity.class));
        items.add(new Sample("Draw Route between two points over the map", SelectionBuildingListActivity.class));
        items.add(new Sample("Draw realtime devices over the map", RealTimeActivity.class));
        items.add(new Sample("Key-Value POIs Filtering", ListBuildingsActivity.class));
        items.add(new Sample("Show all the events of a building", BuildingListActivity.class));
        items.add(new Sample("Instructions while going to a destination", SelectBuildingActivity.class));
        items.add(new Sample("Animate the position while walking", es.situm.gettingstarted.animateposition.SelectBuildingActivity.class));
        items.add(new Sample("Show if user is inside an event", es.situm.gettingstarted.userinsideevent.ListBuildingsActivity.class));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SamplesActivity.this));
        recyclerView.setAdapter(new SamplesAdapter(items, this));
    }


    @Override
    public void onClick(View v) {
        Class clazz = (Class) v.getTag();
        startActivity(new Intent(SamplesActivity.this, clazz));
    }


    private static class SamplesAdapter
            extends RecyclerView.Adapter<SampleViewHolder>{


        private List<Sample> items;
        private View.OnClickListener onClickListener;


        SamplesAdapter(List<Sample> items, View.OnClickListener onClickListener) {
            this.items = items;
            this.onClickListener = onClickListener;
        }

        @Override
        public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.cell_sample, parent, false);
            return new SampleViewHolder(view, onClickListener);
        }

        @Override
        public void onBindViewHolder(SampleViewHolder holder, int position) {
            holder.fill(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class SampleViewHolder
            extends RecyclerView.ViewHolder {

        private TextView textView;

        SampleViewHolder(View itemView,
                         View.OnClickListener onClickListener) {
            super(itemView);
            itemView.setOnClickListener(onClickListener);
            textView = (TextView) itemView.findViewById(R.id.cell_sample_text);
        }

        void fill(Sample sample){
            itemView.setTag(sample.getClazz());
            textView.setText(sample.getText());
        }
    }
}
