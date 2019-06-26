package es.situm.gettingstarted.samplelist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.situm.gettingstarted.R;

class SamplesAdapter
        extends RecyclerView.Adapter<SamplesAdapter.SampleViewHolder> {

    private final List<Sample> items;
    private final OnSampleSelectedCallback callback;

    SamplesAdapter(List<Sample> items, OnSampleSelectedCallback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public SampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_sample, parent, false);
        return new SampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleViewHolder holder, int position) {
        Sample sample = items.get(position);
        holder.bind(sample);

        holder.itemView.setOnClickListener(v -> callback.onSampleSelected(sample));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class SampleViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        SampleViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cell_sample_text);
        }

        void bind(Sample sample) {
            textView.setText(sample.getText());
        }
    }

    interface OnSampleSelectedCallback {
        void onSampleSelected(Sample sample);
    }


}
