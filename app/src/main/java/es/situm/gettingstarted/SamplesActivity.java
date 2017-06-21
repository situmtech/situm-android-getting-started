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

import es.situm.gettingstarted.drawbuilding.DrawBuildingActivity;
import es.situm.gettingstarted.positioning.PositioningActivity;

/**
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
        items.add(new Sample("Use indoor positioning", PositioningActivity.class));
        items.add(new Sample("Draw building over the map", DrawBuildingActivity.class));
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
