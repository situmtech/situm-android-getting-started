package es.situm.gettingstarted.drawbuilding;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.situm.gettingstarted.R;
import es.situm.sdk.model.cartography.Floor;

/**
 * Created by adriannieto on 5/10/17.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    @Nullable
    private Floor selected = null;
    private List<Floor> floors;

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    FloorAdapter(List<Floor> floors, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        setFloors(floors);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    void setFloors(List<Floor> floors) {
        this.floors = floors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.situm_item_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Floor floor = floors.get(position);
        holder.bind(floor);
    }

    @Override
    public int getItemCount() {
        return floors.size();
    }

    public void select(Floor floor) {
        selected = floor;
        notifyDataSetChanged();
    }

    public int getSelectedFloorIndex() { return floors.indexOf(selected);}

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLevelNumber;

        ViewHolder(View itemView) {
            super(itemView);
            tvLevelNumber = itemView.findViewById(R.id.txt_level_number);
        }

        void bind(Floor floor) {
            tvLevelNumber.setText(FloorUtils.getNameOrLevel(floor));

            if (selected != null && selected.getIdentifier().equals(floor.getIdentifier())) {
                itemView.setBackgroundResource(R.drawable.situm_item_level_background_selected);
            } else {
                itemView.setBackgroundResource(R.drawable.situm_item_level_background);
            }

            itemView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(floor);
                selected = floor;
                notifyDataSetChanged();
            });
        }

    }

    interface OnItemClickListener {
        void onItemClick(Floor floor);
    }
}
